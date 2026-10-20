#!/usr/bin/env bash
# End-to-end smoke test against a RUNNING EthioMentor (real HTTP, real database).
#
#   ADMIN_PASSWORD='...' scripts/smoke-test.sh [BASE_URL]
#
# BASE_URL defaults to http://localhost:8080/EthioMentor. ADMIN_PASSWORD is the password of the
# 'admin' account (see README); without it the admin checks are skipped.
# The script creates throw-away users/groups (names contain a timestamp) in the target database,
# so point it at a development database, not at production data.
set -uo pipefail

BASE="${1:-http://localhost:8080/EthioMentor}"
BASE="${BASE%/}"
TAG="$(date +%s)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
PASS_USER="Passw0rd-Smoke"
pass=0; fail=0; skipped=0

ok()   { pass=$((pass+1)); printf '  PASS  %s\n' "$1"; }
bad()  { fail=$((fail+1)); printf '  FAIL  %s\n' "$1"; }
check() { if [[ "$2" == "$3" ]]; then ok "$1"; else bad "$1  (expected '$3', got '$2')"; fi; }
contains()     { if grep -qF -- "$3" "$2"; then ok "$1"; else bad "$1  (missing: $3)"; fi; }
not_contains() { if grep -qF -- "$3" "$2"; then bad "$1  (found: $3)"; else ok "$1"; fi; }

# http <jar> <outfile> <curl args...>  -> prints the status code
http() { local jar="$1" out="$2"; shift 2
  curl -s -o "$out" -w '%{http_code}' -b "$jar" -c "$jar" "$@"; }
loc()  { grep -i '^location:' "$1" | tr -d '\r' | awk '{print $2}'; }
# status + Location header of a no-follow request
redirect_of() { local jar="$1"; shift; curl -s -o /dev/null -D "$TMP/h" -w '%{http_code}' -b "$jar" -c "$jar" "$@"; }
json_field() { python3 -c "import sys,json; print(json.load(open('$1')).get('$2',''))"; }

register() { # name role [expertise]
  local jar="$TMP/$1.jar"
  http "$jar" "$TMP/reg.out" -X POST "$BASE/register" \
    --data-urlencode "username=$1$TAG" --data-urlencode "fullName=Smoke $1 $TAG" \
    --data-urlencode "email=$1$TAG@smoke.test" --data-urlencode "password=$PASS_USER" \
    --data-urlencode "role=$2" --data-urlencode "uni=Addis Ababa University" \
    --data-urlencode "expertise=${3:-}" -D "$TMP/reg.h" >/dev/null
}
login() { # jar identifier password -> status
  http "$1" "$TMP/login.out" -X POST "$BASE/login" -D "$TMP/login.h" \
    --data-urlencode "email=$2" --data-urlencode "password=$3"
}

echo "Smoke test against $BASE"

echo "== public pages"
c=$(http "$TMP/anon.jar" "$TMP/o" "$BASE/");                check "landing page 200" "$c" 200
c=$(http "$TMP/anon.jar" "$TMP/login.html" "$BASE/login.jsp"); check "login page 200" "$c" 200
c=$(http "$TMP/anon.jar" "$TMP/reg.html" "$BASE/register.jsp"); check "register page 200" "$c" 200
not_contains "register.jsp has no merge-conflict markers" "$TMP/reg.html" '<<<<<<<'
c=$(http "$TMP/anon.jar" "$TMP/o" "$BASE/css/global.css");  check "static CSS served" "$c" 200
c=$(http "$TMP/anon.jar" "$TMP/o" "$BASE/js/chat.js");      check "static JS served" "$c" 200

echo "== anonymous access is refused on the server"
for p in /dashboard /admin /admin.jsp /studygroups /mentor-dashboard /chat.jsp; do
  c=$(redirect_of "$TMP/anon.jar" "$BASE$p"); check "GET $p -> 302 login" "$c $(loc "$TMP/h" | sed 's#.*/##')" "302 login.jsp"
