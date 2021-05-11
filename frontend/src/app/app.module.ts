import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageComponent } from './main-page/main-page.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {IvyCarouselModule} from 'angular-responsive-carousel';
import { RegisterComponent } from './register/register.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import { MerchantPanelComponent } from './merchant-panel/merchant-panel.component';
import {PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {CommonBlocksModule} from '../../common-blocks/common-blocks.module';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
// import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    RegisterComponent,
    MerchantPanelComponent,
    TermsOfUseComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        HttpClientModule,
        CommonBlocksModule,
        IvyCarouselModule,
        PerfectScrollbarModule,
        ReactiveFormsModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
