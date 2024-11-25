import React from 'react'
import {Typography, Container, Box } from '@mui/material';
import { useEffect } from 'react'
import axiosInstance from '../config/AxiosConfig';

export default function FailedPage() {
  useEffect(() => {
    const transactionData = {
      merchantOrderId: "6c8c9b28-0743-4f25-80cb-343ffb2db68a",       // Set the actual merchant order ID
      acquirerOrderId: "67890",       // Set the actual acquirer order ID
      paymentId: "abcd1234",          // Set the actual payment ID
      acquirerTimestamp: new Date().toISOString(),  // Set the actual acquirer timestamp (ISO string)
      status: "FAILED"
    };
    axiosInstance.post('transaction/update', transactionData).then(response => {
    })
    .catch(error => {
      console.error("There was an error fetching the profile!", error);
    });
  }, []);
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