done
for p in /api/user "/loadMessages?conversationId=1" /mentormatching; do
  c=$(http "$TMP/anon.jar" "$TMP/o" "$BASE$p"); check "GET $p -> 401" "$c" 401
done
c=$(http "$TMP/anon.jar" "$TMP/o" -X POST "$BASE/studygroups/create" -d 'name=x'); check "anonymous POST /studygroups/create refused" "$c" 401

echo "== registration"
register stud student
check "student registration redirects to /dashboard" "$(loc "$TMP/reg.h" | sed 's#.*/EthioMentor##')" "/dashboard"
register ment mentor "Java, Databases"
check "mentor registration redirects to /mentor-dashboard" "$(loc "$TMP/reg.h" | sed 's#.*/EthioMentor##')" "/mentor-dashboard"
register third student
register stud student            # duplicate
contains "duplicate username/email rejected with a message" "$TMP/reg.out" "already registered"
c=$(http "$TMP/w.jar" "$TMP/weak.out" -X POST "$BASE/register" --data-urlencode "username=weak$TAG" --data-urlencode "fullName=W" \
    --data-urlencode "email=weak$TAG@smoke.test" --data-urlencode "password=short" --data-urlencode "role=student" --data-urlencode "uni=AAU")
contains "weak password rejected" "$TMP/weak.out" "Password must be"
c=$(http "$TMP/esc.jar" "$TMP/o" -X POST "$BASE/register" --data-urlencode "username=adm$TAG" --data-urlencode "fullName=Evil" \
    --data-urlencode "email=adm$TAG@smoke.test" --data-urlencode "password=$PASS_USER" --data-urlencode "role=ADMIN" --data-urlencode "uni=AAU")
c=$(http "$TMP/esc.jar" "$TMP/o" "$BASE/admin"); check "registering with role=ADMIN does not grant admin (403)" "$c" 403

echo "== login / logout / roles"
c=$(login "$TMP/bad.jar" "stud$TAG" "wrong-password"); check "wrong password -> 401 with the login page" "$c" 401
contains "wrong password shows an error" "$TMP/login.out" "Invalid credentials"
c=$(login "$TMP/s.jar" "stud$TAG" "$PASS_USER"); check "student login redirects (302)" "$c" 302
c=$(http "$TMP/s.jar" "$TMP/dash.html" "$BASE/dashboard"); check "student dashboard 200" "$c" 200
contains "dashboard greets by first name" "$TMP/dash.html" "Selam"
c=$(http "$TMP/s.jar" "$TMP/o" "$BASE/api/message");       check "removed insecure /api/message endpoint is gone (404)" "$c" 404
c=$(http "$TMP/s.jar" "$TMP/o" "$BASE/api/group");         check "removed insecure /api/group endpoint is gone (404)" "$c" 404
c=$(http "$TMP/s.jar" "$TMP/o" "$BASE/admin");             check "student cannot open /admin (403)" "$c" 403
c=$(http "$TMP/s.jar" "$TMP/o" "$BASE/admin.jsp");         check "student cannot open /admin.jsp directly (403)" "$c" 403
c=$(http "$TMP/s.jar" "$TMP/o" "$BASE/mentor-dashboard");  check "student cannot open mentor dashboard (403)" "$c" 403
c=$(login "$TMP/m.jar" "ment$TAG@smoke.test" "$PASS_USER"); check "mentor logs in by e-mail (302)" "$c" 302
c=$(http "$TMP/m.jar" "$TMP/mdash.html" "$BASE/mentor-dashboard"); check "mentor dashboard 200" "$c" 200
c=$(login "$TMP/t.jar" "third$TAG" "$PASS_USER");          check "third user logs in (302)" "$c" 302
c=$(http "$TMP/s.jar" "$TMP/h.txt" -D "$TMP/hdr" "$BASE/dashboard")
grep -qi 'x-content-type-options: nosniff' "$TMP/hdr" && ok "security headers present" || bad "security headers missing"
grep -qi 'set-cookie:.*HttpOnly' "$TMP/hdr" || curl -s -D "$TMP/hdr2" -o /dev/null -X POST "$BASE/login" -d "email=stud$TAG&password=$PASS_USER"
grep -qi 'set-cookie:.*HttpOnly' "$TMP/hdr2" 2>/dev/null && ok "session cookie is HttpOnly" || bad "session cookie not HttpOnly"
grep -qi 'set-cookie:.*SameSite=Lax' "$TMP/hdr2" 2>/dev/null && ok "session cookie is SameSite=Lax" || bad "session cookie lacks SameSite=Lax"

