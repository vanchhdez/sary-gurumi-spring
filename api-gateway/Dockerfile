# Etapa 1: Construcción (Build) con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run) con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Configuración opcional para habilitar Virtual Threads en la JVM si fuera necesario
ENTRYPOINT ["java", "-jar", "app.jar"]