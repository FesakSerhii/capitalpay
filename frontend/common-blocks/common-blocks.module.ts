import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { HexagonLinkComponent } from './hexagon-link/hexagon-link.component';
import {RouterModule} from '@angular/router';
import {ReactiveFormsModule} from '@angular/forms';
import {SideMenuComponent} from './side-menu/side-menu.component';
import { PanelHeaderComponent } from './panel-header/panel-header.component';
import {CustomSelectComponent} from './custom-select/custom-select.component';
import {PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {TransactionsLogComponent} from './transactions-log/transactions-log.component';




@NgModule({
  declarations: [HeaderComponent, FooterComponent, HexagonLinkComponent,SideMenuComponent,
    PanelHeaderComponent, CustomSelectComponent, TransactionsLogComponent],
  exports: [HeaderComponent, FooterComponent, HexagonLinkComponent, SideMenuComponent,
    PanelHeaderComponent,CustomSelectComponent, TransactionsLogComponent],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    PerfectScrollbarModule
  ]
})
export class CommonBlocksModule { }
