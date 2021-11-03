import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MerchantPanelRoutingModule} from './merchant-panel-routing.module';
import {CommonBlocksModule} from '../../../common-blocks/common-blocks.module';
import { SettingsComponent } from './settings/settings.component';
import { ToolsComponent } from './tools/tools.component';
import { SupportComponent } from './support/support.component';
import { ApiComponent } from './api/api.component';
import {ReactiveFormsModule} from '@angular/forms';
import {NgScrollbarModule} from 'ngx-scrollbar';
import {NgbDateParserFormatter, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbDateCustomParserFormatter} from '../../../common-blocks/ngb-date-custom-parser-formatter';
import { ChatComponent } from './chat/chat.component';



@NgModule({
  declarations: [SettingsComponent, ToolsComponent, SupportComponent, ApiComponent, ChatComponent],
    imports: [
        CommonModule,
        MerchantPanelRoutingModule,
        CommonBlocksModule,
        NgScrollbarModule,
        ReactiveFormsModule,
        NgbModule
    ],
  providers: [{provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter}],
})
export class MerchantPanelModule { }
