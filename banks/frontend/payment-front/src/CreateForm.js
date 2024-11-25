import React, { useState, useEffect } from 'react';
import { useLocation, useParams } from 'react-router-dom'; // Dodaj useParams
import axios from 'axios';
import Cookies from 'js-cookie';
import './App.css';

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
    const { paymentId } = useParams();
    const [paymentData, setPaymentData] = useState({
        PAN: '',
        SECURITY_CODE: '',
        CARD_HOLDER_NAME: '',
        CARD_EXPIRY_DATE: '',
        payment_id: paymentId || '' 
    });
    const location = useLocation();
    const [merchantId, setMerchantId] = useState('');
    const [modalMessage, setModalMessage] = useState('');
    const [showModal, setShowModal] = useState(false);

    useEffect(() => {
        const m = Cookies.get('merchantId');
        setMerchantId(m);

        if (paymentId) {
            setPaymentData((prev) => ({ ...prev, payment_id: paymentId }));
        }
    }, [location, paymentId]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPaymentData({ ...paymentData, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!paymentData.payment_id) {
            setModalMessage('Payment ID is required!');
            setShowModal(true);
            return;
        }

        try {
            const response = await axios.post(`http://localhost:5000/process_payment/${paymentData.payment_id}`, paymentData);

            if (response.data.ACQUIRER_ORDER_ID) {
                Cookies.set('ACQUIRER_ORDER_ID', response.data.ACQUIRER_ORDER_ID);
            }
            if (response.data.ACQUIRER_TIMESTAMP) {
                Cookies.set('ACQUIRER_TIMESTAMP', response.data.ACQUIRER_TIMESTAMP);
            }
            if (response.data.MERCHANT_ORDER_ID) {
                Cookies.set('MERCHANT_ORDER_ID', response.data.MERCHANT_ORDER_ID);
            }
            if (response.data.PAYMENT_ID) {
                Cookies.set('PAYMENT_ID', response.data.PAYMENT_ID);
            }
            if (response.data.STATUS_URL) {
                window.location.href = response.data.STATUS_URL; // Preusmerite na STATUS_URL
            }
    
        } catch (error) {
            console.error('Error processing payment', error);
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
                {}
                {}
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

            {showModal && <Modal message={modalMessage} onClose={handleCloseModal} />}
        </div>
    );
};

export default PaymentForm;
