# Exemplo de Dockerfile para Spring Boot

# Estágio de Build (se for multistage) ou na imagem final:
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar

# ADICIONE ESTA LINHA:
EXPOSE 8080

ENV PORT=8080
ENTRYPOINT ["java", "-jar", "app.jar"]