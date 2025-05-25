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
  Alert,
  Grid,
  Card,
  CardContent,
  Stepper,
  Step,
  StepLabel,
  Divider,
  Chip
} from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DescriptionIcon from '@mui/icons-material/Description';
import AnalyticsIcon from '@mui/icons-material/Analytics';
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

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: '12px',
  boxShadow: '0 8px 24px rgba(0,0,0,0.1)'
}));

const FileInfoCard = styled(Card)(({ theme }) => ({
  marginTop: theme.spacing(2),
  marginBottom: theme.spacing(2),
  borderRadius: '8px',
  border: `1px solid ${theme.palette.divider}`,
  boxShadow: 'none'
}));

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
      console.error('Error during analysis:', error);
      setError(
        error.response?.status === 500
          ? 'Server error. The AI service might be unavailable. Please try again later.'
          : 'Failed to analyze resume. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  const steps = [
    'Upload Resume', 
    'Enter Job Description', 
    'Get Analysis'
  ];

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 5 }}>
      <StyledPaper>
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Typography variant="h3" gutterBottom component="h1" sx={{ fontWeight: 600 }}>
            Resume Matcher
          </Typography>
          <Typography variant="body1" sx={{ fontSize: '1.1rem', color: 'text.secondary', maxWidth: '650px', mx: 'auto' }}>
            Upload your resume and enter a job description to see how well they match. Get personalized AI feedback to improve your chances.
          </Typography>
        </Box>

        <Stepper activeStep={0} alternativeLabel sx={{ mb: 4 }}>
          {steps.map((label) => (
            <Step key={label}>
              <StepLabel>{label}</StepLabel>
            </Step>
          ))}
        </Stepper>
        
        {error && (
          <Alert severity="error" sx={{ mb: 3 }}>
            {error}
          </Alert>
        )}

        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Grid container spacing={4}>
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                <CloudUploadIcon sx={{ mr: 1 }} /> 
                Upload Your Resume
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                We support PDF and text files. Make sure your resume is up-to-date before uploading.
              </Typography>

              <Box 
                sx={{ 
                  border: '2px dashed #1976d2', 
                  borderRadius: 2, 
                  p: 3, 
                  textAlign: 'center',
                  backgroundColor: 'rgba(25, 118, 210, 0.04)',
                  cursor: 'pointer',
                  mb: 2,
                  transition: 'all 0.2s',
                  '&:hover': {
                    backgroundColor: 'rgba(25, 118, 210, 0.08)',
                  }
                }}
                component="label"
              >
                <input 
                  type="file" 
                  accept=".pdf,.txt" 
                  hidden
                  onChange={handleFileChange} 
                />
                <CloudUploadIcon fontSize="large" color="primary" />
                <Typography variant="subtitle1" sx={{ mt: 1 }}>
                  Drag & drop or click to browse
                </Typography>
                <Typography variant="caption" display="block" color="text.secondary">
                  PDF, TXT (Max 10MB)
                </Typography>
              </Box>

              {file && (
                <FileInfoCard variant="outlined">
                  <CardContent sx={{ py: 1.5 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <DescriptionIcon sx={{ mr: 1.5, color: 'primary.main' }} />
                      <Box sx={{ flexGrow: 1 }}>
                        <Typography variant="subtitle2" noWrap>
                          {fileName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {file.size ? `${(file.size / 1024).toFixed(1)} KB` : ''}
                        </Typography>
                      </Box>
                      <Chip 
                        label={file.type === 'application/pdf' ? 'PDF' : 'TXT'} 
                        size="small" 
                        color="primary" 
                        variant="outlined" 
                      />
                    </Box>
                  </CardContent>
                </FileInfoCard>
              )}
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                <DescriptionIcon sx={{ mr: 1 }} /> 
                Job Description
              </Typography>
              <Typography variant="body2" color="text.secondary" paragraph>
                Copy and paste the complete job description to get the most accurate analysis.
              </Typography>
              <TextField
                fullWidth
                label="Job Description"
                multiline
                rows={8}
                value={jobDescription}
                onChange={handleJobDescriptionChange}
                placeholder="Paste the job description here..."
                variant="outlined"
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 4 }} />
          
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={loading}
              size="large"
              sx={{ 
                py: 1.5, 
                px: 4, 
                fontSize: '1.1rem',
                borderRadius: 2,
                minWidth: '220px'
              }}
              startIcon={loading ? null : <AnalyticsIcon />}
            >
              {loading ? (
                <CircularProgress size={24} color="inherit" />
              ) : (
                'Analyze Resume'
              )}
            </Button>
          </Box>
        </Box>
      </StyledPaper>

      <Box sx={{ mt: 4, textAlign: 'center' }}>
        <Typography variant="body2" color="text.secondary">
          Your files are analyzed securely. We don't store your resume or job descriptions.
        </Typography>
      </Box>
    </Container>
  );
}

export default HomePage;
