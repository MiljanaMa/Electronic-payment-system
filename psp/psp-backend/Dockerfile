FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
# Download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
# Build
RUN mvn clean package -DskipTests
# 2. Runtime stage
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar
# Copy SSL keystore and truststore
COPY src/main/resources/psp.p12 ./psp.p12
COPY src/main/resources/truststore.jks ./truststore.jks

EXPOSE 8082
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]