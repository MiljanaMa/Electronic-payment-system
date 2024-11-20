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

  const handleProfileClick = () => {
    navigate('/profile');
};

  const handleLogoutClick = () => {
    logout();
    navigate('/login');
  };
  


  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}> PSP App </Typography>

          <Button color="inherit" onClick={handleHomeClick}>Home</Button>

          {!isAuthenticated ? (
            <>
              <Button color="inherit" onClick={handleLoginClick}> Login </Button>
            </>
          ) : (
            <>
              <Button color="inherit" onClick={handleProfileClick}> Profile </Button>
              <Button color="inherit" onClick={handleLogoutClick}> Logout </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
    </Box>
  );
}
