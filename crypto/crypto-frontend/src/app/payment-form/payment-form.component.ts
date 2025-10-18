import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import Web3 from 'web3';
import { PaymentService } from '../service/payment.service';
import { ActivatedRoute } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-payment-form',
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './payment-form.component.html',
  styleUrl: './payment-form.component.css'
})
export class PaymentFormComponent {
  private paymentService = inject(PaymentService);
  private cookieService = inject(CookieService);
  private route = inject(ActivatedRoute);
  web3: Web3 | undefined;
  amountUSD: number = 0.5;
  amountETH: number = 0;
  senderWallet: string = '';
  receiverWallet: string = '';
  paymentId: string | null = '';

  constructor() {
    if (typeof (window as any).ethereum !== 'undefined') {
      this.web3 = new Web3((window as any).ethereum);
    }
    this.enableMetaMask();
  }

  ngOnInit(): void {
    this.paymentId = this.route.snapshot.paramMap.get('id');

    if (this.paymentId) {
      this.paymentService.getTransaction(this.paymentId).subscribe({
        next: (res) => {
          this.amountUSD = res.amount;
          this.receiverWallet = res.receiverWallet;
          this.paymentService.convertUsdToEth(this.amountUSD).subscribe({
            next: (eth) => this.amountETH = eth,
            error: (err) => console.error(err)
          });
        },
        error: (err) => console.error('Failed to initialize payment:', err)
      });
    }

    this.enableMetaMask();
    this.getSenderAccount();
  }

  async enableMetaMask(): Promise<void> {
    if (typeof (window as any).ethereum !== 'undefined') {
      console.log('MetaMask is installed!');
    } else {
      console.log('MetaMask is not installed!');
      return;
    }

    try {
      await (window as any).ethereum.request({ method: 'eth_requestAccounts' });
      console.log('MetaMask account access granted');
    } catch (error) {
      console.error('User denied account access');
    }
  }

  async getSenderAccount(): Promise<void> {
    if (!this.web3) {
      console.error("Web3 not initialized");
      return;
    }
    const acounts = await this.web3.eth.getAccounts();
    this.senderWallet = acounts[0];
  }

  async sendEth(): Promise<void> {
    if (!this.web3) {
      console.error("Web3 not initialized");
      return;
    }

    try {
      const from = this.senderWallet;
      const to = this.receiverWallet;
      const value = this.web3.utils.toWei(this.amountETH.toString(), 'ether');

      const gas = await this.web3.eth.estimateGas({
        from,
        to,
        value
      });

      const response = await this.web3.eth.sendTransaction({
        from,
        to,
        value,
        gas
      });

      console.log(response);

      const transactionHashString = this.web3.utils.bytesToHex(response.transactionHash);
      const etherscanLink = `https://sepolia.etherscan.io/tx/${transactionHashString}`;
      Swal.fire({
        title: 'Transaction Successful!',
        html: `View on Etherscan: <a href="${etherscanLink}" target="_blank">${transactionHashString}</a>`,
        icon: 'success',
        allowOutsideClick: false, 
        allowEscapeKey: false,
        showConfirmButton: true,
        confirmButtonText: 'Continue'
      }).then(() => {

        if (this.paymentId) {
          this.paymentService.completeTransaction(this.paymentId, transactionHashString, "SUCCESSFUL")
            .subscribe({
            next: (res) => {
              console.log('Transaction updated in backend:', res);
              this.cookieService.set('MERCHANT_ORDER_ID', res.merchantOrderId, undefined, '/');
              window.location.href = res.statusURL;
            },
            error: (err) => console.error('Failed to update transaction:', err)
          });
          }
        });
      
      } catch (error: any) {
        console.error(error);
        alert(`Transaction failed: ${error.message || error}`);
      }
  }

  submitForm() {
    this.sendEth()
  }
}
