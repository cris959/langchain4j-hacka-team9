# Etapa 1: Compilación con Maven usando Java 21
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final ligera para ejecución con Java 21 JRE
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]