echo "== study groups (CSRF, escaping, membership)"
c=$(http "$TMP/s.jar" "$TMP/sg.html" "$BASE/studygroups"); check "study groups page 200" "$c" 200
CSRF_S=$(grep -o 'name="csrf-token" content="[^"]*"' "$TMP/sg.html" | sed 's/.*content="//; s/"$//')
[[ -n "$CSRF_S" ]] && ok "page carries a CSRF token" || bad "no CSRF token in page"
c=$(http "$TMP/s.jar" "$TMP/o" -X POST "$BASE/studygroups/create" --data-urlencode 'name=NoToken' --data-urlencode 'description=d'); check "POST without CSRF token refused (403)" "$c" 403
c=$(http "$TMP/s.jar" "$TMP/o" -X POST "$BASE/studygroups/create" -H 'X-CSRF-Token: wrong' --data-urlencode 'name=BadToken' --data-urlencode 'description=d'); check "POST with wrong CSRF token refused (403)" "$c" 403
c=$(http "$TMP/s.jar" "$TMP/cg.out" -X POST "$BASE/studygroups/create" -H "X-CSRF-Token: $CSRF_S" \
    --data-urlencode "name=Group $TAG <b>bold</b>" --data-urlencode 'description=Study <script>alert(1)</script> together'); check "create group with token (201)" "$c" 201
c=$(http "$TMP/s.jar" "$TMP/sg2.html" "$BASE/studygroups")
contains "group listed" "$TMP/sg2.html" "Group $TAG"
not_contains "group name is HTML-escaped (no raw <b>)" "$TMP/sg2.html" "<b>bold</b>"
not_contains "group description is HTML-escaped (no raw <script>alert)" "$TMP/sg2.html" "<script>alert(1)"
contains "creator is a member (1 members)" "$TMP/sg2.html" "1 members"
c=$(http "$TMP/s.jar" "$TMP/o" -X POST "$BASE/studygroups/create" -H "X-CSRF-Token: $CSRF_S" --data-urlencode 'name=' --data-urlencode 'description=x'); check "empty group name rejected (400)" "$c" 400
# Amharic round trip (request/response are UTF-8)
http "$TMP/s.jar" "$TMP/o" -X POST "$BASE/studygroups/create" -H "X-CSRF-Token: $CSRF_S" --data-urlencode "name=ቡድን $TAG" --data-urlencode 'description=ጥናት' >/dev/null
http "$TMP/s.jar" "$TMP/sg3.html" "$BASE/studygroups" >/dev/null
contains "Amharic group name survives the round trip" "$TMP/sg3.html" "ቡድን $TAG"
# join from the mentor session
http "$TMP/m.jar" "$TMP/msg.html" "$BASE/studygroups" >/dev/null
CSRF_M=$(grep -o 'name="csrf-token" content="[^"]*"' "$TMP/msg.html" | sed 's/.*content="//; s/"$//')
GID=$(grep -o 'joinGroup([0-9]*' "$TMP/msg.html" | head -1 | grep -o '[0-9]*$')
c=$(http "$TMP/m.jar" "$TMP/join.out" -X POST "$BASE/studygroups/join" -H "X-CSRF-Token: $CSRF_M" -d "groupId=$GID"); check "mentor joins group (200)" "$c" 200
check "member count incremented to 2 (was always 0 before the fix)" "$(json_field "$TMP/join.out" membersCount)" "2"
http "$TMP/m.jar" "$TMP/join2.out" -X POST "$BASE/studygroups/join" -H "X-CSRF-Token: $CSRF_M" -d "groupId=$GID" >/dev/null
check "joining twice does not double-count" "$(json_field "$TMP/join2.out" membersCount)" "2"
c=$(http "$TMP/m.jar" "$TMP/o" -X POST "$BASE/studygroups/join" -H "X-CSRF-Token: $CSRF_M" -d "groupId=99999999"); check "joining an unknown group -> 404" "$c" 404
c=$(http "$TMP/m.jar" "$TMP/o" -X POST "$BASE/studygroups/join" -H "X-CSRF-Token: $CSRF_M" -d "groupId=abc"); check "bad groupId -> 400" "$c" 400

