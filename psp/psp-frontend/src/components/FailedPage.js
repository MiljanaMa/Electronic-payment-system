import React from 'react'
import {Typography, Container, Box } from '@mui/material';

export default function FailedPage() {
    return (
      <Container maxWidth="xs">
      <Box sx={{ mt: 8, color: 'green' }}>
        <Typography variant="h3" align="center" gutterBottom>
          PAYMENT FAILED
        </Typography>
      </Box>
      </Container>
  
    )
  }