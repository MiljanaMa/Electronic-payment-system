import React, { useState, useEffect } from 'react';
import axios from 'axios';
import axiosInstance from '../config/AxiosConfig';
import {Box, Button} from '@mui/material';


export default function PaymentSubscription() {
  const [availablePaymentMethods, setAvailablePaymentMethods] = useState([]);
  const [selectedPaymentMethods, setSelectedPaymentMethods] = useState([]);
  const [client, setClient] = useState(null);

  useEffect(() => {
    axiosInstance.get('paymentMethod')
      .then(response => {
        setAvailablePaymentMethods(response.data);
      })
      .catch(error => {
        console.error('Error fetching available payment methods', error);
      });
  
    axiosInstance.get('client')
      .then(response => {
        const client = response.data;
        setClient(client);
        setSelectedPaymentMethods(client.paymentMethods.map(method => method.id));
      })
      .catch(error => {
        console.error('Error fetching client payment methods', error);
      });
  }, []);

  const handleCheckboxChange = (paymentMethodId) => {
    setSelectedPaymentMethods((prevSelected) => {
      if (prevSelected.includes(paymentMethodId)) {
        return prevSelected.filter(id => id !== paymentMethodId);
      } else {
        return [...prevSelected, paymentMethodId];
      }
    });
  };

  const handleSubmit = () => {
    // Send updated payment methods to the backend
    axios.put(`/api/client/update-payment-methods`, selectedPaymentMethods)
      .then(response => {
        alert('Payment methods updated successfully');
        // Optionally refresh the client data after updating
        setClient(response.data);
      })
      .catch(error => {
        console.error('Error updating payment methods', error);
      });
  };

  return (
    <div>
        <h4>Select Payment Methods</h4>
        <div
    style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        margin: '0', 
    }}
    >
    <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
        {availablePaymentMethods.map((method) => (
        <li
            key={method.id}
            style={{
            display: 'flex',
            alignItems: 'center',
            marginBottom: '8px',
            }}
        >
            <input
            type="checkbox"
            id={method.id}
            checked={selectedPaymentMethods.includes(method.id)}
            onChange={() => handleCheckboxChange(method.id)}
            style={{ marginRight: '8px' }}
            />
            <label htmlFor={method.id}>{method.name}</label>
        </li>
        ))}
    </ul>
    </div>

      <Box marginTop="20px">
        <Button
          variant="contained"
          color="primary"
          onClick={handleSubmit}>Submit Subscription
        </Button>
      </Box>
    </div>
  );
};
