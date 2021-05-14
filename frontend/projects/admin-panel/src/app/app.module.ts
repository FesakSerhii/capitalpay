import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {NgxSpinnerModule} from 'ngx-spinner';
import { AdminPanelComponent } from './admin-panel/admin-panel.component';
import {CommonBlocksModule} from '../../../../common-blocks/common-blocks.module';
import { LoginComponent } from './login/login.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {PerfectScrollbarModule} from "ngx-perfect-scrollbar";
import {NgxMaskModule} from "ngx-mask";
import {httpInterceptorProviders} from "./http-interceptors";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    AdminPanelComponent,
    LoginComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    NgxSpinnerModule,
    HttpClientModule,
    CommonBlocksModule,
    ReactiveFormsModule,
    PerfectScrollbarModule,
    NgxMaskModule.forRoot(),
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
