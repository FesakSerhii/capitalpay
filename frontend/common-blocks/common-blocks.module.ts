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
import {TransactionsLogComponent} from './transactions-log/transactions-log.component';
import { MassageModalComponent } from './massage-modal/massage-modal.component';
import {NgScrollbarModule} from 'ngx-scrollbar';
import { TransactionsRegistryComponent } from './transactions-log/transactions-registry/transactions-registry.component';
import {NgxPaginationModule} from 'ngx-pagination';
import {NgbDateParserFormatter, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbDateCustomParserFormatter} from './ngb-date-custom-parser-formatter';
import {ConfirmActionModalComponent} from './confirm-action-modal/confirm-action-modal.component';
import { PaymentCardModalComponent } from './payment-card-modal/payment-card-modal.component';
import {NgxMaskModule} from 'ngx-mask';
import { LoginModalComponent } from './header/login-modal/login-modal.component';
import { RecoverPasswordModalComponent } from './header/recover-password-modal/recover-password-modal.component';




@NgModule({
  declarations: [HeaderComponent, FooterComponent, HexagonLinkComponent,SideMenuComponent,
    PanelHeaderComponent, CustomSelectComponent, TransactionsLogComponent, MassageModalComponent, TransactionsRegistryComponent,ConfirmActionModalComponent, PaymentCardModalComponent, LoginModalComponent, RecoverPasswordModalComponent],
    exports: [HeaderComponent, FooterComponent, HexagonLinkComponent, SideMenuComponent,
        PanelHeaderComponent, CustomSelectComponent, TransactionsLogComponent, MassageModalComponent, TransactionsRegistryComponent, ConfirmActionModalComponent, PaymentCardModalComponent],
    imports: [
        CommonModule,
        RouterModule,
        ReactiveFormsModule,
        NgScrollbarModule,
        NgxPaginationModule,
        // TranslateModule.forChild(),
        // LocalizeRouterModule.forChild([]),
        NgbModule,
        NgxMaskModule
    ],
  providers: [
    {provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter}
  ]
})
export class CommonBlocksModule { }
