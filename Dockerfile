# Etapa 1: Compilação com limite de memória no Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Limita a memória usada pelo Maven para não estourar o container
ENV MAVEN_OPTS="-Xmx384m"
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Etapa 2: Execução leve
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Informa a porta padrão que o Render espera
EXPOSE 8080

# Limita o consumo da JVM no plano gratuito (máximo 384MB de RAM)
ENTRYPOINT ["java", "-Xmx384m", "-Xms128m", "-jar", "app.jar"]