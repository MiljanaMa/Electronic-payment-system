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

    const scanQr = async () => {
    try {
        const blob = await fetch(qrImageUrl).then(res => res.blob());

        const formData = new FormData();
        formData.append("file", blob, "qr.png");

        const response = await fetch("http://localhost:5000/scan_qr", {
            method: "POST",
            body: formData
        });

        const data = await response.json();

        if (data.qrText) {
            console.log("Skenirano:", data.qrText);
            alert("Skenirano:\n" + data.qrText);
        } else {
            alert("QR nije pročitan");
        }
    } catch (err) {
        console.error(err);
        alert("Greška pri slanju QR koda backendu");
    }
    };


    if (loading) return <p>Loading QR code...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="nbs-qr-page">
            <h2>NBS QR Payment</h2>
            {qrImageUrl && (
                <div className="qr-container">
                    <img src={qrImageUrl} alt="NBS QR Code" />
                    <button onClick={scanQr}>Skeniraj QR</button>
                </div>
            )}
        </div>
    );
};

export default NbsQrPage;