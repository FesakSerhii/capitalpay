import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {ComonBlocksModule} from "./comon-blocks/comon-blocks.module";
import { MainPageComponent } from './main-page/main-page.component';
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
// import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        ComonBlocksModule,
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
