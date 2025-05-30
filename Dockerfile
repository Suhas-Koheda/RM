# Build stage for both backend and frontend
FROM maven:3.8.5-openjdk-17 AS build

# Set working directory
WORKDIR /build

# Copy Maven POM file and source code
COPY pom.xml .
COPY src ./src
COPY .env .

# Build the Spring Boot application
RUN mvn clean package -DskipTests

# Final runtime image
FROM openjdk:17-slim

# Install Node.js and npm
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set up working directories
WORKDIR /app

# Copy Spring Boot JAR from build stage
COPY --from=build /build/target/RecuritrManager-0.0.1-SNAPSHOT.jar /app/app.jar

# Copy frontend files from build stage
COPY --from=build /build/src/main/resources/static/frontend /app/frontend

# Install npm dependencies for the frontend
WORKDIR /app/frontend
RUN npm install

# Set working directory back to /app
WORKDIR /app
 
# Expose ports (8080 for backend, 3000 for React dev server)
EXPOSE 8080 3000

# Copy startup script
COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh"]