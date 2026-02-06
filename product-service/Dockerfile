# Etapa 1: Construcción (Build) con Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos saltando los tests
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run) con JRE ligero
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el jar generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]