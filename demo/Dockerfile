# -------------------------
# Build Stage (has Maven)
# -------------------------
FROM maven:3.9.3-eclipse-temurin-20 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy all source code
COPY src ./src

# Package the application (skip tests)
RUN mvn clean package -DskipTests

# -------------------------
# Runtime Stage (only JDK, no Maven needed)
# -------------------------
FROM eclipse-temurin:20-jdk-alpine

WORKDIR /app

# Copy the JAR built in the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
