# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY . .

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the Spring Boot application (skip tests to speed up)
RUN mvn clean package -DskipTests

# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Set environment variable for Render port
ENV PORT=8080

# Run the Spring Boot app
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
