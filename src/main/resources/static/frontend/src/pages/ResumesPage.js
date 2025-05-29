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
import DeleteIcon from '@mui/icons-material/Delete';
import { useNavigate } from 'react-router-dom';
import { getResumes, deleteResume } from '../services/api';

function ResumesPage() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [resumes, setResumes] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    fetchResumes();
  }, []);

  const fetchResumes = async () => {
    try {
      setLoading(true);
      const data = await getResumes();
      setResumes(data);
    } catch (err) {
      setError('Failed to load resumes. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const viewResumeDetails = (resume) => {
    setSelectedResume(resume);
  };

  const backToList = () => {
    setSelectedResume(null);
  };

  const handleDelete = async (id) => {
    setDeleting(true);
    setError('');
    try {
      await deleteResume(id);
      fetchResumes();
      setSelectedResume(null);
    } catch (err) {
      setError('Failed to delete resume. Please try again.');
    } finally {
      setDeleting(false);
    }
  };

  const getMatchColor = (percentage) => {
    if (percentage >= 80) return 'success.main';
    if (percentage >= 60) return 'warning.main';
    return 'error.main';
  };

  if (loading) {
    return (
      <Container maxWidth="md" sx={{ mt: 6 }}>
        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
          <CircularProgress />
        </Box>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="md" sx={{ mt: 6 }}>
        <Alert severity="error">{error}</Alert>
      </Container>
    );
  }

  if (selectedResume) {
    const { analysedResults, JD, title } = selectedResume;
    const { match, suggestions, modelUsed } = analysedResults;
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Button startIcon={<ArrowBackIcon />} onClick={backToList} sx={{ mb: 2 }}>
          Back to List
        </Button>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Typography variant="h4" gutterBottom align="center">
            Resume Analysis Details
          </Typography>
          <Box sx={{ mb: 4 }}>
            <Typography variant="subtitle1">
              Resume: <strong>{title}</strong>
            </Typography>
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
              Job Description
            </Typography>
            <Paper variant="outlined" sx={{ p: 2, mt: 1 }}>
              <Typography variant="body2" color="text.secondary">
                {JD}
              </Typography>
            </Paper>
          </Box>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 4 }}>
        <StorageIcon sx={{ verticalAlign: 'middle', mr: 1 }} />
        <Typography variant="h4" sx={{ flexGrow: 1, textAlign: 'center' }}>
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
                    <Button
                      variant="outlined"
                      color="error"
                      size="small"
                      startIcon={<DeleteIcon />}
                      sx={{ ml: 1 }}
                      onClick={() => handleDelete(resume.id)}
                      disabled={deleting}
                    >
                      {deleting ? 'Deleting...' : 'Delete'}
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
