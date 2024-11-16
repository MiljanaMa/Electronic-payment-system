import './App.css';
import Header from './components/Header';

import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Registration from './components/Registration';
import RegistrationSuccessful from './components/RegistrationSuccessful';
import Login from './components/Login';
import Offers from './components/Offers';
import Home from './components/Home';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './utils/ProtectedRoute';

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
            <Route path='/registration' element={<Registration></Registration>} />
            <Route path='/registrationSuccessful' element={<RegistrationSuccessful></RegistrationSuccessful>} />

            <Route path='/offers' element={<ProtectedRoute><Offers></Offers></ProtectedRoute>} /> 
          </Routes>
        </BrowserRouter>    
        </AuthProvider>   
    </div>
  );
}

export default App;