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
import {UserSettingsComponent} from './user-settings/user-settings.component';
import {ChatComponent} from './chat/chat.component';
import {DocumentsLayoutsComponent} from './documents-layouts/documents-layouts.component';
import {DocumentLayoutsEditorComponent} from './document-layouts-editor/document-layouts-editor.component';
import {TransactionsRegistryComponent} from '../../../../../common-blocks/transactions-log/transactions-registry/transactions-registry.component';
import {CardCheckComponent} from "./card-check/card-check.component";
import {TerminalsComponent} from "./terminals/terminals.component";


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
        path: 'transaction-log/registry',
        component: TransactionsRegistryComponent
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
        path: 'help/chat',
        component: ChatComponent
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
      {
        path: 'user/settings',
        component: UserSettingsComponent
      },
      {
        path: 'documents-layouts',
        component: DocumentsLayoutsComponent
      },
      {
        path: 'documents-layouts/editor',
        component: DocumentLayoutsEditorComponent
      },
      {
        path: 'card-check',
        component: CardCheckComponent
      },
      {
        path: 'card-err',
        component: CardCheckComponent
      },
      {
        path: "terminals",
        component: TerminalsComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminPanelRoutingModule { }
