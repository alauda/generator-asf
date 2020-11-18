FROM openjdk:8-alpine

COPY target/*.jar /app/app.jar

COPY docker-entrypoint.sh /app/docker-entrypoint.sh

RUN chmod +x /app/docker-entrypoint.sh

ENTRYPOINT ["sh","/app/docker-entrypoint.sh"]
