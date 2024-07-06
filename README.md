# FileInfo Service

## Overview

FileInfo Service is a Spring Boot application designed to manage file information operations. It provides APIs to list
files in a directory, handle large numbers of files, and manage directories with different access permissions.

## Prerequisites

- JDK 17
- Maven 3.9.0
- Spring Boot 3.3.1

## Configuration

Before running the application, ensure the following configurations are set
in `src/main/resources/application.properties`:

- `spring.application.name`: Set the application name.
- `server.port`: Configure the port on which the application will run.
- `server.tomcat.connection-timeout`: Set the connection timeout.
- `spring.mvc.async.request-timeout`: Configure the request timeout for asynchronous operations.
- Logging: Ensure `log4j2.xml` is configured for logging.

## Pushing Image on

For building docker image use [build-image.sh](build-image.sh) file.
This script is extended with following tags.

| Tag | Description                                                                          |
|-----|--------------------------------------------------------------------------------------|
| -h  | shows brief help                                                                     |
| -s  | specify ServiceName to be used for kubectl                                           |
| -b  | if flag is true then mvn clean package deploy will be executed,default value is true |
| -t  | tag Name for image to be used for build default tag is latest GIT hash               |

### For Building image use following commands

* #### Build with latest as tag

```sh
bash build-image.sh -s fileinfo-service
 ```

* #### Deployment with custom tag

```sh
bash build-image.sh -s fileinfo-service -v latest
```  

* #### Deployment with custom tag and when no build is required

```sh    
bash build-image.sh -s fileinfo-service -v latest -b false
```    

## Running the Application

To run the application on docker, use the following docker command:

```bash
docker run -p 8080:8080 -d fileinfo-service:latest
```
To mount test directory use the following command:

```bash
docker run -p 8080:8080 -v /d/temp:/home/appuser/temp -d fileinfo-service:latest
```

# Api Documentation

## API Endpoints

### List Files in a Directory

* **Endpoint**: GET /listFiles/{directoryPath}
* **Description**: Lists all files in the specified directory path.
* **Response**: 
  * A list of file information objects with the following attributes:
    * `path`: The file path.
    * `directory`: A boolean value indicating whether the file is a directory.
    * `sz`: The file size in bytes.
    * `ct`: The creation time of the file in milliseconds since the epoch.
    * `lmt`: The last modified time of the file in milliseconds since the epoch.
  * Or an error message if the directory does not exist or is
    inaccessible.
  * **Example**: `http://localhost:8080/listFiles/C:/Users`
  * **Response Example**:
    ```json
    [
      {
      "path": "D:\\temp2\\file0.txt",
      "directory": false,
      "sz": 0,
      "ct": 1720104035538,
      "lmt": 1720104035538
    }
    ]
    ```

### Error Handling

* The service handles various errors, including non-existent directories, inaccessible directories due to permissions,
  and null or empty directory paths.

### Logging

* The application uses Log4j2 for logging.
* To retain logs, the application logs are written to a file. you can bind the volume to the container to access the logs from the host machine.

### Health Checks

* Health, readiness, and liveness probes are enabled for monitoring the application's health.

### Compression

* Response compression is enabled for specific MIME types to optimize performance.