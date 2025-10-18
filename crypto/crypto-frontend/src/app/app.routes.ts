import { Routes } from '@angular/router';
import { PaymentFormComponent } from './payment-form/payment-form.component';

export const routes: Routes = [
      {path: ':id', component: PaymentFormComponent}
];