echo "== profile API and password change (double-hash regression)"
c=$(http "$TMP/s.jar" "$TMP/me.json" "$BASE/api/user"); check "GET /api/user 200" "$c" 200
not_contains "profile JSON never contains a password/hash" "$TMP/me.json" "assword"
SID=$(json_field "$TMP/me.json" id)
c=$(http "$TMP/s.jar" "$TMP/o" -X PUT "$BASE/api/user" -H 'Content-Type: application/json' -d '{"fullName":"No Token"}'); check "PUT without CSRF token refused (403)" "$c" 403
c=$(http "$TMP/s.jar" "$TMP/o" -X PUT "$BASE/api/user" -H "X-CSRF-Token: $CSRF_S" -H 'Content-Type: application/json' -d '{"fullName":"Renamed Student"}'); check "rename (200)" "$c" 200
c=$(login "$TMP/s2.jar" "stud$TAG" "$PASS_USER"); check "still able to log in after a profile edit" "$c" 302
c=$(http "$TMP/s.jar" "$TMP/o" -X PUT "$BASE/api/user" -H "X-CSRF-Token: $CSRF_S" -H 'Content-Type: application/json' -d '{"password":"N3w-Passw0rd-x"}'); check "change password (200)" "$c" 200
c=$(login "$TMP/s3.jar" "stud$TAG" "$PASS_USER");        check "old password no longer works (401)" "$c" 401
c=$(login "$TMP/s4.jar" "stud$TAG" "N3w-Passw0rd-x");    check "new password works" "$c" 302
c=$(http "$TMP/s4.jar" "$TMP/o" -X PUT "$BASE/api/user" -H "X-CSRF-Token: $(grep -o 'name="csrf-token" content="[^"]*"' <(curl -s -b "$TMP/s4.jar" "$BASE/studygroups") | sed 's/.*content="//; s/"$//')" -H 'Content-Type: application/json' -d '{"email":"nope"}'); check "invalid e-mail rejected (400)" "$c" 400
PASS_STUD="N3w-Passw0rd-x"

echo "== chat isolation (HTTP side)"
c=$(http "$TMP/m.jar" "$TMP/mme.json" "$BASE/api/user"); MID=$(json_field "$TMP/mme.json" id)
c=$(http "$TMP/s4.jar" "$TMP/chat.html" "$BASE/chat.jsp?userId=$MID"); check "student opens chat with mentor (200)" "$c" 200
CONV=$(grep -o 'CONVERSATION_ID = [0-9]*' "$TMP/chat.html" | grep -o '[0-9]*$')
[[ -n "$CONV" ]] && ok "conversation id $CONV created" || bad "no conversation id in chat page"
not_contains "chat page has no inline message dump (</script> injection vector removed)" "$TMP/chat.html" "CHAT_HISTORY"
c=$(http "$TMP/s4.jar" "$TMP/o" "$BASE/loadMessages?conversationId=$CONV"); check "participant can load history (200)" "$c" 200
c=$(http "$TMP/t.jar" "$TMP/o" "$BASE/loadMessages?conversationId=$CONV"); check "NON-participant cannot read it (403)" "$c" 403
c=$(http "$TMP/t.jar" "$TMP/o" "$BASE/loadMessages?conversationId=abc"); check "malformed conversationId -> 400" "$c" 400
c=$(http "$TMP/t.jar" "$TMP/o" "$BASE/chat.jsp?userId=999999999"); [[ "$c" == 400 || "$c" == 404 ]] && ok "chat with unknown user refused ($c)" || bad "chat with unknown user -> $c"

echo "== admin"
if [[ -z "${ADMIN_PASSWORD:-}" ]]; then
  skipped=$((skipped+1)); echo "  SKIP  admin checks (set ADMIN_PASSWORD to run them)"
else
  c=$(login "$TMP/a.jar" "admin" "$ADMIN_PASSWORD"); check "admin login (302)" "$c" 302
  c=$(http "$TMP/a.jar" "$TMP/admin.html" "$BASE/admin"); check "admin page 200" "$c" 200
  contains "admin sees the pending mentor" "$TMP/admin.html" "Smoke ment $TAG"
  CSRF_A=$(grep -o 'name="_csrf" value="[^"]*"' "$TMP/admin.html" | head -1 | sed 's/.*value="//; s/"$//')
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" -d "action=approveMentor&id=$MID"); check "admin POST without CSRF token refused (403)" "$c" 403
  # before approval: mentor is not listed to students
  http "$TMP/s4.jar" "$TMP/mm0.json" "$BASE/mentormatching" >/dev/null
  not_contains "unapproved mentor is hidden from mentor matching" "$TMP/mm0.json" "Smoke ment $TAG"
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A" -d "action=approveMentor&id=$MID"); check "approve mentor (redirect 302)" "$c" 302
  http "$TMP/s4.jar" "$TMP/mm1.json" "$BASE/mentormatching" >/dev/null
  contains "approved mentor appears in mentor matching" "$TMP/mm1.json" "Smoke ment $TAG"
  contains "mentor expertise saved at registration is shown" "$TMP/mm1.json" "Databases"
  not_contains "mentor matching JSON does not leak e-mail addresses" "$TMP/mm1.json" "@smoke.test"
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A" -d "action=nonsense&id=1"); [[ "$c" == 302 || "$c" == 400 ]] && ok "unknown admin action is harmless ($c)" || bad "unknown admin action -> $c"
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A"); [[ "$c" == 302 || "$c" == 400 ]] && ok "missing action/id does not crash (was NullPointerException) ($c)" || bad "missing action -> $c"
  # deactivate the third user: their next request is refused, they cannot log in again
  http "$TMP/t.jar" "$TMP/tme.json" "$BASE/api/user" >/dev/null; TID=$(json_field "$TMP/tme.json" id)
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A" -d "action=deactivateUser&id=$TID"); check "deactivate user (302)" "$c" 302
  c=$(login "$TMP/t2.jar" "third$TAG" "$PASS_USER"); check "deactivated user cannot log in (401)" "$c" 401
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A" -d "action=activateUser&id=$TID"); check "reactivate user (302)" "$c" 302
  c=$(login "$TMP/t3.jar" "third$TAG" "$PASS_USER"); check "reactivated user can log in (302)" "$c" 302
  c=$(http "$TMP/a.jar" "$TMP/o" -X POST "$BASE/admin" --data-urlencode "_csrf=$CSRF_A" -d "action=deleteUser&id=$TID"); check "delete user (302)" "$c" 302
fi

echo "== logout"
c=$(redirect_of "$TMP/s4.jar" "$BASE/logout"); check "logout redirects (302)" "$c" 302
c=$(redirect_of "$TMP/s4.jar" "$BASE/dashboard"); check "session is dead after logout (302 to login)" "$c" 302

echo
echo "Result: $pass passed, $fail failed, $skipped skipped"
[[ $fail -eq 0 ]]
