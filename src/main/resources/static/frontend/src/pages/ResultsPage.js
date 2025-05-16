import React from 'react';
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

  if (!result) {
    navigate('/');
    return null;
  }

  const { match, suggestions, modelUsed } = result;
  const matchPercentage = match;
  
  // Determine color based on match percentage
  const getMatchColor = (percentage) => {
    if (percentage >= 80) return 'success.main';
    if (percentage >= 60) return 'warning.main';
    return 'error.main';
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/')}
        sx={{ mb: 2 }}
        color="primary"
      >
        Back to Home
      </Button>

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
            <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>
              {suggestions}
            </Typography>
          </CardContent>
        </Card>

        <Box>
          <Typography variant="h6" gutterBottom>
            Job Description Summary
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ maxHeight: 150, overflow: 'auto' }}>
            {jobDescription}
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default ResultsPage;