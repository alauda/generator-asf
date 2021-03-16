FROM openjdk:8-alpine
ARG JAR_FILE=target/*.jar
ENV MICRO_SERVICE_NAME <%= PROJECT_NAME %>
ENV JAVA_OPTS ""
COPY ${JAR_FILE} app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
