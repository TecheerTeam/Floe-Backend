# Backend Dockerfile
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY floe/build/libs/floe-0.0.1-SNAPSHOT-plain.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
