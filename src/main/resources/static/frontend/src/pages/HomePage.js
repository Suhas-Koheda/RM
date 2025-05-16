import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, 
  Paper, 
  Typography, 
  Button, 
  TextField, 
  Box, 
  CircularProgress,
  Alert 
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { styled } from '@mui/material/styles';
import { analyzeResume } from '../services/api';

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

function HomePage() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fileName, setFileName] = useState('No file selected');

  const handleFileChange = (event) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) {
      if (selectedFile.type === 'application/pdf' || 
          selectedFile.type === 'text/plain') {
        setFile(selectedFile);
        setFileName(selectedFile.name);
        setError('');
      } else {
        setFile(null);
        setFileName('No file selected');
        setError('Please upload a PDF or text file');
      }
    }
  };

  const handleJobDescriptionChange = (event) => {
    setJobDescription(event.target.value);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!file) {
      setError('Please upload a resume file');
      return;
    }
    if (!jobDescription.trim()) {
      setError('Please enter a job description');
      return;
    }

    try {
      setLoading(true);
      const result = await analyzeResume(file, jobDescription);
      navigate('/results', { state: { result, jobDescription, fileName: file.name } });
    } catch (error) {
      setError('Failed to analyze resume. Please try again.');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom align="center">
          Resume Matcher
        </Typography>
        <Typography variant="body1" paragraph align="center">
          Upload your resume and enter a job description to see how well they match.
        </Typography>
        
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Box sx={{ mb: 3 }}>
            <Button
              component="label"
              variant="contained"
              startIcon={<CloudUploadIcon />}
              sx={{ mb: 1 }}
            >
              Upload Resume
              <VisuallyHiddenInput type="file" onChange={handleFileChange} />
            </Button>
            <Typography variant="body2" sx={{ ml: 1, display: 'inline' }}>
              {fileName}
            </Typography>
            <Typography variant="caption" display="block" sx={{ mt: 1 }}>
              Accepted formats: PDF, txt
            </Typography>
          </Box>

          <TextField
            fullWidth
            label="Job Description"
            multiline
            rows={6}
            value={jobDescription}
            onChange={handleJobDescriptionChange}
            sx={{ mb: 3 }}
            placeholder="Paste the job description here..."
          />

          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            disabled={loading}
            sx={{ py: 1.5 }}
          >
            {loading ? (
              <CircularProgress size={24} color="inherit" />
            ) : (
              'Analyze Match'
            )}
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}

export default HomePage;