import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';

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
}
