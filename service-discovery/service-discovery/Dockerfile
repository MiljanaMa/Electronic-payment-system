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

EXPOSE 8761
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]