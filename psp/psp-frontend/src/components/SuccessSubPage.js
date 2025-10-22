import React from 'react'
import {Typography, Container, Box } from '@mui/material';
import axiosInstance from '../config/AxiosConfig';
import { useEffect } from 'react';
import Cookies from 'js-cookie';

export default function SuccessPage() {
  const getCookieValues = () => {

    return {
      merchantSubscriptionId: Cookies.get('MERCHANT_ORDER_ID'),
      acquirerOrderId: Cookies.get('ACQUIRER_ORDER_ID'),
      paymentId: Cookies.get('PAYMENT_ID'),
      acquirerTimestamp: Cookies.get('ACQUIRER_TIMESTAMP'),
      status: "ACTIVE"
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
          SUBSCRIPTION IS NOW ACTIVE
        </Typography>
      </Box>
      </Container>
  
    )
  }