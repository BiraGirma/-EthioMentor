#!/usr/bin/env bash
# Runs the automated tests (no JUnit needed - the tests are plain Java programs).
#
#   scripts/test.sh          unit tests only (no database, no Tomcat)
#   scripts/test.sh --db     also the database tests (needs PostgreSQL, see below)
#
# --db uses DB_HOST / DB_PORT / DB_USER / DB_PASSWORD, but ALWAYS works on scratch databases
# named ethiomentor_test and ethiomentor_migration_test (dropped and recreated). Your real
# database is never touched. The DB user therefore needs the CREATEDB privilege, and
# 'psql' must be installed (sudo apt install postgresql-client).
set -euo pipefail
cd "$(dirname "$0")/.."

./build.sh classes
LIB=src/main/webapp/WEB-INF/lib
CP="build/classes"
for j in "$LIB"/*.jar; do CP="$CP:$j"; done
if [[ -n "${SERVLET_CP:-}" ]]; then CP="$CP:$SERVLET_CP"; else
  for j in servlet-api websocket-api jsp-api el-api; do
    [[ -n "${CATALINA_HOME:-}" && -f "$CATALINA_HOME/lib/$j.jar" ]] && CP="$CP:$CATALINA_HOME/lib/$j.jar"
  done
fi

rm -rf build/test-classes; mkdir -p build/test-classes
find src/test/java -name '*.java' > build/test-sources.txt
javac -encoding UTF-8 -proc:none -Xlint:-options --release "${JAVA_RELEASE:-17}" -cp "$CP" -d build/test-classes @build/test-sources.txt
TCP="build/test-classes:$CP"

status=0
java -cp "$TCP" com.ethiomentor.test.UnitTests || status=1

if [[ "${1:-}" == "--db" ]]; then
  : "${DB_PASSWORD:?set DB_PASSWORD for the database tests}"
  export DB_HOST="${DB_HOST:-localhost}" DB_PORT="${DB_PORT:-5432}" DB_USER="${DB_USER:-postgres}"
  export PGPASSWORD="$DB_PASSWORD"
  psql_() { psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -v ON_ERROR_STOP=1 -q "$@"; }

  echo; echo "== integration tests (scratch DB ethiomentor_test)"
  psql_ -d postgres -c "DROP DATABASE IF EXISTS ethiomentor_test" -c "CREATE DATABASE ethiomentor_test"
  DB_NAME=ethiomentor_test ADMIN_INITIAL_PASSWORD=Adm1n-Test-Pass \
    java -cp "$TCP" com.ethiomentor.test.DbIntegrationTest || status=1

  echo; echo "== migration test (scratch DB ethiomentor_migration_test, OLD schema)"
  psql_ -d postgres -c "DROP DATABASE IF EXISTS ethiomentor_migration_test" -c "CREATE DATABASE ethiomentor_migration_test"
  psql_ -d ethiomentor_migration_test -f src/test/resources/old-schema.sql
  DB_NAME=ethiomentor_migration_test \
    java -cp "$TCP" com.ethiomentor.test.MigrationTest || status=1

  psql_ -d postgres -c "DROP DATABASE IF EXISTS ethiomentor_test" -c "DROP DATABASE IF EXISTS ethiomentor_migration_test"
fi

echo; [[ $status -eq 0 ]] && echo "ALL TESTS PASSED" || echo "SOME TESTS FAILED"
exit $status
