import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MerchantPanelRoutingModule} from './merchant-panel-routing.module';
import {PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {CommonBlocksModule} from '../../../common-blocks/common-blocks.module';



@NgModule({
  declarations: [],
    imports: [
        CommonModule,
        MerchantPanelRoutingModule,
        CommonBlocksModule,
        PerfectScrollbarModule
    ]
})
export class MerchantPanelModule { }
