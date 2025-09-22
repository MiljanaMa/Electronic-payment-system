import React from 'react'
import {Typography, Container, Box } from '@mui/material';
import { useEffect } from 'react'
import axiosInstance from '../config/AxiosConfig';
import Cookies from 'js-cookie';

export default function FailedPage() {
  const getCookieValues = () => {

    return {
      merchantOrderId: Cookies.get('MERCHANT_ORDER_ID'),
      acquirerOrderId: Cookies.get('ACQUIRER_ORDER_ID'),
      paymentId: Cookies.get('PAYMENT_ID'),
      acquirerTimestamp: Cookies.get('ACQUIRER_TIMESTAMP'),
      status: "FAILED"
    };
};
  useEffect(() => {
    const data = getCookieValues();
    axiosInstance.post('subscription/update', data).then(response => {
    })
    .catch(error => {
      console.error("There was an error!", error);
    });
  }, []);
    return (
      <Container maxWidth="xs">
      <Box sx={{ mt: 8, color: 'green' }}>
        <Typography variant="h3" align="center" gutterBottom>
          SUBSCRIPTION FAILED
        </Typography>
      </Box>
      </Container>
  
    )
  }