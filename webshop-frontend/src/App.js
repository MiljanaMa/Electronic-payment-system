import './App.css';
import Header from './components/Header';
import Products from './components/Products';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Registration from './components/Registration';
import RegistrationSuccessful from './components/RegistrationSuccessful';
import Login from './components/Login';
import LoginSuccessful from './components/LoginSuccessful';

function App() {

  const futureFlags = {
    v7_startTransition: true
  };

  return (
      <div className='App'>
         <BrowserRouter future={futureFlags}>  
         <Header></Header> 
          <Routes>
            <Route path='' element={<Products></Products>} />
            <Route path='/products' element={<Products></Products>} />
            <Route path='/registration' element={<Registration></Registration>} />
            <Route path='/registrationSuccessful' element={<RegistrationSuccessful></RegistrationSuccessful>} />
            <Route path='/login' element={<Login></Login>} />
            <Route path='/loginSuccessful' element={<LoginSuccessful></LoginSuccessful>} />
          </Routes>
        </BrowserRouter>       
    </div>
  );
}

export default App;