// src/components/PaymentForm.js
import React, { useState } from 'react';
import axios from 'axios';

const PaymentForm = () => {
    const [paymentData, setPaymentData] = useState({
        PAN: '',
        SECURITY_CODE: '',
        CARD_HOLDER_NAME: '',
        CARD_EXPIRY_DATE: '',
        payment_id: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPaymentData({ ...paymentData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Prvo proveriti da li je payment_id prosleđen
        if (!paymentData.payment_id) {
            alert('Payment ID is required!');
            return;
        }

        try {
            // Slanje podataka na backend (Flask API)
            const response = await axios.post(`http://localhost:5000/process_payment/${paymentData.payment_id}`, paymentData);
            
            // Obrada odgovora
            if (response.status === 200) {
                alert('Payment processed successfully!');
            } else {
                alert('Payment failed!');
            }
        } catch (error) {
            console.error('Error processing payment', error);
            alert('Error processing payment');
        }
    };

    return (
        <div className="payment-form">
            <h2>Payment Form</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Payment ID:</label>
                    <input
                        type="text"
                        name="payment_id"
                        value={paymentData.payment_id}
                        onChange={handleChange}
                        placeholder="Enter Payment ID"
                        required
                    />
                </div>
                <div>
                    <label>PAN:</label>
                    <input
                        type="text"
                        name="PAN"
                        value={paymentData.PAN}
                        onChange={handleChange}
                        placeholder="Enter PAN"
                        required
                    />
                </div>
                <div>
                    <label>Security Code:</label>
                    <input
                        type="text"
                        name="SECURITY_CODE"
                        value={paymentData.SECURITY_CODE}
                        onChange={handleChange}
                        placeholder="Enter Security Code"
                        required
                    />
                </div>
                <div>
                    <label>Card Holder Name:</label>
                    <input
                        type="text"
                        name="CARD_HOLDER_NAME"
                        value={paymentData.CARD_HOLDER_NAME}
                        onChange={handleChange}
                        placeholder="Enter Card Holder Name"
                        required
                    />
                </div>
                <div>
                    <label>Card Expiry Date:</label>
                    <input
                        type="text"
                        name="CARD_EXPIRY_DATE"
                        value={paymentData.CARD_EXPIRY_DATE}
                        onChange={handleChange}
                        placeholder="Enter Expiration Date"
                        required
                    />
                </div>
                <button type="submit">Submit Payment</button>
            </form>
        </div>
    );
};

export default PaymentForm;
