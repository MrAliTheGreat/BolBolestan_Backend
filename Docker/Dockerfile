# Multi-stage

# Stage 1: Build
FROM maven:3.8.1-openjdk-15 AS MAVEN
MAINTAINER MrAliTheGreat
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package


# Stage 2: Package and run
FROM openjdk:11.0.11-jre-slim
WORKDIR /app
COPY --from=MAVEN /build/target/*.war /app/app.war
EXPOSE 8080
ENTRYPOINT ["java" , "-jar" , "app.war"]