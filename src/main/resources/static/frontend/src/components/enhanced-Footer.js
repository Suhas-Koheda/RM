import React from 'react';
import { Box, Typography, Container, Grid, Link, Divider } from '@mui/material';

function Footer() {
  return (
    <Box
      component="footer"
      sx={{
        py: 4,
        px: 2,
        mt: 'auto',
        backgroundColor: '#f8f9fa',
        borderTop: '1px solid #e0e0e0'
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4}>
          <Grid item xs={12} md={4}>
            <Typography variant="h6" color="primary" gutterBottom sx={{ fontWeight: 600 }}>
              Resume Matcher
            </Typography>
            <Typography variant="body2" color="text.secondary" paragraph>
              A powerful tool to analyze your resume against job descriptions using AI to improve your match rate and land more interviews.
            </Typography>
          </Grid>
          
          <Grid item xs={6} md={4}>
            <Typography variant="subtitle1" color="text.primary" gutterBottom sx={{ fontWeight: 600 }}>
              Resources
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://github.com/suhas-koheda/resume-matcher" color="inherit" underline="hover">
                GitHub Repository
              </Link>
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://github.com/suhas-koheda/resume-matcher/issues" color="inherit" underline="hover">
                Report Issues
              </Link>
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://github.com/suhas-koheda" color="inherit" underline="hover">
                Developer Profile
              </Link>
            </Typography>
          </Grid>
          
          <Grid item xs={6} md={4}>
            <Typography variant="subtitle1" color="text.primary" gutterBottom sx={{ fontWeight: 600 }}>
              Technologies
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://spring.io/projects/spring-boot" color="inherit" underline="hover">
                Spring Boot
              </Link>
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://reactjs.org/" color="inherit" underline="hover">
                React.js
              </Link>
            </Typography>
            <Typography variant="body2" component="p" gutterBottom>
              <Link href="https://ollama.com/" color="inherit" underline="hover">
                Ollama AI
              </Link>
            </Typography>
          </Grid>
        </Grid>
        
        <Divider sx={{ mt: 4, mb: 3 }} />
        
        <Box sx={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'wrap' }}>
          <Typography variant="body2" color="text.secondary">
            © {new Date().getFullYear()} Resume Matcher. All rights reserved.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Made with ❤️ by <Link href="https://github.com/suhas-koheda" color="inherit" underline="hover">Suhas Koheda</Link>
          </Typography>
        </Box>
      </Container>
    </Box>
  );
}

export default Footer;
