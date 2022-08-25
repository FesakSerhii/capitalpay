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
import { UserSettingsComponent } from './user-settings/user-settings.component';
import {ReactiveFormsModule} from "@angular/forms";
import {NgxMaskModule} from "ngx-mask";
import { ChatComponent } from './chat/chat.component';
import { DocumentsLayoutsComponent } from './documents-layouts/documents-layouts.component';
import { DocumentLayoutsEditorComponent } from './document-layouts-editor/document-layouts-editor.component';
import {NgScrollbarModule} from 'ngx-scrollbar';
import {NgbDateParserFormatter, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbDateCustomParserFormatter} from '../../../../../common-blocks/ngb-date-custom-parser-formatter';
import {CKEditorModule} from '@ckeditor/ckeditor5-angular';
import {TranslateModule} from '@ngx-translate/core';
import { CardCheckComponent } from './card-check/card-check.component';
import { TerminalsComponent } from './terminals/terminals.component';
// import { CKEditorModule } from '@ckeditor/ckeditor5-build-classic';


@NgModule({
  declarations: [DashboardComponent, SettingsComponent, HelpComponent, CurrenciesComponent, PaymentMethodsComponent, UserComponent, UserSettingsComponent, ChatComponent, DocumentsLayoutsComponent, DocumentLayoutsEditorComponent, CardCheckComponent, TerminalsComponent],
    imports: [
        CommonModule,
        AdminPanelRoutingModule,
        CommonBlocksModule,
        NgScrollbarModule,
        ReactiveFormsModule,
        NgxMaskModule,
        NgbModule,
        CKEditorModule,
        TranslateModule,
        // CKEditorModule
    ],
  providers: [
    {provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter}
  ]
})
export class AdminPanelModule { }
