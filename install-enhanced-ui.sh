#!/bin/bash

# Script to install and update enhanced UI files for Resume Matcher Frontend
# This script replaces the frontend files with enhanced versions

# Set the base directory
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRONTEND_DIR="$BASE_DIR/src/main/resources/static/frontend"

# Function to show colored output
print_colored() {
  local color_code="$1"
  local message="$2"
  echo -e "\033[${color_code}m${message}\033[0m"
}

# Success message in green
print_success() {
  print_colored "32" "✅ $1"
}

# Error message in red
print_error() {
  print_colored "31" "❌ $1"
}

# Info message in blue
print_info() {
  print_colored "34" "ℹ️ $1"
}

# Header message in purple
print_header() {
  print_colored "35" "\n$1\n$(printf '=%.0s' {1..50})"
}

# Check if frontend directory exists
if [ ! -d "$FRONTEND_DIR" ]; then
  print_error "Frontend directory not found at $FRONTEND_DIR"
  exit 1
fi

# Print installation header
print_header "Resume Matcher Enhanced UI Installation"
print_info "Updating frontend files with enhanced versions..."

# Copy enhanced App files
if [ -f "$FRONTEND_DIR/src/enhanced-App.js" ] && [ -f "$FRONTEND_DIR/src/enhanced-App.css" ]; then
  cp "$FRONTEND_DIR/src/enhanced-App.js" "$FRONTEND_DIR/src/App.js"
  cp "$FRONTEND_DIR/src/enhanced-App.css" "$FRONTEND_DIR/src/App.css"
  print_success "Updated App.js and App.css with enhanced versions"
else
  print_error "Enhanced App files not found"
fi

# Copy enhanced component files
if [ -f "$FRONTEND_DIR/src/components/enhanced-Header.js" ]; then
  cp "$FRONTEND_DIR/src/components/enhanced-Header.js" "$FRONTEND_DIR/src/components/Header.js"
  print_success "Updated Header.js with enhanced version"
else
  print_error "Enhanced Header.js not found"
fi

if [ -f "$FRONTEND_DIR/src/components/enhanced-Footer.js" ]; then
  cp "$FRONTEND_DIR/src/components/enhanced-Footer.js" "$FRONTEND_DIR/src/components/Footer.js"
  print_success "Updated Footer.js with enhanced version"
else
  print_error "Enhanced Footer.js not found"
fi

# Copy enhanced page files
if [ -f "$FRONTEND_DIR/src/pages/enhanced-HomePage.js" ]; then
  cp "$FRONTEND_DIR/src/pages/enhanced-HomePage.js" "$FRONTEND_DIR/src/pages/HomePage.js"
  print_success "Updated HomePage.js with enhanced version"
else
  print_error "Enhanced HomePage.js not found"
fi

if [ -f "$FRONTEND_DIR/src/pages/enhanced-ResultsPage.js" ]; then
  cp "$FRONTEND_DIR/src/pages/enhanced-ResultsPage.js" "$FRONTEND_DIR/src/pages/ResultsPage.js"
  print_success "Updated ResultsPage.js with enhanced version"
else
  print_error "Enhanced ResultsPage.js not found"
fi

# Copy enhanced index.html
if [ -f "$FRONTEND_DIR/public/enhanced-index.html" ]; then
  cp "$FRONTEND_DIR/public/enhanced-index.html" "$FRONTEND_DIR/public/index.html"
  print_success "Updated index.html with enhanced version"
else
  print_error "Enhanced index.html not found"
fi

# Print completion message
print_header "Installation Complete"
print_info "The enhanced UI has been installed successfully."
print_info "To apply these changes, restart the application with ./run.sh"

exit 0
