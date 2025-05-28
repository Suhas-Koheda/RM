# Resume Matcher

A full-stack application that analyzes resumes against job descriptions using AI to help job seekers improve their resume match rate.

---

## Project Overview

Resume Matcher is a web application that allows users to upload their resume and a job description, then uses AI (Google's Gemini) to analyze how well the resume matches the job requirements. The application provides a match percentage and suggestions for improving the resume.

**Features:**
- Upload resume files (PDF or text format)
- Enter job descriptions
- Analyze resume-job description match using AI
- View match percentage and score
- Get personalized suggestions to improve resume match
- Clean and responsive UI with Material Design
- View history of previous analyses

---

## Technology Stack

### Backend
- **Language:** Kotlin
- **Framework:** Spring Boot
- **Java Version:** 21
- **Build Tool:** Maven
- **AI Integration:** Langchain4j with Google Gemini
- **PDF Processing:** Apache PDFBox
- **Database:** Built-in persistence layer

### Frontend
- **Language:** JavaScript (React)
- **UI Framework:** Material UI
- **HTTP Client:** Axios
- **Routing:** React Router DOM
- **Development Server:** React Scripts

---

## Project Structure

```
ResumeMatcher/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── dev/haas/rm/
│   │   │       ├── controller/
│   │   │       │   ├── FileUploadController.kt
│   │   │       │   └── AuthController.kt
│   │   │       ├── model/
│   │   │       │   ├── dto/
│   │   │       │   └── entity/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   │           └── frontend/
│   │               ├── package.json
│   │               ├── public/
│   │               │   └── index.html
│   │               └── src/
│   │                   ├── App.js
│   │                   ├── App.css
│   │                   ├── index.js
│   │                   ├── components/
│   │                   │   ├── Header.js
│   │                   │   └── Footer.js
│   │                   ├── pages/
│   │                   │   ├── HomePage.js
│   │                   │   ├── ResultsPage.js
│   │                   │   └── ResumesPage.js
│   │                   └── services/
│   │                       └── api.js
├── run.sh
├── run.bat
├── Dockerfile
├── README.md
└── ...
```

---

## Backend API Endpoints

- `POST /analyse/upload` — Upload a resume and job description for analysis (multipart/form-data)
  - Params: `resumeFile`, `JD`, `title`
- `GET /analyse/resume` — Get all previous analyses
- `POST /auth/login` — User login (optional, if enabled)
- `POST /auth/register` — User registration (optional, if enabled)

---

## Frontend Usage

The frontend is a modern React app using Material UI. It provides:
- **Home Page:** Upload resume, enter job description, and submit for analysis
- **Results Page:** View match percentage, suggestions, and job description summary
- **Resumes Page:** View history of previous analyses and details for each

### Main Files
- `src/pages/HomePage.js` — Upload and analyze resumes
- `src/pages/ResultsPage.js` — Show analysis results
- `src/pages/ResumesPage.js` — List and view previous analyses
- `src/services/api.js` — Handles API calls to backend
- `src/components/Header.js` and `Footer.js` — Layout

---

## How to Run

### Prerequisites
- Java 21 or later
- Node.js and npm
- Maven (wrapper included)

### On Linux/macOS
```bash
./run.sh
```

### On Windows
```cmd
run.bat
```

This will:
1. Build the backend using Maven
2. Install frontend dependencies if needed
3. Start the Spring Boot backend
4. Start the React frontend development server

- Backend: http://localhost:8080
- Frontend: http://localhost:3000

---

## Development

### Backend
- Run tests: `./mvnw test`
- Build: `./mvnw clean package`

### Frontend
- Install dependencies: `cd src/main/resources/static/frontend && npm install`
- Start dev server: `npm start`

---

## Troubleshooting
- If ports 8080 or 3000 are in use, stop the conflicting processes or change the ports in configuration.
- If you see dependency errors, run `npm install` in the frontend directory.
- For Java errors, ensure Java 21+ is installed and on your PATH.

---

## Libraries Used

### Backend
| Library         | Purpose                                 |
|-----------------|-----------------------------------------|
| Spring Boot     | Web framework                           |
| Kotlin          | Programming language                    |
| Langchain4j     | AI integration (Google Gemini)          |
| Apache PDFBox   | PDF text extraction                     |
| Spring Data JPA | Database access                         |

### Frontend
| Library             | Purpose                        |
|---------------------|--------------------------------|
| React               | UI framework                   |
| Material UI         | UI components & theming        |
| Axios               | HTTP client                    |
| React Router DOM    | Routing                        |

---

## License
MIT or as specified in the repository.

