import * as React from 'react';
import { useEffect, useState } from 'react'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {Paper, Box, IconButton} from '@mui/material';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import axiosInstance from '../axiosConfig';

export default function Products() {
  
    const[products, setProducts] = useState(null)

    useEffect(() => {
      //change this when you add jwt
        axiosInstance.get('http://localhost:8081/api/products').then(response => {
          setProducts(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the products!", error);
        });
      }, []);
  
    return (
   
      <Box
      display="flex"
      justifyContent="center"
      alignItems="center"
      height="20%"
      padding="2rem"
      border="ActiveBorder"
      width="100%"
      >
        <TableContainer component={Paper} style={{width: '70%'}}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell>Id</TableCell>
                <TableCell align="right">Name</TableCell>
                <TableCell align="right">Price</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {products !== null? products.map((product, index) => (
                <TableRow
                  key={product.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell>{product.id}</TableCell>
                  <TableCell>{product.name}</TableCell>
                  <TableCell>{product.price}</TableCell>
                  <TableCell align='center'>
                      <IconButton color='primary'>
                        < AddShoppingCartIcon/>
                      </IconButton>
                  </TableCell>
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
