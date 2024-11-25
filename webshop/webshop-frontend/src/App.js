import './App.css';
import Header from './components/Header';

import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Registration from './components/Registration';
import RegistrationSuccessful from './components/RegistrationSuccessful';
import Login from './components/Login';
import Offers from './components/Offers';
import Transactions from './components/Transactions';
import Home from './components/Home';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './utils/ProtectedRoute';
import theme from './utils/Theme'
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

function App() {

  const futureFlags = {
    v7_startTransition: true
  };

  return (
      <div className='App'>
         <ThemeProvider theme={theme}>
         <CssBaseline />
        <AuthProvider>
         <BrowserRouter future={futureFlags}>  
         <Header></Header> 
          <Routes>
            <Route path='' element={<Home></Home>} />
            <Route path='/login' element={<Login></Login>} />
            <Route path='/registration' element={<Registration></Registration>} />
            <Route path='/registrationSuccessful' element={<RegistrationSuccessful></RegistrationSuccessful>} />

            <Route path='/offers' element={<ProtectedRoute><Offers></Offers></ProtectedRoute>} /> 
            <Route path='/transactions' element={<ProtectedRoute><Transactions></Transactions></ProtectedRoute>} />
          </Routes>
        </BrowserRouter>    
        </AuthProvider>   
        </ThemeProvider>
    </div>
  );
}

export default App;