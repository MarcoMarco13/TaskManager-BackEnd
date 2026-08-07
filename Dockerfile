# Etapa 1: Build (Compilação do projeto com Maven)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execução leve
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia o JAR gerado na etapa 1 para a etapa de execução
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]