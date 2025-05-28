import React, { useState, useEffect } from 'react';
import { 
  Container, 
  Typography, 
  Paper, 
  Button,
  Box, 
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
  Divider,
  Card,
  CardContent
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import StorageIcon from '@mui/icons-material/Storage';
import { useNavigate } from 'react-router-dom';
import { getResumes } from '../services/api';

function ResumesPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [resumes, setResumes] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);

  // Fetch resumes when the component mounts
  useEffect(() => {
    fetchResumes();
  }, []);

  // Function to fetch resumes from the backend
  const fetchResumes = async () => {
    try {
      setLoading(true);
      const data = await getResumes();
      setResumes(data);
    } catch (err) {
      setError('Failed to load resumes. Please try again later.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Function to view a specific resume
  const viewResumeDetails = (resume) => {
    setSelectedResume(resume);
  };

  // Function to go back to the resume list
  const backToList = () => {
    setSelectedResume(null);
  };

  // Function to determine match color
  const getMatchColor = (percentage) => {
    if (percentage >= 80) return 'success.main';
    if (percentage >= 60) return 'warning.main';
    return 'error.main';
  };

  // Render loading state
  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4, textAlign: 'center' }}>
        <CircularProgress />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading resumes...
        </Typography>
      </Container>
    );
  }

  // Render error state
  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          variant="contained"
        >
          Back to Home
        </Button>
      </Container>
    );
  }

  // Render selected resume details
  if (selectedResume) {
    const { resume, analysedResults } = selectedResume;
    const { match, suggestions, modelUsed } = analysedResults;
    
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={backToList}
          sx={{ mb: 2 }}
          color="primary"
        >
          Back to Resumes
        </Button>

        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" gutterBottom align="center">
            Resume Analysis Details
          </Typography>
          
          <Box sx={{ mb: 4 }}>
            <Typography variant="subtitle1">
              Model: <strong>{modelUsed}</strong>
            </Typography>
          </Box>

          <Divider sx={{ mb: 4 }} />

          <Box sx={{ mb: 4 }}>
            <Typography variant="h6" gutterBottom>
              Match Score: <span style={{ color: getMatchColor(match) }}>{match}%</span>
            </Typography>
          </Box>

          <Card variant="outlined" sx={{ mb: 4 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Suggestions
              </Typography>
              <Box 
                sx={{ 
                  '& h3': { 
                    fontSize: '1.15rem',
                    fontWeight: 'bold',
                    marginTop: '16px',
                    marginBottom: '8px',
                    color: 'text.primary'
                  },
                  '& h5': {
                    fontSize: '0.95rem',
                    fontWeight: 'normal',
                    marginTop: '4px',
                    marginBottom: '8px',
                    color: 'text.secondary'
                  }
                }}
                dangerouslySetInnerHTML={{ __html: suggestions }}
              />
            </CardContent>
          </Card>

          <Box>
            <Typography variant="h6" gutterBottom>
              Resume Content
            </Typography>
            <Typography variant="body2" color="text.secondary" 
              sx={{ 
                maxHeight: 200, 
                overflow: 'auto',
                whiteSpace: 'pre-wrap',
                backgroundColor: '#f5f5f5',
                p: 2,
                borderRadius: 1
              }}>
              {resume}
            </Typography>
          </Box>
        </Paper>
      </Container>
    );
  }

  // Render resume list
  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          color="primary"
        >
          Back to Home
        </Button>
        <Typography variant="h4" sx={{ flexGrow: 1, textAlign: 'center' }}>
          <StorageIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
          Previous Resume Analyses
        </Typography>
      </Box>
      
      {resumes.length === 0 ? (
        <Paper elevation={3} sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6">No resumes found</Typography>
          <Typography variant="body2" sx={{ mt: 2, mb: 3 }}>
            Upload a resume on the home page to analyze it against a job description.
          </Typography>
          <Button 
            variant="contained" 
            color="primary"
            onClick={() => navigate('/')}
          >
            Upload Resume
          </Button>
        </Paper>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>#</TableCell>
                <TableCell>ID</TableCell>
                <TableCell>Match Percentage</TableCell>
                <TableCell>Model Used</TableCell>
                <TableCell>Action</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {resumes.map((resume, index) => (
                <TableRow key={resume.id}>
                  <TableCell>{index + 1}</TableCell>
                  <TableCell>{resume.id}</TableCell>
                  <TableCell 
                    sx={{ 
                      color: getMatchColor(resume.analysedResults.match),
                      fontWeight: 'bold'
                    }}
                  >
                    {resume.analysedResults.match}%
                  </TableCell>
                  <TableCell>{resume.analysedResults.modelUsed}</TableCell>
                  <TableCell>
                    <Button 
                      variant="outlined" 
                      size="small"
                      onClick={() => viewResumeDetails(resume)}
                    >
                      View Details
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Container>
  );
}

export default ResumesPage;