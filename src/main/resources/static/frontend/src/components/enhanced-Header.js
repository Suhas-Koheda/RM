import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, Container, useScrollTrigger, Slide } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import DescriptionIcon from '@mui/icons-material/Description';

// Hide AppBar on scroll down
function HideOnScroll(props) {
  const { children } = props;
  const trigger = useScrollTrigger();

  return (
    <Slide appear={false} direction="down" in={!trigger}>
      {children}
    </Slide>
  );
}

function Header() {
  return (
    <HideOnScroll>
      <AppBar 
        position="sticky" 
        elevation={0} 
        sx={{ 
          backgroundColor: 'white',
          borderBottom: '1px solid #e0e0e0'
        }}
      >
        <Container maxWidth="lg">
          <Toolbar disableGutters sx={{ py: 0.5 }}>
            <Box sx={{ 
              display: 'flex', 
              alignItems: 'center', 
              mr: 2,
              color: 'primary.main'
            }}>
              <DescriptionIcon sx={{ fontSize: 32, mr: 1 }} />
              <Typography 
                variant="h5" 
                component={RouterLink} 
                to="/"
                sx={{ 
                  fontWeight: 700, 
                  color: 'primary.main',
                  textDecoration: 'none',
                  letterSpacing: -0.5
                }}
              >
                Resume Matcher
              </Typography>
            </Box>
            
            <Box sx={{ flexGrow: 1 }} />
            
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Button 
                color="primary" 
                component={RouterLink} 
                to="/"
                sx={{ 
                  fontWeight: 500,
                  textTransform: 'none',
                  fontSize: '1rem'
                }}
              >
                Home
              </Button>
              
              <Button 
                variant="contained" 
                color="primary"
                component="a"
                href="https://github.com/suhas-koheda/resume-matcher"
                target="_blank"
                rel="noopener noreferrer"
                sx={{ 
                  ml: 2,
                  borderRadius: 2,
                  textTransform: 'none',
                  fontWeight: 500
                }}
              >
                GitHub
              </Button>
            </Box>
          </Toolbar>
        </Container>
      </AppBar>
    </HideOnScroll>
  );
}

export default Header;
