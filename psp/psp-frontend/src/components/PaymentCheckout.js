import * as React from 'react';
import { useEffect, useState } from 'react';
import axiosInstance from '../config/AxiosConfig';
import { useLocation, useNavigate } from 'react-router-dom';

export default function PaymentCheckout() {
  const [transactionId, setTransactionId] = useState(null);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(null);
  const [error, setError] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const id = queryParams.get('transactionId');
   
    if (id) {
      setTransactionId(id);
    }
  }, [location]);

  useEffect(() => {
    if (!transactionId) return; // Prevent the fetch if transactionId is not set yet

    const fetchPaymentMethods = async () => {
      try {
        const response = await axiosInstance.get(`paymentMethod/transaction`, transactionId);
        setPaymentMethods(response.data);
      } catch (err) {
        console.error('Error fetching payment methods:', err);
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

    try {
      const response = await axiosInstance.post('/api/checkout', {
        transactionId,
        paymentMethod: selectedPaymentMethod,
      });

      // Redirect to success page or show confirmation message
      navigate(`/success/${response.data.transactionId}`);
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
}
