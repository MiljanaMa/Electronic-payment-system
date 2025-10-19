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

export default function Products() {
  
    const[transactions, setTransactions] = useState(null)
    const[subscriptions, setSubscriptions] = useState(null)

    useEffect(() => {
        axiosInstance.get('/webshop/transactions').then(response => {
            setTransactions(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the transactions!", error);
        });
        axiosInstance.get('/webshop/subscriptions').then(response => {
            setSubscriptions(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the subscriptions!", error);
        });
      }, []);
      
      

    return (
   
      <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" height="20%" padding="2rem" border="ActiveBorder" width="100%">
        <Box margin="10px" justifyContent="center" alignItems="center"> Transactions </Box>
        <TableContainer component={Paper} style={{width: '70%'}}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell align="center">Purchase Id</TableCell>
                <TableCell align="center">Amount</TableCell>
                <TableCell align="center">Type</TableCell>
                <TableCell align="center">Date</TableCell>
                <TableCell align="center">Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions !== null? transactions.map((transaction, index) => (
                <TableRow
                  key={transaction.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell align="center">{transaction.purchaseId}</TableCell>
                  <TableCell align="center">{transaction.amount}</TableCell>
                  <TableCell align="center">{transaction.type}</TableCell>
                  <TableCell align="center">
                    {new Date(transaction.timestamp).toLocaleString('en-US', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit',
                        hour12: false,
                    })}
                </TableCell>
                  <TableCell align="center">{transaction.status}</TableCell>                
                </TableRow>
              )): (
                  <TableRow>
                    <TableCell colSpan={4} align="center">Loading...</TableCell>
                  </TableRow>
                  )} 
            </TableBody>
          </Table>
        </TableContainer>
        <Box marginTop="100px" justifyContent="center" alignItems="center"> Subscriptions </Box>
        <TableContainer component={Paper} style={{width: '70%'}}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell align="center">Purchase Id</TableCell>
                <TableCell align="center">Amount</TableCell>
                <TableCell align="center">Type</TableCell>
                <TableCell align="center">Date</TableCell>
                <TableCell align="center">Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {transactions !== null? transactions.map((transaction, index) => (
                <TableRow
                  key={transaction.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell align="center">{transaction.purchaseId}</TableCell>
                  <TableCell align="center">{transaction.amount}</TableCell>
                  <TableCell align="center">{transaction.type}</TableCell>
                  <TableCell align="center">
                    {new Date(transaction.timestamp).toLocaleString('en-US', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit',
                        hour12: false,
                    })}
                </TableCell>
                  <TableCell align="center">{transaction.status}</TableCell>                
                </TableRow>
              )): (
                  <TableRow>
                    <TableCell colSpan={4} align="center">Loading...</TableCell>
                  </TableRow>
                  )} 
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
      
      
  )
}
