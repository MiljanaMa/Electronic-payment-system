import { CommonModule, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import Web3 from 'web3';
import { PaymentService } from '../service/payment.service';
import { Transaction } from '../model/transaction.model';

@Component({
  selector: 'app-payment-form',
  imports: [FormsModule, ReactiveFormsModule, NgIf, CommonModule],
  templateUrl: './payment-form.component.html',
  styleUrl: './payment-form.component.css'
})
export class PaymentFormComponent {
  private paymentService = inject(PaymentService);
  paymentForm: FormGroup;
  web3: Web3 | undefined;
  amountUSD: number = 0.5;
  amountETH: number = 0;
  senderWallet: string = '';

  constructor(private fb: FormBuilder) {
    this.paymentForm = this.fb.group({
      receiverWallet: ['', [Validators.required]]
    });

    if (typeof (window as any).ethereum !== 'undefined') {
      this.web3 = new Web3((window as any).ethereum);
    }
    this.enableMetaMask();
  }

  ngOnInit(): void {
    this.paymentService.convertUsdToEth(this.amountUSD).subscribe({
      next: result => this.amountETH = result,
      error: (error: any) => console.log(error),
    });

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

    const address = acounts[0];
    const balanceWei = await this.web3.eth.getBalance(address);
    const balanceEth = this.web3.utils.fromWei(balanceWei, "ether");
    console.log(`Balance: ${balanceEth} ETH`);
  }

  async sendEth(): Promise<void> {
    if (!this.web3) {
      console.error("Web3 not initialized");
      return;
    }

    try {
      const acounts = await this.web3.eth.getAccounts();
      const from = acounts[0];
      const to = this.paymentForm.value.receiverWallet;
      const value = this.web3.utils.toWei(this.amountETH.toString(), 'ether');

      const gas = await this.web3.eth.estimateGas({
        from,
        to,
        value
      });

      console.log("Estimated gas:", gas);

      const response = await this.web3.eth.sendTransaction({
        from,
        to,
        value,
        gas
      });

      console.log(response);
      alert(`Transaction successful: ${response.transactionHash}`);

      const transaction: Transaction = {
        id: Math.floor(Math.random() * 1_000_000_000),
        senderWalletId: from,
        receiverWalletId: to,
        amount: this.amountETH,
        transactionHash: response.transactionHash.toString()
      }
      console.log('Transaction for back', transaction)

    } catch (error: any) {
      console.error(error);
      alert(`Transaction failed: ${error.message || error}`);
    }
  }


  submitForm() {
    if (this.paymentForm.valid) {
      this.sendEth()
    } else {
      alert('Please fill the form correctly.');
    }
  }
}
