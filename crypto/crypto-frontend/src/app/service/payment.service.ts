import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from '../../env/environment';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  ethAmount: number = 0;
  private http = inject(HttpClient);
  
  convertUsdToEth(usdAmount: number): Observable<number> {
    return this.http.get<any>('https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd')
      .pipe(
        map(response => {
          const ethPrice = response.ethereum.usd;
          this.ethAmount = usdAmount / ethPrice;
          console.log(`$${usdAmount} = ${this.ethAmount} ETH`);
          return this.ethAmount;
      })
      );
  }

  getTransaction(id: string): Observable<{id: string, amount: number, receiverWallet: string }> {
    return this.http.get<{id: string, amount: number, receiverWallet: string }>(
      `${environment.apiUrl}transactions/${id}`
    );
  }

  completeTransaction(id: string, transactionHash: string, status: 'SUCCESSFUL' | 'FAILED'): Observable<{ merchantOrderId: string; statusURL: string }> {
    return this.http.put<{ merchantOrderId: string; statusURL: string }>(
      `${environment.apiUrl}transactions/${id}/complete`,
      {
        transactionHash,
        status
      }
    );
  }

}
