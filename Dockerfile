FROM openjdk:11-jdk

COPY target/swen-quiz-backend-1.0.0-spring-boot.jar quiz-app.jar

ARG DB_CONNECTION_STRING


ENV DB_CONNECTION_STRING=$DB_CONNECTION_STRING

ENTRYPOINT ["java","-jar","/quiz-app.jar"]
EXPOSE 808
WEBSITES_PORT 808
