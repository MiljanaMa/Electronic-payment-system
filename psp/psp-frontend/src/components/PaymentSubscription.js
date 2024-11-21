import React, { useState, useEffect } from 'react';
import axiosInstance from '../config/AxiosConfig';
import {Box, Button} from '@mui/material';
import { useNavigate } from 'react-router-dom';




export default function PaymentSubscription() {
  const [availablePaymentMethods, setAvailablePaymentMethods] = useState([]);
  const [selectedPaymentMethods, setSelectedPaymentMethods] = useState([]);
  const [client, setClient] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    axiosInstance.get('paymentMethod')
      .then(response => {
        const sortedPaymentMethods = response.data.sort((a, b) => {
          return a.name.localeCompare(b.name);
        });
        setAvailablePaymentMethods(sortedPaymentMethods);
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
    axiosInstance.post('client/updatePaymentMethods', selectedPaymentMethods)
      .then(response => {
        alert('Payment methods updated successfully');
        navigate('/profile');
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
