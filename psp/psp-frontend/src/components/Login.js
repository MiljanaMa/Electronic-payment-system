import React, { useState } from 'react';
import { Typography, Button, Box, Container, TextField } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../config/AxiosConfig';
import { useAuth } from '../context/AuthContext';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const {login} = useAuth();

    const handleLogin = async (e) => {
      e.preventDefault();
      setError('');

      const loginData = {
          username,
          password
      };

      try {
          const response = await axiosInstance.post('auth/login', loginData);
          if (response.status === 200 && response.data.token) {
              login(response.data.token);
              navigate('/offers');
          } else {
              setError('Login failed. Please try again!');
          }
      } catch (error) {
          setError('An error occurred. Please try again.');
      }
  };

    return (
        <Container maxWidth="xs">
            <Box component="form" onSubmit={handleLogin} sx={{ mt: 4, display: 'flex', flexDirection: 'column', gap: 2 }}>
                <Typography variant="h5" component="h1" align="center">
                    Login
                </Typography>

                {error && (
                    <Typography variant="body2" color="error" align="center">
                        {error}
                    </Typography>
                )}

                <TextField
                    label="Username"
                    variant="outlined"
                    fullWidth
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />

                <TextField
                    label="Password"
                    variant="outlined"
                    type="password"
                    fullWidth
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                <Button type="submit" variant="contained" color="primary" fullWidth>
                    Login
                </Button>
            </Box>
        </Container>
    );
}
