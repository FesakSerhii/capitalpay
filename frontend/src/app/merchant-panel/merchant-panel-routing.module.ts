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
import {PaymentLinksShopComponent} from "./payment-links/payment-links-shop/payment-links-shop.component";
import {PaymentLinksHistoryComponent} from "./payment-links/payment-links-history/payment-links-history.component";

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
        path: 'payment-links/:shop-id',
        component: PaymentLinksShopComponent
      }, {
        path: 'payment-links/:shop-id/history',
        component: PaymentLinksHistoryComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MerchantPanelRoutingModule { }
