#!/bin/sh
set -e

# Render inyecta la variable PORT; en local se usa 8080.
export PORT="${PORT:-8080}"

# Ajusta el conector HTTP de Tomcat al puerto indicado.
sed -i "s/port=\"8080\"/port=\"${PORT}\"/" /usr/local/tomcat/conf/server.xml

exec catalina.sh run
