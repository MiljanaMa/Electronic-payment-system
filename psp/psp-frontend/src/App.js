import './App.css';
import Header from './components/Header';

import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Login from './components/Login';
import Home from './components/Home';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './utils/ProtectedRoute';
import Profile from './components/Profile';
import PaymentSubscription from './components/PaymentSubscription';

function App() {

  const futureFlags = {
    v7_startTransition: true
  };

  return (
      <div className='App'>
        <AuthProvider>
         <BrowserRouter future={futureFlags}>  
         <Header></Header> 
          <Routes>
            <Route path='' element={<Home></Home>} />
            <Route path='/login' element={<Login></Login>} />
            <Route path='/profile' element={<ProtectedRoute><Profile></Profile></ProtectedRoute>} />
            <Route path="/paymentSubscription" element={<PaymentSubscription />} />
          </Routes>
        </BrowserRouter>    
        </AuthProvider>   
    </div>
  );
}

export default App;