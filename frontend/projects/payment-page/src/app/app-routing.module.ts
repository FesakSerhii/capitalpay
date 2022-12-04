import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CardBindingComponent} from './card-binding/card-binding.component';
import {CardPaymentComponent} from './card-payment/card-payment.component';
import {PaymentInvoicingComponent} from './payment-invoicing/payment-invoicing.component';

const routes: Routes = [
  {
    path: '',
    component: CardBindingComponent
  },
  {
    path: 'payment',
    component: CardPaymentComponent
  },{
    path: 'invoicing',
    component: PaymentInvoicingComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
