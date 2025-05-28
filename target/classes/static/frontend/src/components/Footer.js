import React from 'react';
import { Box, Typography } from '@mui/material';

function Footer() {
  return (
    <Box component="footer" sx={{ py: 2, textAlign: 'center', bgcolor: 'background.paper', mt: 4 }}>
      <Typography variant="body2" color="text.secondary">
        &copy; {new Date().getFullYear()} Resume Matcher. All rights reserved.
      </Typography>
    </Box>
  );
}

export default Footer;
