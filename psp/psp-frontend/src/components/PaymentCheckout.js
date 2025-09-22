import * as React from 'react';
import { useEffect, useState } from 'react';
import axiosInstance from '../config/AxiosConfig';
import { useLocation } from 'react-router-dom';
import {Button} from '@mui/material';
import Cookies from 'js-cookie';
import axios from 'axios';

export default function PaymentCheckout() {
  const [transactionId, setTransactionId] = useState('');
  const [subscriptionId, setSubscriptionId] = useState('');
  const [merchantId, setMerchantId] = useState('123456');
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [paymentMethodId, setSelectedPaymentMethod] = useState(null);
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
    var id = queryParams.get('transactionId');
    let paymentType;
   
    if (id) {
      setTransactionId(id);
      paymentType = "transaction"
    }else if(queryParams.get("subscriptionId")){
      id = queryParams.get("subscriptionId")
      setSubscriptionId(id);
      paymentType = "subscription"
    }
    //see if i will need it later
    //Cookies.remove('merchantId');
    axiosInstance.get('paymentMethod/methods', {
      params: { id, merchantId, paymentType },
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
  }, [location, merchantId]);

const handleCheckout = async () => {
  if (!paymentMethodId) {
    setError('Please select a payment method');
    return;
  }

  try {
    let response;

    if (subscriptionId) {
      // Subscription flow
      const subscriptionData = {
        merchantId,
        subscriptionId,
        paymentMethodId,
      };

      response = await axiosInstance.post(
        '/subscription/checkout',
        subscriptionData,
        { headers: { 'Content-Type': 'application/json' } }
      );

      window.location.href = response.data;

    } else if (transactionId) {
      // Transaction flow
      const transactionData = {
        merchantId,
        transactionId,
        paymentMethodId,
      };

      response = await axiosInstance.post(
        '/transaction/checkout',
        transactionData,
        { headers: { 'Content-Type': 'application/json' } }
      );

      window.location.href = response.data;

    } else {
      setError('Missing transaction or subscription ID');
      return;
    }
  } catch (err) {
    console.error('Checkout failed:', err);
    setError('Checkout failed. Please try again.');
  }
};

  /*
  try {
    const response = await axios.post(
      'http://localhost:8082/api/transaction/checkout',
      transactionData1,
      {
        headers: { 'Content-Type': 'application/json' }
      }
    );
    console.log('Response:', response.data);
  } catch (err) {
    console.error('Error:', err.response ? err.response.data : err.message);
  }
*/

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
            checked={paymentMethodId === method.id}
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
