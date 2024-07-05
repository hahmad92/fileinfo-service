## Build stage
#FROM maven:3.8.4-amazoncorretto-17 as builder
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
##Change name of jar file
#
#RUN mvn clean package -DskipTests
#

# Run stage
FROM amazoncorretto:17-alpine
USER 10000
ADD /target/fileinfo-service-1.0.0.jar fileinfo-service-1.0.0.jar
EXPOSE 8080
CMD ["java", "-jar", "fileinfo-service-1.0.0.jar"]
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1

