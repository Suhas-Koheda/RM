# Stage 1: Build Frontend
# Assumes Node.js frontend in 'src/main/resources/static/frontend'
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

# Copy package.json and package-lock.json (if available)
COPY src/main/resources/static/frontend/package.json ./
# If you use package-lock.json (recommended for npm)
# COPY src/main/resources/static/frontend/package-lock.json ./
# If you use yarn.lock instead:
# COPY src/main/resources/static/frontend/yarn.lock ./

# Install dependencies
# For npm, 'npm ci' is often preferred for reproducible builds if package-lock.json exists
RUN npm install
# If using yarn:
# RUN yarn install --frozen-lockfile

# Copy the rest of the frontend application code
COPY src/main/resources/static/frontend/ ./

# Build the frontend application
# This assumes the build output is in /app/frontend/build
RUN npm run build

# Stage 2: Build Backend (Spring Boot with Maven)
# Using a Maven image with JDK 21 as specified in pom.xml
FROM maven:3-eclipse-temurin-21 AS backend-builder
WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Copy the backend source code
COPY src ./src

# Copy built frontend assets into Spring Boot's static resources directory
# This allows Spring Boot to serve the frontend files.
# The frontend build output is in /app/frontend/build from the frontend-builder stage.
# We copy its contents into /app/src/main/resources/static, which will be packaged into the JAR.
COPY --from=frontend-builder /app/frontend/build ./src/main/resources/static

# Build the Spring Boot application, skipping tests for faster Docker builds
# The target JAR will be in /app/target/
RUN mvn package -DskipTests

# Stage 3: Create Final Application Image
# Use a JRE image for a smaller footprint, compatible with Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the executable JAR from the backend-builder stage
# Adjust rm-0.0.1-SNAPSHOT.jar if your artifactId or version changes in pom.xml
COPY --from=backend-builder /app/target/rm-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on (default for Spring Boot is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
