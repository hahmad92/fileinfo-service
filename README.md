# FileInfo Service

## Overview
FileInfo Service is a Spring Boot application designed to manage file information operations. It provides APIs to list files in a directory, handle large numbers of files, and manage directories with different access permissions.

## Prerequisites
- JDK 17
- Maven 3.9.0
- Spring Boot 3.3.1

## Configuration
Before running the application, ensure the following configurations are set in `src/main/resources/application.properties`:

- `spring.application.name`: Set the application name.
- `server.port`: Configure the port on which the application will run.
- `server.tomcat.connection-timeout`: Set the connection timeout.
- `spring.mvc.async.request-timeout`: Configure the request timeout for asynchronous operations.
- Logging: Ensure `log4j2.xml` is configured for logging.

## Pushing Image on Nexus

For building docker image and pushing it to nexus use [build-image.sh](build-image.sh) file.
This script is extended with following tags.

| Tag | Description                                                                          |
|-----|--------------------------------------------------------------------------------------|
| -h  | shows brief help                                                                     |
| -s  | specify ServiceName to be used for kubectl                                           |
| -b  | if flag is true then mvn clean package deploy will be executed,default value is true |
| -t  | tag Name for image to be used for nexus deployment default tag is current GIT hash   |


For deploying image on nexus use following commands
```sh
    # Deployment with current git hash as tag
    bash build-image.sh -s fileinfo-service
    # Deployment with custom tag
    bash build-image.sh -s fileinfo-service -v latest
    # Deployment with custom tag and when no build is required
    bash build-image.sh -s ivr-versions -v latest -b false
```


## Running the Application
To run the application, use the following Maven command:
```bash
docker run -p 8080:8080 -d fileinfo-service:latest
```

# Api Documentation

## API Endpoints

### List Files in a Directory
* **Endpoint**: GET /listFiles/{directoryPath}
* **Description**: Lists all files in the specified directory path.
* **Response**: A list of file information objects or an error message if the directory does not exist or is inaccessible.

### Error Handling
* The service handles various errors, including non-existent directories, inaccessible directories due to permissions, and null or empty directory paths.

### Logging
* The application uses Log4j2 for logging. Trace ID and Span ID are included in the logs for tracing requests across microservices.

### Health Checks
* Health, readiness, and liveness probes are enabled for monitoring the application's health.

### Compression
* Response compression is enabled for specific MIME types to optimize performance.