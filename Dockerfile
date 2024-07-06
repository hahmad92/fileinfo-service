FROM eclipse-temurin:21-jdk-alpine

# Create a non root group and user for the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set the working directory
WORKDIR /home/appuser/app

# Change ownership of the working directory
RUN chown -R appuser:appgroup /home/appuser/app

#Create a directory for logs
RUN mkdir -p /home/appuser/app/log/file-service
RUN chown -R appuser:appgroup /home/appuser/app/log/file-service && chmod -R 777 /home/appuser/app/log/file-service

# Copy the jar file into the image
COPY --chown=appuser:appgroup target/fileinfo-service-1.0.0.jar /home/appuser/app/fileinfo-service-1.0.0.jar

# Use the non-root user to run the application
USER appuser

EXPOSE 8080
CMD ["java", "-jar", "/home/appuser/app/fileinfo-service-1.0.0.jar"]
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1