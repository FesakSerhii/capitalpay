import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {ComonBlocksModule} from './comon-blocks/comon-blocks.module';
import { MainPageComponent } from './main-page/main-page.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {IvyCarouselModule} from 'angular-responsive-carousel';
import { RegisterComponent } from './register/register.component';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
// import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    RegisterComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        NgbModule,
        HttpClientModule,
        ComonBlocksModule,
        IvyCarouselModule,
        ReactiveFormsModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
