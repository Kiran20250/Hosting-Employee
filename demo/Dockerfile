# -------------------------
# Build Stage
# -------------------------
FROM maven:3.9.3-eclipse-temurin-20 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------
# Runtime Stage
# -------------------------
FROM eclipse-temurin:20-jdk-alpine
WORKDIR /app

# Copy WAR file
COPY --from=build /app/target/*.war app.war

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.war"]