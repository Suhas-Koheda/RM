# Resume Matcher

A full-stack application that analyzes resumes against job descriptions using AI to help job seekers improve their resume match rate.

## Project Overview

Resume Matcher is a web application that allows users to upload their resume and a job description, then uses AI (Google's Gemini) to analyze how well the resume matches the job requirements. The application provides a match percentage and suggestions for improving the resume.

The application consists of:
- A Kotlin/Spring Boot backend for processing files and integrating with AI services
- A React frontend with Material UI for user interaction
- PDF processing capabilities for extracting text from resume PDFs

## Features

- Upload resume files (PDF or text format)
- Enter job descriptions
- Analyze resume-job description match using AI
- View match percentage and score
- Get personalized suggestions to improve resume match
- Clean and responsive UI with Material Design

## Technology Stack

### Backend
- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.4.5
- **Java Version**: 21
- **Build Tool**: Maven
- **AI Integration**: Langchain4j with Google Gemini
- **PDF Processing**: Apache PDFBox 2.0.30
- **Testing**: JUnit 5, Mockito-Kotlin

### Frontend
- **Language**: JavaScript/React 18.2.0
- **UI Framework**: Material UI 5.14.5
- **HTTP Client**: Axios 1.4.0
- **Routing**: React Router DOM 6.15.0
- **Development Server**: React Scripts 5.0.1

## Component Overview

### Backend Components

#### Controllers
- `FileUploadController`: Handles file uploads and processes analysis requests

#### Models
- `AnalysedResults`: Data class for storing analysis results (match percentage, suggestions, model used)
- `FileProcessService`: Service for processing and extracting text from uploaded files
- `ResumeProcessService`: Service for analyzing resume text against job descriptions using AI
- `UploadRequest`: Data class for handling the uploaded resume and job description

### Frontend Components

#### Pages
- `HomePage`: Landing page with file upload and job description input
- `ResultsPage`: Displays analysis results including match percentage and suggestions

#### Components
- `Header`: Navigation header component
- `Footer`: Application footer component

#### Services
- `api.js`: Contains API calls to the backend

## Core Functionality

### Resume Processing
The application extracts text from uploaded resumes (PDF or text) using Apache PDFBox for PDF files.

### AI Analysis
The Langchain4j integration with Google Gemini analyzes:
1. How well the resume matches the job description
2. Generates a match percentage
3. Provides suggestions for improvement

### User Interface
The React frontend provides an intuitive interface for uploading resumes, entering job descriptions, and viewing results in a user-friendly format.

## Project Structure

```
rm/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── dev/haas/rm/
│   │   │       ├── controller/
│   │   │       │   └── FileUploadController.kt
│   │   │       ├── model/
│   │   │       │   ├── AnalysedResults.kt
│   │   │       │   ├── FileProcessService.kt
│   │   │       │   ├── FirebaseModel.kt
│   │   │       │   ├── ResumeProcessService.kt
│   │   │       │   └── UploadRequest.kt
│   │   │       └── RmApplication.kt
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── frontend/
│   │       │   │   ├── public/
│   │       │   │   └── src/
│   │       │   │       ├── components/
│   │       │   │       ├── pages/
│   │       │   │       └── services/
│   │       │   └── Suhas_Koheda_Kotlin_Resume.pdf
│   │       └── application.properties
│   └── test/
│       └── kotlin/
│           └── dev/haas/rm/
│               ├── controller/
│               │   └── FileUploadControllerTest.kt
│               ├── model/
│               │   ├── FileProcessServiceTest.kt
│               │   └── ResumeProcessServiceTest.kt
│               └── RmApplicationTests.kt
├── pom.xml
├── run.sh
└── README.md
```

## Libraries Used and Their Purpose

### Backend Libraries

| Library | Purpose | Usage |
|---------|---------|-------|
| Spring Boot | Application framework | Forms the backbone of the application, providing web functionality |
| Kotlin | Programming language | Used for all backend code, providing type safety and modern syntax |
| Langchain4j | AI integration | Connects with Google's Gemini AI for resume analysis |
| Apache PDFBox | PDF processing | Extracts text from PDF resumes for analysis |
| Mockito-Kotlin | Testing | Used for mocking dependencies in unit tests |
| JUnit 5 | Testing | Used for backend unit testing |

### Frontend Libraries

| Library | Purpose | Usage |
|---------|---------|-------|
| React | UI library | Core frontend framework |
| Material UI | Component library | Provides pre-styled components and theming |
| Axios | HTTP client | Handles API requests to the backend |
| React Router DOM | Routing | Manages navigation between pages |
| Emotion | Styling | CSS-in-JS styling solution used by Material UI |

## Key Functions

### Backend

- `processFile(resume: MultipartFile)`: Extracts text from uploaded resume files
- `processUploadRequest(uploadRequest: UploadRequest)`: Processes the uploaded resume and job description
- `analyseResume(resume: String, JD: String)`: Uses AI to analyze the resume against the job description
- `buildAnalysedResults(results: String)`: Parses AI response into structured result data

### Frontend

- `handleFileChange()`: Processes file uploads in the UI
- `handleSubmit()`: Sends analysis request to the backend
- `analyzeResume()`: API service function that communicates with the backend
- `getMatchColor()`: Determines the color coding based on match percentage

## Running the Application

The project includes a convenient startup script (`run.sh`) that:

1. Builds the backend using Maven
2. Installs frontend dependencies if needed
3. Starts the Spring Boot backend
4. Starts the React frontend development server

### Prerequisites

- Java 21 or later
- Node.js and npm
- Maven (wrapper included)

### Starting the Application

```bash
./run.sh
```

This will start:
- Backend server at http://localhost:8080
- Frontend development server at http://localhost:3000

## Development

### Backend Development

- Run tests: `./mvnw test`
- Build: `./mvnw clean package`

### Frontend Development

- Install dependencies: `cd src/main/resources/static/frontend && npm install`
- Start development server: `npm start`

## Future Enhancements

- User authentication system
- Save analysis history
- Compare multiple job descriptions
- Export analysis results
- Enhanced AI suggestions
- Custom resume templates

## Contributors

- Original development by [Your Name]

## License

[Specify your license here]