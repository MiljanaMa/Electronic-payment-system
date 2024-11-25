import * as React from 'react';
import { useEffect, useState } from 'react';
import axiosInstance from '../config/AxiosConfig';
import { useLocation } from 'react-router-dom';
import {Button} from '@mui/material';
import Cookies from 'js-cookie';

export default function PaymentCheckout() {
  const [transactionId, setTransactionId] = useState('240264c0-9c6d-411f-8086-e6ab65ef82ef');
  const [merchantId, setMerchantId] = useState('');
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(null);
  const [error, setError] = useState(null);
  const location = useLocation();

  const formContainerStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '10px', // Space between the radio buttons
  };

  const paymentMethodContainerStyle = {
    display: 'flex',
    flexDirection: 'row', // Row layout for radio buttons and labels
    alignItems: 'center',
    gap: '10px', // Space between the radio button and label
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('transactionId');
   
    if (id) {
      setTransactionId(id);
    }
    /*
    const m = Cookies.get('merchantId')
    console.log(m)*/
    setMerchantId('');
  }, [location]);

  useEffect(() => {
    setMerchantId('123456')
    if (!merchantId || !transactionId) return;
    
    //see if i will need it later
    //Cookies.remove('merchantId');

    axiosInstance.get('paymentMethod/transaction', {
      params: { transactionId, merchantId },
  })
      .then(response => {
        const sortedPaymentMethods = response.data.sort((a, b) => {
          return a.name.localeCompare(b.name);
        });
        setPaymentMethods(sortedPaymentMethods);
      })
      .catch(error => {
        console.error('Error fetching available payment methods', error);
      });
  }, [transactionId, merchantId]);

  const handleCheckout = async () => {
    if (!selectedPaymentMethod) {
      setError('Please select a payment method');
      return;
    }
    const transactionData = {
      merchantId,
      transactionId,
      selectedPaymentMethod
  };

    try {
      const response = await axiosInstance.post('/transaction/checkout', transactionData)
      .then(response1 => {
        Cookies.set('merchantId', merchantId, {
          path: '/', 
          secure: true, 
          sameSite: 'Strict',
        });
        window.location.href = response1.data;
      })
      .catch(error => {
        console.error('Error fetching available payment methods', error);
      });
    } catch (err) {
      console.error('Checkout failed:', err);
      setError('Checkout failed. Please try again.');
    }
  };

  if (paymentMethods.length === 0) {
    return <div>No payment methods available at the moment.</div>;
  }

  return (
    <div>
      {error && <div className="error">{error}</div>}
      <h2>Select a payment method:</h2>
      <form style={formContainerStyle}>
      {paymentMethods.map((method) => (
        <div key={method.id} style={paymentMethodContainerStyle}>
          <input
            type="radio"
            id={method.id}
            name="paymentMethod"
            value={method.id}
            checked={selectedPaymentMethod === method.id}
            onChange={() => setSelectedPaymentMethod(method.id)}
          />
          <label htmlFor={method.id}>{method.name}</label>
        </div>
      ))}
    </form>
      <Button variant="contained" sx={{ marginTop: '20px' }}
          color="primary" onClick={handleCheckout}>Checkout</Button>
    </div>
  );
}
