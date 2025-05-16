@echo off
setlocal enabledelayedexpansion

:: Resume Matcher Startup Script for Windows
:: This script starts both the frontend React app and the Spring Boot backend

echo ======================================================
echo      Resume Matcher - Frontend/Backend Launcher      
echo ======================================================

:: Set the base directory to the script location
set BASE_DIR=%~dp0
set FRONTEND_DIR=%BASE_DIR%src\main\resources\static\frontend
set BACKEND_DIR=%BASE_DIR%

:: Check for required commands
where java >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not installed or not in PATH. Please install Java 21 or later.
    exit /b 1
)

where node >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: Node.js is not installed or not in PATH. Please install Node.js.
    exit /b 1
)

where npm >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo ERROR: npm is not installed or not in PATH. Please install npm.
    exit /b 1
)

echo.
echo [1/4] Building backend...
:: Navigate to backend directory and build with Maven wrapper
cd "%BACKEND_DIR%" || (
    echo ERROR: Cannot navigate to backend directory
    exit /b 1
)
call mvnw.cmd package -DskipTests
if %ERRORLEVEL% neq 0 (
    echo ERROR: Backend build failed
    exit /b 1
)
echo Backend build successful!

echo.
echo [2/4] Installing frontend dependencies if needed...
:: Navigate to frontend directory and install dependencies if needed
cd "%FRONTEND_DIR%" || (
    echo ERROR: Cannot navigate to frontend directory
    exit /b 1
)
if not exist node_modules (
    echo Node modules not found, installing dependencies...
    call npm install
    if %ERRORLEVEL% neq 0 (
        echo ERROR: Frontend dependency installation failed
        exit /b 1
    )
) else (
    echo Node modules found, checking for react-scripts...
    :: Check if react-scripts is installed
    if not exist node_modules\react-scripts (
        echo react-scripts not found, installing...
        call npm install react-scripts --save-dev
        if %ERRORLEVEL% neq 0 (
            echo ERROR: Failed to install react-scripts
            exit /b 1
        )
    )
)
echo Frontend dependencies ready!

echo.
echo [3/4] Starting Spring Boot backend...
:: Navigate to backend directory and start the Spring Boot app
cd "%BACKEND_DIR%" || (
    echo ERROR: Cannot navigate to backend directory
    exit /b 1
)
start "" java -jar target\rm-0.0.1-SNAPSHOT.jar
echo Backend started successfully!

:: Give the backend a moment to start
timeout /t 3 /nobreak >nul

echo.
echo [4/4] Starting React frontend...
:: Navigate to frontend directory and start the React app
cd "%FRONTEND_DIR%" || (
    echo ERROR: Cannot navigate to frontend directory
    exit /b 1
)
:: Use npx to ensure react-scripts is found
start "" npx react-scripts start
echo Frontend started successfully!

echo.
echo ======================================================
echo Resume Matcher is now running!
echo Frontend: http://localhost:3000
echo Backend: http://localhost:8080
echo ======================================================
echo.
echo The applications are running in separate windows.
echo Close those windows to stop the applications when done.
echo Press any key to exit this script...
pause >nul