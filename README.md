# Spring Boot GraalVM Native Example

![Build Status](https://github.com/susimsek/spring-boot-graalvm-native-example/actions/workflows/deploy.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=spring-boot-graalvm-native-example&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=spring-boot-graalvm-native-example)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=spring-boot-graalvm-native-example&metric=coverage)](https://sonarcloud.io/summary/new_code?id=spring-boot-graalvm-native-example)
![Top Language](https://img.shields.io/github/languages/top/susimsek/spring-boot-graalvm-native-example)

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
- 🐳 [Docker](#-docker)
- 🛠️ [Used Technologies](#️-used-technologies)

## 📖 Features

- 🚀 **High Performance**: Leverages GraalVM Native Image for improved performance and startup time.
- 🌐 **Modern Spring Boot**: Built on the latest Spring Boot version.
- 🔐 **Secure**: Implements robust authentication mechanisms.
- 🔄 **Extensible**: Add your own features with ease.

## 🎥 Demo Preview

Below is a quick preview of the Application:

The application will be available at http://localhost:8080.

![Demo Preview](https://github.com/susimsek/spring-boot-graalvm-native-example/blob/main/images/webapp.png)

## 🧑‍💻 Development Setup

To clone and run this application locally:

```bash
# Clone the repository
git clone https://github.com/example/spring-boot-graalvm-native-example.git

# Navigate to the project directory
cd spring-boot-graalvm-native-example

# Build the project
 ./mvnw native:compile -B -ntp -Pnative,prod -DskipTests

# Run the application
./target/native-executable
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

1. Ensure GraalVM is installed and set up correctly.
2. Run the native image build:
   ```bash
   ./mvnw native:compile -B -ntp -Pnative,prod -DskipTests
   ```
3. The executable will be available in the `target` directory.

## 🕵️ Code Analysis

Checkstyle is used to enforce coding standards and maintain code consistency.

To check the Java code style using Checkstyle, execute:

```bash
mvn checkstyle:check
```

## 🛡️ Code Quality

To assess code quality locally using SonarQube, execute:

```bash
mvn -Psonar compile initialize sonar:sonar
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

## 🛠️ Used Technologies

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.6.7-green?logo=spring&logoColor=white)  
![GraalVM](https://img.shields.io/badge/GraalVM-Native_Image-FF8C00?logo=graalvm&logoColor=white)  
![Spring Boot Actuator](https://img.shields.io/badge/Spring_Boot_Actuator-green?logo=spring&logoColor=white)  
![Checkstyle](https://img.shields.io/badge/Checkstyle-Code_Analysis-orange?logo=openjdk&logoColor=white)  
![Lombok](https://img.shields.io/badge/Lombok-orange?logo=openjdk&logoColor=white)  
![Maven](https://img.shields.io/badge/Maven-Build_Automation-C71A36?logo=apachemaven&logoColor=white)  
![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?logo=sonarqube&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)  


