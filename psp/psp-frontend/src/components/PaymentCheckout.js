import * as React from 'react';
import { useEffect, useState } from 'react'
import axiosInstance from '../config/AxiosConfig';
import { useLocation } from 'react-router-dom';

export default function PaymentCheckout() {
  
  const [transactionId, setTransactionId] = useState(null);
  const location = useLocation();
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    console.log('Location search:', location.search);  // Check if the query string is correct
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('transactionId');
   
    if (id) {
      setTransactionId(id);
    }
  }, [location]);

  useEffect(() => {
    if (!transactionId) {
      return; // Prevent the fetch if transactionId is not set yet
    }
  
    console.log('Component mounted or transactionId changed');
    const fetchPaymentMethods = async () => {
      try {
        const response = await axiosInstance.get(`paymentMethod/${transactionId}`);
        setPaymentMethods(response.data);
      } catch (err) {
        setError('Failed to load payment methods');
      }
    };
  
    fetchPaymentMethods();
  }, [transactionId]);

  const handleCheckout = async () => {
    if (!selectedPaymentMethod) {
      setError('Please select a payment method');
      return;
    }
/*
    try {
      // Assuming you send the selected payment method and transaction ID to the backend to process the checkout
      const response = await axios.post('/api/checkout', {
        transactionId,
        paymentMethod: selectedPaymentMethod,
      });

      // Redirect to success page or show confirmation message
      history.push(`/success/${response.data.transactionId}`);
    } catch (err) {
      setError('Checkout failed. Please try again.');
    }*/
  };

  return (
    <div>
      <h1>Payment Page</h1>
      {error && <div className="error">{error}</div>}
      <h2>Select a payment method:</h2>
      <form>
        {paymentMethods.map((method) => (
          <div key={method.id}>
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
      <button onClick={handleCheckout}>Checkout</button>
    </div>
  );
};
