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
import { UserSettingsComponent } from './user-settings/user-settings.component';
import {ReactiveFormsModule} from "@angular/forms";
import {NgxMaskModule} from "ngx-mask";
import { ChatComponent } from './chat/chat.component';
import { DocumentsLayoutsComponent } from './documents-layouts/documents-layouts.component';
import { DocumentLayoutsEditorComponent } from './document-layouts-editor/document-layouts-editor.component';



@NgModule({
  declarations: [DashboardComponent, SettingsComponent, HelpComponent, CurrenciesComponent, PaymentMethodsComponent, UserComponent, UserSettingsComponent, ChatComponent, DocumentsLayoutsComponent, DocumentLayoutsEditorComponent],
  imports: [
    CommonModule,
    AdminPanelRoutingModule,
    CommonBlocksModule,
    PerfectScrollbarModule,
    ReactiveFormsModule,
    NgxMaskModule
  ]
})
export class AdminPanelModule { }
