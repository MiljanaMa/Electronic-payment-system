import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Header() {

  const navigate = useNavigate();
  const {isAuthenticated, logout} = useAuth();

  const handleHomeClick = () => {
    navigate('/');
};

  const handleLoginClick = () => {
    navigate('/login');
  };

  const handleRegisterClick = () => {
    navigate('/registration')
  };

  const handleServicesClick = () => {
    navigate('/offers');
};

const handleTransactionsClick = () => {
  navigate('/transactions');
};

  const handleLogoutClick = () => {
    logout();
    navigate('/login');
  };
  


  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}> Webshop App </Typography>

          <Button color="inherit" onClick={handleHomeClick}>Home</Button>

          {!isAuthenticated ? (
            <>
              <Button color="inherit" onClick={handleLoginClick}> Login </Button>
              <Button color="inherit" onClick={handleRegisterClick}> Register </Button>
            </>
          ) : (
            <>
              <Button color="inherit" onClick={handleServicesClick}> Offers </Button>
              <Button color="inherit" onClick={handleTransactionsClick}> Transactions </Button>
              <Button color="inherit" onClick={handleLogoutClick}> Logout </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
    </Box>
  );
}
