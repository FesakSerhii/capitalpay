import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainPageComponent } from './main-page/main-page.component';
import { NgbDateParserFormatter, NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { IvyCarouselModule } from 'angular-responsive-carousel';
import { RegisterComponent } from './register/register.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MerchantPanelComponent } from './merchant-panel/merchant-panel.component';
import { CommonBlocksModule } from '../../common-blocks/common-blocks.module';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { NgScrollbarModule } from 'ngx-scrollbar';
import { BlankPageComponent } from './blank-page/blank-page.component';
import { IConfig, NgxMaskModule } from 'ngx-mask';
import { NgbDateCustomParserFormatter } from '../../common-blocks/ngb-date-custom-parser-formatter';
import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { FeedbackComponent } from './feedback/feedback.component';
import { JwtModule } from "@auth0/angular-jwt";
import { httpInterceptorProviders } from './http-interceptors';
import {SlickCarouselModule} from 'ngx-slick-carousel';

export function tokenGetter() {
  return sessionStorage.getItem("token");
}

const maskConfigFunction: () => Partial<IConfig> = () => {
  return {
    validation: true,
  };
};
@NgModule({
  declarations: [
    AppComponent,
    MainPageComponent,
    RegisterComponent,
    MerchantPanelComponent,
    TermsOfUseComponent,
    BlankPageComponent,
    FeedbackComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    SlickCarouselModule,
    HttpClientModule,
    CommonBlocksModule,
    IvyCarouselModule,
    NgScrollbarModule,
    ReactiveFormsModule,
    CKEditorModule,
    NgxMaskModule.forRoot(maskConfigFunction),
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
      },
    }),
  ],
  providers: [
    {
      provide: NgbDateParserFormatter,
      useClass: NgbDateCustomParserFormatter
    },
    httpInterceptorProviders
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
