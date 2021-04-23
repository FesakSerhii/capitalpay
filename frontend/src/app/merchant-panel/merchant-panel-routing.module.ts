import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MerchantPanelComponent} from './merchant-panel.component';
import {TransactionsLogComponent} from '../../../common-blocks/transactions-log/transactions-log.component';

export const routes: Routes = [
  {
    path: '',
    component: MerchantPanelComponent,
    children:[
      {
        path: 'transactions-log',
        component: TransactionsLogComponent
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MerchantPanelRoutingModule { }
