# Spring Boot GraalVM Native Example

![Build Status](https://github.com/susimsek/spring-boot-graalvm-native-example/actions/workflows/deploy.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring-boot-graalvm-native-example&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=spring-boot-graalvm-native-example)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=spring-boot-graalvm-native-example&metric=coverage)](https://sonarcloud.io/summary/new_code?id=spring-boot-graalvm-native-example)
![Language](https://img.shields.io/badge/Language-Java-brightgreen)
![Framework](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)

Welcome to **Spring Boot GraalVM Native Example** – a lightweight and high-performance application built using Spring Boot and GraalVM Native Image.

Explore high-speed application startup, reduced memory footprint, and seamless integration with GraalVM. 🚀

## 🚀 Quick Links

- 📖 [Features](#-features)
- 🎥 [Demo Preview](#-demo-preview)
- 🧑‍💻 [Development Setup](#-development-setup)
- 🔄 [Live Reload](#-live-reload)
- 🧪 [Testing](#-testing)
- 🏗️ [Build](#️-build)
- 🕵️‍♂️ [Code Analysis](#️-code-analysis)
- 🛡️ [Code Quality](#️-code-quality)
- 📜 [API Documentation](#-api-documentation)
- 📚 [Code Documentation](#-code-documentation)
- 🐳 [Docker](#-docker)
- 🚀 [Deployment](#-deployment)
- 🛠️ [Used Technologies](#️-used-technologies)

## 📖 Features

- 🚀 **High Performance**: Powered by GraalVM Native Image for faster startup times and reduced memory footprint.
- 🌐 **Modern Spring Boot**: Utilizes the latest Spring Boot version, optimized for GraalVM compatibility.
- 🛠️ **Production-Ready**: Supports native builds for cloud-native and containerized deployments.
- 🔄 **Extensible**: Easily adaptable with the flexibility to add custom features.

## 🎥 Demo Preview

Below is a quick preview of the Application:

The application will be available at http://localhost:8080.

![Demo Preview](https://github.com/susimsek/spring-boot-graalvm-native-example/blob/main/images/webapp.png)

## 🧑‍💻 Development Setup

Before you begin, ensure you have the following installed:

- **Java 21**

To clone and run this application locally:

```bash
# Clone the repository
git clone https://github.com/susimsek/spring-boot-graalvm-native-example.git

# Navigate to the project directory
cd spring-boot-graalvm-native-example

# Run the application
./mvnw spring-boot:run
```

## 🔄 Live Reload

`Spring DevTools` provides live reload capabilities for Spring Boot applications.

### Enabling Live Reload

1. Add the `spring-boot-devtools` dependency to your `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-devtools</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. Restart the application automatically when code changes are detected.

## 🧪 Testing

To test the application:

```bash
curl -X GET http://localhost:8080/api/v1/hello
```

Expected response:

```json
{
  "message": "Hello, GraalVM Native Image!"
}
```

### Running Unit Tests

Run the following command to execute unit tests:

```bash
./mvnw -ntp verify
```

## 🏗️ Build

To build the application for production with GraalVM Native Image:

1. Ensure the following prerequisites:
  - **Java 21**
  - **GraalVM 22.3+** (ensure `native-image` is available)
  - **UPX** (optional, for executable compression)
2. Run the native image build:
   ```bash
   ./mvnw native:compile -B -ntp -Pnative,prod -DskipTests
   ```
3. The executable will be available as `target/native-executable`.

4. Optionally, compress the native executable using UPX for smaller file size:
   ```bash
   upx --ultra-brute --lzma target/native-executable
   ```

## 🕵️ Code Analysis

To check the code style for both Java and Kotlin, execute the following commands:

#### Java Code Style
```bash
mvn checkstyle:check
```
This will analyze the Java code style using Checkstyle.

#### Kotlin Code Style
```bash
mvn detekt:check -Ddetekt.config=detekt.yml
```
This will analyze the Kotlin code style using Detekt.

## 🛡️ Code Quality

To assess code quality locally using SonarQube, execute:

```bash
mvn -Psonar compile initialize sonar:sonar
```

## 📜 API Documentation

To view the API documentation, access the Swagger UI at:

http://localhost:8080/swagger-ui.html

## 📚 Code Documentation

To generate and view the Java and Kotlin documentation for this project:

### Generate Documentation

Run the following Maven commands to generate the documentation:

#### Java Documentation
```bash
mvn javadoc:javadoc
```
The generated Java documentation can be found under the `target/reports/apidocs` directory.

#### Kotlin Documentation
```bash
mvn dokka:dokka
```
The generated Kotlin documentation can be found under the `target/dokka` directory.

### Open Documentation

To view the documentation, open the following files in your browser:

#### Java Documentation
```text
target/reports/apidocs/index.html
```

#### Kotlin Documentation
```text
target/dokka/index.html
```


## 🐳 Docker

To build and run the application using Docker:

### Build Docker Image

```bash
docker build -t spring-boot-graalvm-samples .
```

### Run Docker Container

```bash
docker run -d -p 8080:8080 spring-boot-graalvm-samples
```

The application will be available at `http://localhost:8080`.

## 🚀 Deployment

### Docker Compose Deployment

To deploy the application using Docker Compose, run the following command:

```bash
docker-compose -f deploy/docker-compose/prod/docker-compose.yml up -d
```

To stop and remove the Docker Compose deployment:

```bash
docker-compose -f deploy/docker-compose/prod/docker-compose.yml down
```

### Kubernetes Deployment

To deploy the application on Kubernetes using Helm, run the following command:

```bash
helm install graalvm-native-app deploy/helm/graalvm-native-app
```

To uninstall the Helm deployment:

```bash
helm uninstall graalvm-native-app
```

## 🛠️ Used Technologies

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk&logoColor=white)  
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-7F52FF?logo=kotlin&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green?logo=spring&logoColor=white)  
![GraalVM](https://img.shields.io/badge/GraalVM-Native_Image-FF8C00?logo=graalvm&logoColor=white)  
![Spring Boot WebFlux](https://img.shields.io/badge/Spring_Boot_WebFlux-Reactive_Programming-6DB33F?logo=spring&logoColor=white)  
![Spring Boot Actuator](https://img.shields.io/badge/Spring_Boot_Actuator-Monitoring-green?logo=spring&logoColor=white)  
![Checkstyle](https://img.shields.io/badge/Checkstyle-Code_Analysis-orange?logo=openjdk&logoColor=white)  
![Detekt](https://img.shields.io/badge/Detekt-Code_Analysis-orange?logo=kotlin&logoColor=white)  
![Lombok](https://img.shields.io/badge/Lombok-Boilerplate_Code_Reduction-orange?logo=openjdk&logoColor=white)  
![Maven](https://img.shields.io/badge/Maven-Build_Automation-C71A36?logo=apachemaven&logoColor=white)  
![Springdoc](https://img.shields.io/badge/Springdoc-API_Documentation-6DB33F?logo=spring&logoColor=white)    
![Javadoc](https://img.shields.io/badge/Javadoc-Documentation-007396?logo=openjdk&logoColor=white)   
![Dokka](https://img.shields.io/badge/Dokka-Documentation-007396?logo=kotlin&logoColor=white)  
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?logo=sonarqube&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)  
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?logo=kubernetes&logoColor=white)  
![Helm](https://img.shields.io/badge/Helm-0F1689?logo=helm&logoColor=white)  
![UPX](https://img.shields.io/badge/UPX-Executable_Compression-0096D6?logo=upx&logoColor=white)  
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?logo=bootstrap&logoColor=white)    
![Font Awesome](https://img.shields.io/badge/Font_Awesome-6.0-339AF0?logo=fontawesome&logoColor=white)  
![WebJars Locator Lite](https://img.shields.io/badge/WebJars_Locator_Lite-Dynamic_Asset_Locator-007396?logo=java&logoColor=white)
