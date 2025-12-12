import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import './App.css';

const NbsQrPage = () => {
    const { paymentId } = useParams();
    const [qrImageUrl, setQrImageUrl] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Čuvamo rezultate parsiranja QR
    const [qrData, setQrData] = useState(null);

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

        if (paymentId) fetchQr();
    }, [paymentId]);

    // Parsiranje QR stringa
    const parseQrText = (text) => {
        const parts = text.split("|");
        const data = {};

        parts.forEach(p => {
            if (p.startsWith("N:")) data.nazivPrimaoca = p.substring(2);
            if (p.startsWith("R:")) data.racunPrimaoca = p.substring(2);
            if (p.startsWith("RO:")) data.pozivNaBroj = p.substring(3);
            if (p.startsWith("I:RSD")) {
                data.valuta = "RSD";
                data.iznos = p.substring(5);
            }
            if (p.startsWith("SF:")) data.sifraPlacanja = p.substring(3);
            if (p.startsWith("S:")) data.svrhaPlacanja = p.substring(2);
        });

        return data;
    };

    // Funkcija za slanje QR na backend
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
                const parsed = parseQrText(data.qrText);
                setQrData(parsed);
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

            <div className="qr-layout">
                {/* LEVA STRANA – QR + dugme */}
                <div className="qr-left">
                    {qrImageUrl && (
                        <div className="qr-container">
                            <img src={qrImageUrl} alt="NBS QR Code" />
                            <button onClick={scanQr}>Skeniraj QR</button>
                        </div>
                    )}
                </div>

                {/* SREDNJA KOLONA — PRIMALAC + DETALJI PLAĆANJA */}
                {qrData && (
                    <div className="qr-right">
                        <div className="qr-form">
                            <h3>PRIMALAC</h3>

                            <label>Naziv primaoca:</label>
                            <input type="text" value={qrData.nazivPrimaoca || ""} readOnly />

                            <label>Broj računa primaoca:</label>
                            <input type="text" value={qrData.racunPrimaoca || ""} readOnly />

                            <label>Poziv na broj:</label>
                            <input type="text" value={qrData.pozivNaBroj || ""} readOnly />

                            <h3 style={{ marginTop: "25px" }}>DETALJI PLAĆANJA</h3>

                            <label>Iznos:</label>
                            <input type="text" value={qrData.iznos || ""} readOnly />

                            <label>Valuta:</label>
                            <input type="text" value={qrData.valuta || ""} readOnly />

                            <label>Šifra plaćanja:</label>
                            <input type="text" value={qrData.sifraPlacanja || ""} readOnly />

                            <label>Svrha plaćanja:</label>
                            <input type="text" value={qrData.svrhaPlacanja || ""} readOnly />
                        </div>
                    </div>
                )}

                {qrData && (
                <div className="qr-card-form">
                    <div className="qr-form">
                        <h3>KARTIČNI PODACI</h3>

                        <label>Broj kartice:</label>
                        <input
                            type="text"
                            maxLength="16"
                            placeholder="XXXX XXXX XXXX XXXX"
                        />

                        <label>Datum isteka:</label>
                        <input
                            type="text"
                            placeholder="MM/YY"
                            maxLength="5"
                        />

                        <label>CVV:</label>
                        <input
                            type="text"
                            maxLength="3"
                            placeholder="XXX"
                        />

                        <button className="pay-btn">Plati</button>
                    </div>
                </div>
                )}
            </div>
        </div>
    );
};

export default NbsQrPage;
