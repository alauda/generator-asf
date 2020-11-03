FROM java:openjdk-8u212-alpine

COPY target/*.jar /app/app.jar

COPY docker-entrypoint.sh /app/docker-entrypoint.sh

RUN chmod +x /app/docker-entrypoint.sh

ENTRYPOINT ["/app/docker-entrypoint.sh"]