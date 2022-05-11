import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ReactiveFormsModule} from '@angular/forms';
import {PaymentFormComponent} from './payment-form/payment-form.component';
import {CommonBlocksModule} from '../../../../common-blocks/common-blocks.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {HttpClientModule} from '@angular/common/http';
import {IConfig, NgxMaskModule} from 'ngx-mask';
import { CardBindingComponent } from './card-binding/card-binding.component';
import { CardPaymentComponent } from './card-payment/card-payment.component';

const maskConfigFunction: () => Partial<IConfig> = () => {
  return {
    validation: true,
  };
};
@NgModule({
  declarations: [
    AppComponent,
    PaymentFormComponent,
    CardBindingComponent,
    CardPaymentComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
    ReactiveFormsModule,
    CommonBlocksModule,
    NgxMaskModule,
    NgxMaskModule.forRoot(maskConfigFunction),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
