#!/usr/bin/env bash
# Builds EthioMentor without Maven/Gradle: needs only a JDK (17 or newer) and a Tomcat 9 install
# (for the servlet / websocket API jars, which are *not* bundled in the WAR).
#
#   ./build.sh            compile + package  ->  build/EthioMentor.war
#   ./build.sh classes    compile only       ->  build/classes
#   ./build.sh clean      remove build/
#
# Environment:
#   CATALINA_HOME   Tomcat 9 directory (its lib/ holds servlet-api.jar, websocket-api.jar, ...)
#   SERVLET_CP      alternative: a classpath that already contains the servlet + websocket APIs
#   JAVA_RELEASE    Java language level to compile for (default 17)
set -euo pipefail
cd "$(dirname "$0")"

APP=EthioMentor
LIB=src/main/webapp/WEB-INF/lib
OUT=build
RELEASE="${JAVA_RELEASE:-17}"

if [[ "${1:-}" == "clean" ]]; then rm -rf "$OUT"; echo "removed $OUT/"; exit 0; fi

command -v javac >/dev/null || { echo "javac not found - install a JDK (e.g. 'sudo apt install openjdk-17-jdk')" >&2; exit 1; }

# ---- locate the servlet API -------------------------------------------------
if [[ -z "${SERVLET_CP:-}" ]]; then
  for home in "${CATALINA_HOME:-}" /opt/tomcat9 /opt/tomcat /usr/share/tomcat9 /usr/local/tomcat; do
    if [[ -n "$home" && -f "$home/lib/servlet-api.jar" ]]; then CATALINA_HOME="$home"; break; fi
  done
  if [[ -z "${CATALINA_HOME:-}" || ! -f "$CATALINA_HOME/lib/servlet-api.jar" ]]; then
    echo "Cannot find Tomcat's servlet-api.jar. Set CATALINA_HOME to your Tomcat 9 directory," >&2
    echo "e.g.  export CATALINA_HOME=\$HOME/apache-tomcat-9.0.98" >&2
    exit 1
  fi
  SERVLET_CP=""
  for j in servlet-api websocket-api jsp-api el-api annotations-api; do
    [[ -f "$CATALINA_HOME/lib/$j.jar" ]] && SERVLET_CP="$SERVLET_CP:$CATALINA_HOME/lib/$j.jar"
  done
  SERVLET_CP="${SERVLET_CP#:}"
fi

APP_CP="$SERVLET_CP"
for j in "$LIB"/*.jar; do APP_CP="$APP_CP:$j"; done

# ---- compile ----------------------------------------------------------------
rm -rf "$OUT/classes"; mkdir -p "$OUT/classes"
find src/main/java -name '*.java' > "$OUT/sources.txt"
echo "Compiling $(wc -l < "$OUT/sources.txt") source files (release $RELEASE)..."
javac -encoding UTF-8 -proc:none -Xlint:-options --release "$RELEASE" -cp "$APP_CP" -d "$OUT/classes" @"$OUT/sources.txt"
echo "Compiled -> $OUT/classes"

[[ "${1:-}" == "classes" ]] && exit 0

# ---- package ----------------------------------------------------------------
STAGE="$OUT/war"
rm -rf "$STAGE"; mkdir -p "$STAGE"
cp -r src/main/webapp/. "$STAGE/"
mkdir -p "$STAGE/WEB-INF/classes"
cp -r "$OUT/classes/." "$STAGE/WEB-INF/classes/"
[[ -d src/main/resources ]] && cp -r src/main/resources/. "$STAGE/WEB-INF/classes/"
( cd "$STAGE" && jar cf "../$APP.war" . )
echo "Packaged -> $OUT/$APP.war  ($(du -h "$OUT/$APP.war" | cut -f1))"
