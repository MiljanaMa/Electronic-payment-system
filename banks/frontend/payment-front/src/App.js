// src/App.js
import React from 'react';
import PaymentForm from './CreateForm';
import NbsQrPage from './NbsQrPage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
const futureFlags = {
    v7_startTransition: true
  };

function App() {
    return (
        <BrowserRouter future={futureFlags}>  
         <Routes>
            <Route path='/:paymentId' element={<PaymentForm />} />
            <Route path="/qr/:paymentId" element={<NbsQrPage />} />

         </Routes>
       </BrowserRouter> 
    );
}

export default App;
