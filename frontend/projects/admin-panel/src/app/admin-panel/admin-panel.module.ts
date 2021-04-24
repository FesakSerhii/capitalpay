import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {AdminPanelRoutingModule} from './admin-panel-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SettingsComponent } from './settings/settings.component';
import { HelpComponent } from './help/help.component';
import { CurrenciesComponent } from './currencies/currencies.component';
import { PaymentMethodsComponent } from './payment-methods/payment-methods.component';
import { UserComponent } from './user/user.component';
import {CommonBlocksModule} from "../../../../../common-blocks/common-blocks.module";
import {PerfectScrollbarModule} from "ngx-perfect-scrollbar";



@NgModule({
  declarations: [DashboardComponent, SettingsComponent, HelpComponent, CurrenciesComponent, PaymentMethodsComponent, UserComponent],
  imports: [
    CommonModule,
    AdminPanelRoutingModule,
    CommonBlocksModule,
    PerfectScrollbarModule
  ]
})
export class AdminPanelModule { }
