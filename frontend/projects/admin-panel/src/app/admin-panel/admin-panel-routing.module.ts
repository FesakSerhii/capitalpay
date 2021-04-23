import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {DashboardComponent} from './dashboard/dashboard.component';
import {AdminPanelComponent} from './admin-panel.component';
import {SettingsComponent} from './settings/settings.component';
import {HelpComponent} from './help/help.component';
import {CurrenciesComponent} from './currencies/currencies.component';
import {PaymentMethodsComponent} from './payment-methods/payment-methods.component';
import {UserComponent} from './user/user.component';
import {TransactionsLogComponent} from '../../../../../common-blocks/transactions-log/transactions-log.component';


const routes: Routes = [
  {
    path: '',
    component: AdminPanelComponent,
    children:[
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'transaction-log',
        component: TransactionsLogComponent
      },
      {
        path: 'settings',
        component: SettingsComponent
      },
      {
        path: 'help',
        component: HelpComponent
      },
      {
        path: 'currencies',
        component: CurrenciesComponent
      },
      {
        path: 'payment-methods',
        component: PaymentMethodsComponent
      },
      {
        path: 'user',
        component: UserComponent
      },
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminPanelRoutingModule { }
