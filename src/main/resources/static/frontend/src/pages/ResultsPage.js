import React, { useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Button,
  Box,
  Divider,
  LinearProgress,
  Grid,
  Card,
  CardContent,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';

function ResultsPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { result, jobDescription, fileName } = location.state || {};

  useEffect(() => {
    if (!localStorage.getItem('accessToken')) {
      navigate('/auth');
    }
  }, [navigate]);

  if (!result) {
    navigate('/');
    return null;
  }

  const { match, suggestions, modelUsed } = result;
  const matchPercentage = match;

  // Process suggestions to remove markdown code fences
  const processedSuggestions = suggestions ? suggestions
    .replace(/^```html\s*/i, '') // Remove opening ```html
    .replace(/\s*```\s*$/i, '')  // Remove closing ```
    : '';

  // Determine color based on match percentage
  const getMatchColor = (percentage) => {
    if (percentage >= 80) return 'success.main';
    if (percentage >= 60) return 'warning.main';
    return 'error.main';
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Box sx={{ display: 'flex', mb: 2, gap: 2 }}>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/')}
          color="primary"
        >
          Back to Home
        </Button>
        <Button
          onClick={() => navigate('/resumes')}
          color="secondary"
          variant="outlined"
        >
          View All Resumes
        </Button>
      </Box>
      <Paper elevation={3} sx={{ p: 4 }}>
        <Typography variant="h4" gutterBottom align="center">
          Resume Analysis Results
        </Typography>
        <Box sx={{ mb: 4 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle1">
                Resume: <strong>{fileName}</strong>
              </Typography>
            </Grid>
            <Grid item xs={12} sm={6} textAlign={{ sm: 'right' }}>
              <Typography variant="subtitle1">
                Model: <strong>{modelUsed}</strong>
              </Typography>
            </Grid>
          </Grid>
        </Box>
        <Divider sx={{ mb: 4 }} />
        <Box sx={{ mb: 4 }}>
          <Typography variant="h6" gutterBottom>
            Match Score
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
            <Box sx={{ width: '100%', mr: 1 }}>
              <LinearProgress
                variant="determinate"
                value={matchPercentage}
                color={
                  matchPercentage >= 80 ? "success" :
                  matchPercentage >= 60 ? "warning" : "error"
                }
                sx={{ height: 10, borderRadius: 5 }}
              />
            </Box>
            <Typography
              variant="h5"
              color={getMatchColor(matchPercentage)}
              sx={{ fontWeight: 'bold', minWidth: 60 }}
            >
              {matchPercentage}%
            </Typography>
          </Box>
        </Box>
        <Card variant="outlined" sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Suggestions to Improve Your Match
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
              dangerouslySetInnerHTML={{ __html: processedSuggestions }}
            />
          </CardContent>
        </Card>
        <Box>
          <Typography variant="h6" gutterBottom>
            Job Description Summary
          </Typography>
          <Paper variant="outlined" sx={{ p: 2, mt: 1 }}>
            <Typography variant="body2" color="text.secondary">
              {jobDescription}
            </Typography>
          </Paper>
        </Box>
      </Paper>
    </Container>
  );
}

export default ResultsPage;
