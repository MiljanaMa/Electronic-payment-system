import * as React from 'react';
import { useEffect, useState } from 'react'
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {Paper, Box, IconButton, Dialog, DialogActions, DialogTitle, DialogContent, Button} from '@mui/material';
import AddShoppingCartIcon from '@mui/icons-material/AddShoppingCart';
import AutorenewIcon from '@mui/icons-material/Autorenew';
import AssignmentIcon from '@mui/icons-material/Assignment';
import axiosInstance from '../config/AxiosConfig';
import Cookies from 'js-cookie';

export default function Products() {
  
    const[products, setProducts] = useState(null)
    const[bundles, setBundles] = useState(null)
    const [openDialog, setOpenDialog] = useState(false); 
    const [bundleProducts, setBundleProducts] = useState([]); 

    useEffect(() => {
        axiosInstance.get('products').then(response => {
          setProducts(response.data);
        })
        .catch(error => {
          console.error("There was an error fetching the products!", error);
        });
      }, []);

      const handleAddToCartClick = async (purchaseType, purchaseId) => {
        try {
          const response = await axiosInstance.post(`${purchaseType}s/buy`, {
            purchaseId: purchaseId,
            purchaseType: purchaseType,
          });
      
          const { redirectUrl, merchantId } = response.data;
      
          if (redirectUrl && redirectUrl.trim() !== "") {
            //document.cookie = `merchantId=${merchantId}; path=/; Secure; SameSite=Strict`;
            Cookies.set('merchantId', merchantId, {
              path: '/', 
              secure: true, 
              sameSite: 'Strict',
            });
            window.location.href = redirectUrl;
          } else {
          }
        } catch (error) {
          console.error("Error adding product to cart:", error);
        }
      };

      useEffect(() => {
          axiosInstance.get('bundles').then(response => {
            setBundles(response.data);
          })
          .catch(error => {
            console.error("There was an error fetching the bundles!", error);
          });
        }, []);


        const handleDetailsClick = (bundleId) => {
          axiosInstance.get(`bundles/bundle-products/${bundleId}`)
            .then(response => {
              setBundleProducts(response.data);
              setOpenDialog(true);
            })
            .catch(error => {
              console.error("There was an error fetching the bundle products!", error);
            });
        };
      
        const handleCloseDialog = () => {
          setOpenDialog(false);
          setBundleProducts([]);
        };
        const handleSubscribeClick = async (purchaseType, purchaseId) => {
          try {
            const response = await axiosInstance.post(`${purchaseType}s/subscribe`, {
              purchaseId: purchaseId,
              purchaseType: purchaseType,
            });
        
            const { redirectUrl, merchantId } = response.data;
        
            if (redirectUrl && redirectUrl.trim() !== "") {
              //document.cookie = `merchantId=${merchantId}; path=/; Secure; SameSite=Strict`;
              Cookies.set('merchantId', merchantId, {
                path: '/', 
                secure: true, 
                sameSite: 'Strict',
              });
              window.location.href = redirectUrl;
            } else {
            }
        } catch (error) {
          console.error("Error adding product to cart:", error);
        }
            };
  
    return (
   
      <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center" height="20%" padding="2rem" border="ActiveBorder" width="100%">
        <Box margin="10px" justifyContent="center" alignItems="center" >Products</Box>
        <TableContainer component={Paper} style={{width: '70%'}}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell align="center">Name</TableCell>
                <TableCell align="center">Description</TableCell>
                <TableCell align="center">Price</TableCell>
                <TableCell align="center">Buy</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {products !== null? products.map((product, index) => (
                <TableRow
                  key={product.id}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell align="center">{product.name}</TableCell>
                  <TableCell align="center">{product.description}</TableCell>
                  <TableCell align="center">{product.price}</TableCell>
                  <TableCell align='center'>
                      <IconButton color='primary' onClick={() => handleAddToCartClick("product", product.id)}>
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
        
        <Box margin="10px" justifyContent="center" alignItems="center" >Bundles</Box>
        <TableContainer component={Paper} style={{ width: '70%' }}>
        <Table sx={{ minWidth: 650 }} aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell align="center">Name</TableCell>
              <TableCell align="center">Description</TableCell>
              <TableCell align="center">Price</TableCell>
              <TableCell align="center">Details</TableCell>
              <TableCell align="center">Subscribe/Buy</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {bundles !== null ? (
              bundles.map((bundle) => (
                <TableRow key={bundle.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                  <TableCell align="center">{bundle.name}</TableCell>
                  <TableCell align="center">{bundle.description}</TableCell>
                  <TableCell align="center">{bundle.price}</TableCell>
                  
                  <TableCell align="center">
                    <IconButton color="primary" onClick={() => handleDetailsClick(bundle.id)}>
                      <AssignmentIcon />
                    </IconButton>
                  </TableCell>
                  <TableCell align="center">
                    <IconButton color="primary" onClick={() => handleSubscribeClick("bundle", bundle.id)}>
                      <AutorenewIcon  />
                    </IconButton>
                    <IconButton color="primary" onClick={() => handleAddToCartClick("bundle", bundle.id)}>
                      <AddShoppingCartIcon />
                    </IconButton>
                  </TableCell>

                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={5} align="center">
                  Loading...
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Bundle Products</DialogTitle>
        <DialogContent>
          <Table sx={{ minWidth: 500 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell align="center">Product</TableCell>
                <TableCell align="center">Description</TableCell>
                <TableCell align="center">Price</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {bundleProducts.length > 0 ? bundleProducts.map((bundleProduct) => (
                <TableRow key={bundleProduct.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                  <TableCell align="center">{bundleProduct.product.name}</TableCell>
                  <TableCell align="center">{bundleProduct.product.description}</TableCell>
                  <TableCell align="center">{bundleProduct.product.price}</TableCell>
                </TableRow>
              )) : (
                <TableRow>
                  <TableCell colSpan={3} align="center">Loading products...</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary">Close</Button>
        </DialogActions>
      </Dialog>
      </Box>
  )
}
