#!/bin/bash

# Resume Matcher Startup Script
# This script starts both the frontend React app and the Spring Boot backend

# Set the base directory to the script location
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$BASE_DIR/src/main/resources/static/frontend"
BACKEND_DIR="$BASE_DIR"
LOG_DIR="$BASE_DIR/logs"

# Create logs directory if it doesn't exist
mkdir -p "$LOG_DIR"
BACKEND_LOG="$LOG_DIR/backend.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"

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
  
  $mvnw_cmd "$@"
}

# Print banner
echo "======================================================"
echo "      Resume Matcher - Frontend/Backend Launcher      "
echo "======================================================"

# Check for required commands
command_exists java || error_exit "Java is not installed. Please install Java 21 or later."
command_exists node || error_exit "Node.js is not installed. Please install Node.js."
command_exists npm || error_exit "npm is not installed. Please install npm."

echo -e "\n[1/4] Installing frontend dependencies if needed..."
# Navigate to frontend directory and install dependencies if needed
cd "$FRONTEND_DIR" || error_exit "Cannot navigate to frontend directory"
if [ ! -d "node_modules" ]; then
  echo "Node modules not found, installing dependencies..."
  npm install || error_exit "Frontend dependency installation failed"
else
  echo "Node modules found, checking for react-scripts..."
  # Check if react-scripts is installed
  if [ ! -d "node_modules/react-scripts" ]; then
    echo "react-scripts not found, installing..."
    npm install react-scripts --save-dev || error_exit "Failed to install react-scripts"
  fi
fi
echo "Frontend dependencies ready!"

# Always run the backend in dev mode so code changes are picked up
# Remove JAR build logic and use spring-boot:run

echo -e "\n[2/4] Starting Spring Boot backend..."
cd "$BACKEND_DIR" || error_exit "Cannot navigate to backend directory"
> "$BACKEND_LOG"
use_maven_wrapper spring-boot:run > "$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!
echo "Backend started with PID: $BACKEND_PID"

# Give the backend a moment to start
sleep 3

# Start the frontend with logging
echo -e "\n[3/4] Starting React frontend..."
cd "$FRONTEND_DIR" || error_exit "Cannot navigate to frontend directory"
# Clear previous logs
> "$FRONTEND_LOG"
# Use npx to ensure react-scripts is found
npx react-scripts start > "$FRONTEND_LOG" 2>&1 &
FRONTEND_PID=$!
echo "Frontend started with PID: $FRONTEND_PID"

echo -e "\n======================================================"
echo "Resume Matcher is now running!"
echo "Frontend: http://localhost:3000"
echo "Backend: http://localhost:8080"
echo "======================================================"
echo -e "\nShowing logs from both services (Press Ctrl+C to stop)..."

# Setup trap to handle cleanup on script termination
trap "echo -e '\nShutting down services...'; kill $FRONTEND_PID 2>/dev/null; kill $BACKEND_PID 2>/dev/null; echo 'Services stopped.'; exit 0" INT TERM

# Show logs from both services in real-time
# The "|| true" ensures that if the tail command exits abnormally, the script continues
tail -f "$BACKEND_LOG" "$FRONTEND_LOG" | sed -e 's/^/[BACKEND] /' -e 's/^/[FRONTEND] /' || true

# Wait for user to press Ctrl+C
wait