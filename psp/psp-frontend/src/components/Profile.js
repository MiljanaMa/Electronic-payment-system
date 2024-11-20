import * as React from 'react';
import { useEffect, useState } from 'react'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {Paper, Box} from '@mui/material';
import axiosInstance from '../config/AxiosConfig';

export default function Profile() {
  
    const[profile, setProfile] = useState(null)

    useEffect(() => {
        axiosInstance.get('client').then(response => {
            setProfile(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the profile!", error);
        });
      }, []);

    return (
   
<Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" height="20%" padding="2rem" border="ActiveBorder" width="100%">
  <Box margin="10px" justifyContent="center" alignItems="center">Profile</Box>
  <TableContainer component={Paper} style={{ width: '70%' }}>
    <Table sx={{ minWidth: 650 }} aria-label="simple table">
      <TableHead>
        <TableRow>
          <TableCell align="center">Company Name</TableCell>
          <TableCell align="center">Company Email</TableCell>
          <TableCell align="center">Payment Methods</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {profile ? (  // Assuming 'product' is the single product object
          <TableRow
            sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
          >
            <TableCell align="center">{profile.companyName}</TableCell>
            <TableCell align="center">{profile.companyEmail}</TableCell>
            <TableCell align="center">
            {/* Assuming 'paymentMethods' is an array of objects with a 'name' field */}
            {profile.paymentMethods && profile.paymentMethods.length > 0 ? (
              profile.paymentMethods.map((method, index) => (
                <span key={index}>{method.name}{index < profile.paymentMethods.length - 1 ? ', ' : ''}</span>
              ))
            ) : (
              <span>No payment methods available</span>
            )}
          </TableCell>
          </TableRow>
        ) : (
          <TableRow>
            <TableCell colSpan={3} align="center">Loading...</TableCell>  {/* Show loading message if 'product' is null */}
          </TableRow>
        )}
      </TableBody>
    </Table>
  </TableContainer>
</Box>

  )
}
