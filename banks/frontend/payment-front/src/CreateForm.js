import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

// Modal Component
const Modal = ({ message, onClose }) => {
    return (
        <div className="modal">
            <div className="modal-content">
                <span className="close" onClick={onClose}>&times;</span>
                <p>{message}</p>
            </div>
        </div>
    );
};

const PaymentForm = () => {
    const [paymentData, setPaymentData] = useState({
        PAN: '',
        SECURITY_CODE: '',
        CARD_HOLDER_NAME: '',
        CARD_EXPIRY_DATE: '',
        payment_id: ''
    });

    const [modalMessage, setModalMessage] = useState('');
    const [showModal, setShowModal] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPaymentData({ ...paymentData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Check if payment_id is provided
        if (!paymentData.payment_id) {
            setModalMessage('Payment ID is required!');
            setShowModal(true);
            return;
        }

        try {
            // Sending data to the backend (Flask API)
            const response = await axios.post(`http://localhost:5000/process_payment/${paymentData.payment_id}`, paymentData);
            
            // Handle server response
            if (response.status === 200) {
                setModalMessage(response.data.message || 'Payment processed successfully!');
            } else {
                setModalMessage(response.data.message || 'Payment failed!');
            }
        } catch (error) {
            console.error('Error processing payment', error);
            setModalMessage('Error processing payment');
        }

        setShowModal(true);
    };

    const handleCloseModal = () => {
        setShowModal(false);
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

            {/* Display Modal */}
            {showModal && <Modal message={modalMessage} onClose={handleCloseModal} />}
        </div>
    );
};

export default PaymentForm;
