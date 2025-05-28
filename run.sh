#!/bin/bash

# Resume Matcher Startup Script
# This script starts both the frontend React app and the Spring Boot backend

# Set the base directory to the script location
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$BASE_DIR/src/main/resources/static/frontend"
BACKEND_DIR="$BASE_DIR"

# Function to check if command exists
command_exists() {
  command -v "$1" >/dev/null 2>&1
}

# Function to show error and exit
error_exit() {
  echo "ERROR: $1" >&2
  exit 1
}

# Function to determine OS type and use appropriate Maven wrapper
use_maven_wrapper() {
  if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
    # Windows OS
    mvnw_cmd="$BACKEND_DIR/mvnw.cmd"
  else
    # Unix-like OS (Linux, macOS)
    mvnw_cmd="$BACKEND_DIR/mvnw"
    # Ensure mvnw is executable
    chmod +x "$mvnw_cmd" 2>/dev/null
  fi
  
  $mvnw_cmd clean package -DskipTests
}

# Print banner
echo "======================================================"
echo "      Resume Matcher - Frontend/Backend Launcher      "
echo "======================================================"

# Check for required commands
command_exists java || error_exit "Java is not installed. Please install Java 21 or later."
command_exists node || error_exit "Node.js is not installed. Please install Node.js."
command_exists npm || error_exit "npm is not installed. Please install npm."

echo -e "\n[1/4] Building backend (clean build)..."
# Navigate to backend directory and build with Maven wrapper
cd "$BACKEND_DIR" || error_exit "Cannot navigate to backend directory"
use_maven_wrapper || error_exit "Backend build failed"
echo "Backend build successful!"

echo -e "\n[2/4] Installing frontend dependencies if needed..."
# Install frontend dependencies if needed
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
  echo "Installing frontend dependencies..."
  cd "$FRONTEND_DIR" || error_exit "Cannot navigate to frontend directory"
  npm install || error_exit "Frontend dependency installation failed"
  cd - > /dev/null
else
  echo "Node modules found, checking for react-scripts..."
  # Check if react-scripts is installed
  if [ ! -d "$FRONTEND_DIR/node_modules/react-scripts" ]; then
    echo "react-scripts not found, installing..."
    cd "$FRONTEND_DIR" || error_exit "Cannot navigate to frontend directory"
    npm install react-scripts --save-dev || error_exit "Failed to install react-scripts"
    cd - > /dev/null
  fi
fi
echo "Frontend dependencies ready!"

# Start the backend in the background and log output
cd "$BACKEND_DIR" || error_exit "Cannot navigate to backend directory"
java -jar target/RecuritrManager-0.0.1-SNAPSHOT.jar > "$BASE_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "Backend started with PID: $BACKEND_PID (logs: $BASE_DIR/backend.log)"

# Give the backend a moment to start
sleep 3

# Start the frontend and log output
cd "$FRONTEND_DIR" || error_exit "Cannot navigate to frontend directory"
npx react-scripts start > "$BASE_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "Frontend started with PID: $FRONTEND_PID (logs: $BASE_DIR/frontend.log)"

echo -e "\n======================================================"
echo "Resume Matcher is now running!"
echo "Frontend: http://localhost:3000"
echo "Backend: http://localhost:8080"
echo "======================================================"
echo -e "\nPress Ctrl+C to shut down both services..."

# Show logs after startup
tail -f "$BASE_DIR/backend.log" "$BASE_DIR/frontend.log"

# Setup trap to handle cleanup on script termination
trap "echo -e '\nShutting down services...'; kill $FRONTEND_PID 2>/dev/null; kill $BACKEND_PID 2>/dev/null; echo 'Services stopped.'; exit 0" INT TERM

# Wait for user to press Ctrl+C
wait &