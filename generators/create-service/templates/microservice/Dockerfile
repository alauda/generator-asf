FROM openjdk:8-alpine
ARG JAR_FILE=target/*.jar
ENV MICRO_SERVICE_NAME <%= PROJECT_NAME %>
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

