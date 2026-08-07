# Etapa 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execução leve
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Limita a memória Heap da JVM a 256MB para caber no plano grátis de 512MB
ENTRYPOINT ["sh", "-c", "java -Xms128m -Xmx256m -Dserver.port=${PORT:-8080} -jar app.jar"]