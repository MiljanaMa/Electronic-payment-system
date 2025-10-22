import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Typography, Button, Box, Container, Grid, TextField, IconButton, InputAdornment } from '@mui/material';
import { Visibility, VisibilityOff } from '@mui/icons-material';
import axiosInstance from '../config/AxiosConfig';

export default function Registration() {

    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        username: '',
        password: '',
        confirmPassword: ''
    });

    const [error, setError] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const toggleShowPassword = () => setShowPassword(prev => !prev);
    const toggleShowConfirmPassword = () => setShowConfirmPassword(prev => !prev);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if(formData.password !== formData.confirmPassword){
            setError("Passwords do not match");
            return;
        }

        const strongPasswordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        if(!strongPasswordRegex.test(formData.password)){
            setError("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
            return;
        }

        setError('');

        try{
            const response = await axiosInstance.post('auth/register', formData);

            if(response.status === 201) {
                navigate('/registrationSuccessful');
            } else {
                const errorText = await response.text();
                setError(errorText);
            }
        } catch(err){
            setError('An error occurred during registration');
        }
    }

  return (
    <Container maxWidth="xs">
      <Box sx={{ mt: 8 }}>
        <Typography variant="h5" align="center" gutterBottom>
          Register
        </Typography>

        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                label="First Name"
                name="firstName"
                fullWidth
                value={formData.firstName}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Last Name"
                name="lastName"
                fullWidth
                value={formData.lastName}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Username"
                name="username"
                fullWidth
                value={formData.username}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Password"
                name="password"
                type={showPassword ? 'text' : 'password'}
                fullWidth
                value={formData.password}
                onChange={handleChange}
                required
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={toggleShowPassword} edge="end">
                        {showPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  )
                }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                label="Confirm Password"
                name="confirmPassword"
                type={showConfirmPassword ? 'text' : 'password'}
                fullWidth
                value={formData.confirmPassword}
                onChange={handleChange}
                required
                InputProps={{
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton onClick={toggleShowConfirmPassword} edge="end">
                        {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                      </IconButton>
                    </InputAdornment>
                  )
                }}
              />
            </Grid>
            {error && (
              <Grid item xs={12}>
                <Typography color="error" variant="body2">
                  {error}
                </Typography>
              </Grid>
            )}
            <Grid item xs={12}>
              <Button type="submit" fullWidth variant="contained" color="primary">
                Register
              </Button>
            </Grid>
          </Grid>
        </form>
      </Box>
    </Container>
  );
};
