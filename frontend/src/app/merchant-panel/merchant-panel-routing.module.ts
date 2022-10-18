import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MerchantPanelComponent} from './merchant-panel.component';
import {TransactionsLogComponent} from '../../../common-blocks/transactions-log/transactions-log.component';
import {ApiComponent} from './api/api.component';
import {SettingsComponent} from './settings/settings.component';
import {SupportComponent} from './support/support.component';
import {ToolsComponent} from './tools/tools.component';
import {TransactionsRegistryComponent} from '../../../common-blocks/transactions-log/transactions-registry/transactions-registry.component';
import { ChatComponent } from './chat/chat.component';
import {PaymentLinksComponent} from "./payment-links/payment-links.component";
import {CreatePaymentLinkComponent} from "./payment-links/create-payment-link/create-payment-link.component";

export const routes: Routes = [
  {
    path: '',
    component: MerchantPanelComponent,
    children:[
      {
        path: 'transaction-log',
        component: TransactionsLogComponent
      },
      {
        path: 'transaction-log/registry',
        component: TransactionsRegistryComponent
      },
      {
        path: 'api',
        component: ApiComponent
      },
      {
        path: 'settings',
        component: SettingsComponent
      },
      {
        path: 'support',
        component: SupportComponent
      },
      {
        path: 'support/chat',
        component: ChatComponent
      },
      {
        path: 'tools',
        component: ToolsComponent
      },{
        path: 'payment-links',
        component: PaymentLinksComponent
      }, {
        path: 'payment-links/create',
        component: CreatePaymentLinkComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MerchantPanelRoutingModule { }
