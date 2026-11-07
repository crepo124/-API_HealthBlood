FROM tomcat:11-jdk21-temurin

# Despliega la API en el contexto raiz: /api/...
RUN rm -rf /usr/local/tomcat/webapps/*
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/ROOT.war

# Entrypoint que adapta el puerto de Tomcat a la variable PORT de Render.
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Render limita la memoria a 512 MB en el plan Free.
ENV CATALINA_OPTS="-Xmx256m -Dfile.encoding=UTF-8"

EXPOSE 8080

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
