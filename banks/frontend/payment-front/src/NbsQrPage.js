import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './App.css';

const NbsQrPage = () => {
    const { paymentId } = useParams(); // uzimamo PAY-xxxx iz URL-a
    const [qrImageUrl, setQrImageUrl] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchQr = async () => {
            try {
                const response = await fetch(`http://localhost:5000/get_qr/${paymentId}`);
                if (!response.ok) throw new Error('Failed to fetch QR code');
                
                const blob = await response.blob();
                const url = URL.createObjectURL(blob);
                setQrImageUrl(url);
                setLoading(false);
            } catch (err) {
                console.error(err);
                setError('Failed to load QR code');
                setLoading(false);
            }
        };

        if (paymentId) {
            fetchQr();
        }
    }, [paymentId]);

    if (loading) return <p>Loading QR code...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="nbs-qr-page">
            <h2>NBS QR Payment</h2>
            {qrImageUrl && (
                <div className="qr-container">
                    <img src={qrImageUrl} alt="NBS QR Code" />
                </div>
            )}
        </div>
    );
};

export default NbsQrPage;