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
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Stack,
  Avatar
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import WarningIcon from '@mui/icons-material/Warning';
import ErrorIcon from '@mui/icons-material/Error';
import ArticleIcon from '@mui/icons-material/Article';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import InsightsIcon from '@mui/icons-material/Insights';
import TipsAndUpdatesIcon from '@mui/icons-material/TipsAndUpdates';
import { styled } from '@mui/material/styles';

const StyledPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  borderRadius: '12px',
  boxShadow: '0 8px 24px rgba(0,0,0,0.1)'
}));

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
  
  // Process suggestions to remove markdown code fences
  const processedSuggestions = suggestions ? suggestions
    .replace(/^```html\s*/i, '') // Remove opening ```html
    .replace(/\s*```\s*$/i, '')  // Remove closing ```
    : '';
  
  // Determine status based on match percentage
  const getMatchStatus = (percentage) => {
    if (percentage >= 80) return { color: 'success', icon: CheckCircleIcon, text: 'Excellent Match' };
    if (percentage >= 60) return { color: 'warning', icon: WarningIcon, text: 'Good Match' };
    return { color: 'error', icon: ErrorIcon, text: 'Needs Improvement' };
  };

  const matchStatus = getMatchStatus(matchPercentage);

  const formattedDate = new Intl.DateTimeFormat('en-US', {
    day: 'numeric',
    month: 'long',
    year: 'numeric',
    hour: 'numeric',
    minute: 'numeric'
  }).format(new Date());

  return (
    <Container maxWidth="md" sx={{ mt: 5, mb: 5 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/')}
        sx={{ mb: 3 }}
        color="primary"
        variant="outlined"
      >
        Back to Home
      </Button>

      <StyledPaper>
        <Box sx={{ mb: 4, textAlign: 'center' }}>
          <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
            <InsightsIcon color="primary" sx={{ fontSize: 40 }} />
          </Box>
          <Typography variant="h3" gutterBottom component="h1" sx={{ fontWeight: 600 }}>
            Resume Analysis Results
          </Typography>
          <Typography variant="body1" sx={{ color: 'text.secondary' }}>
            Analysis completed on {formattedDate}
          </Typography>
        </Box>
        
        <Box sx={{ mb: 4 }}>
          <Grid container spacing={2} alignItems="center">
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <ArticleIcon color="primary" sx={{ mr: 1.5 }} />
                <Typography variant="subtitle1">
                  Resume: <strong>{fileName}</strong>
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6} sx={{ textAlign: { sm: 'right' } }}>
              <Chip 
                label={`Analyzed with ${modelUsed}`} 
                color="primary" 
                variant="outlined"
                size="small"
                sx={{ fontWeight: 500 }}
              />
            </Grid>
          </Grid>
        </Box>

        <Divider sx={{ mb: 4 }} />

        <Box sx={{ mb: 5 }}>
          <Card 
            sx={{ 
              borderRadius: 3, 
              bgcolor: `${matchStatus.color}.light`, 
              boxShadow: '0 4px 20px rgba(0,0,0,0.08)',
              position: 'relative',
              overflow: 'hidden'
            }}
          >
            <Box
              sx={{
                position: 'absolute',
                top: 0,
                right: 0,
                width: '100px',
                height: '100px',
                bgcolor: `${matchStatus.color}.main`,
                opacity: 0.1,
                borderRadius: '0 0 0 100%'
              }}
            />
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                <Avatar sx={{ bgcolor: `${matchStatus.color}.main`, mr: 2 }}>
                  <EmojiEventsIcon />
                </Avatar>
                <Typography variant="h5" sx={{ fontWeight: 600 }}>
                  Match Score
                </Typography>
              </Box>
              
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Box sx={{ width: '100%', mr: 2 }}>
                  <LinearProgress
                    variant="determinate"
                    value={matchPercentage}
                    color={matchStatus.color}
                    sx={{ height: 12, borderRadius: 6 }}
                  />
                </Box>
                <Typography
                  variant="h4"
                  color={`${matchStatus.color}.main`}
                  sx={{ fontWeight: 'bold', minWidth: 70 }}
                >
                  {matchPercentage}%
                </Typography>
              </Box>
              
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <matchStatus.icon color={matchStatus.color} sx={{ mr: 1 }} />
                <Typography variant="subtitle1" color={`${matchStatus.color}.main`}>
                  {matchStatus.text}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Box>

        <Box sx={{ mb: 5 }}>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <TipsAndUpdatesIcon color="primary" sx={{ mr: 1.5 }} />
            <Typography variant="h5" sx={{ fontWeight: 600 }}>
              Improvement Suggestions
            </Typography>
          </Box>
          
          <Card variant="outlined" sx={{ borderRadius: 2 }}>
            <CardContent>
              <Box 
                sx={{ 
                  '& h3': { 
                    fontSize: '1.15rem',
                    fontWeight: 'bold',
                    marginTop: '16px',
                    marginBottom: '8px',
                    color: 'text.primary',
                    display: 'flex',
                    alignItems: 'center',
                    '&::before': {
                      content: '""',
                      display: 'inline-block',
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: 'primary.main',
                      marginRight: '12px'
                    }
                  },
                  '& h5': {
                    fontSize: '0.95rem',
                    fontWeight: 'normal',
                    marginTop: '4px',
                    marginBottom: '12px',
                    color: 'text.secondary',
                    paddingLeft: '20px'
                  },
                  '& ul': {
                    paddingLeft: '20px'
                  },
                  '& li': {
                    marginBottom: '8px'
                  }
                }}
                dangerouslySetInnerHTML={{ __html: processedSuggestions }}
              />
            </CardContent>
          </Card>
        </Box>

        <Divider sx={{ mb: 4 }} />

        <Box>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <ArticleIcon color="primary" sx={{ mr: 1.5 }} />
            <Typography variant="h5" sx={{ fontWeight: 600 }}>
              Job Description Summary
            </Typography>
          </Box>
          <Card variant="outlined" sx={{ borderRadius: 2 }}>
            <CardContent>
              <Typography variant="body2" color="text.secondary" sx={{ maxHeight: 200, overflow: 'auto' }}>
                {jobDescription}
              </Typography>
            </CardContent>
          </Card>
        </Box>
      </StyledPaper>
      
      <Box sx={{ mt: 4, textAlign: 'center' }}>
        <Button 
          variant="contained"
          color="primary"
          onClick={() => navigate('/')}
          sx={{ borderRadius: 2, py: 1, px: 3 }}
        >
          Analyze Another Resume
        </Button>
      </Box>
    </Container>
  );
}

export default ResultsPage;
