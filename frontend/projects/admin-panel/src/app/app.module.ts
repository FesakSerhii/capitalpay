import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {NgxSpinnerModule} from 'ngx-spinner';
import { AdminPanelComponent } from './admin-panel/admin-panel.component';
import {CommonBlocksModule} from '../../../../common-blocks/common-blocks.module';
import { LoginComponent } from './login/login.component';
import { ReactiveFormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {IConfig, NgxMaskModule} from 'ngx-mask';
import {httpInterceptorProviders} from "./http-interceptors";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {NgScrollbarModule} from 'ngx-scrollbar';
import {NgbDateParserFormatter, NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgbDateCustomParserFormatter} from '../../../../common-blocks/ngb-date-custom-parser-formatter';
import { JwtModule } from "@auth0/angular-jwt";

export function tokenGetter() {
  return sessionStorage.getItem("token");
}

// import { CKEditorModule } from '@ckeditor/ckeditor5-angular';
// import {TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
// import {LocalizeParser, LocalizeRouterModule, LocalizeRouterSettings, ManualParserLoader} from 'localize-router';
// import {TranslateHttpLoader} from '@ngx-translate/http-loader';
// import { routes } from './app-routing.module';

// export function ManualLoaderFactory(translate, location, settings) {
//   return new ManualParserLoader(translate, location, settings, ['en', 'kz'], 'en');
// }
// export function HttpLoaderFactory(http: HttpClient) {
//   return new TranslateHttpLoader(http, './assets/i18n/', '.json');
// }
// export function chosenLocale(translate: TranslateService) {
//   return translate.currentLang;
// }

const maskConfigFunction: () => Partial<IConfig> = () => {
  return {
    validation: true,
  };
};

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
    NgScrollbarModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
      },
    }),
    // CKEditorModule,
    // LocalizeRouterModule.forRoot(routes, {
    //   parser: {
    //     provide: LocalizeParser,
    //     useFactory: (ManualLoaderFactory),
    //     deps: [TranslateService, Location, LocalizeRouterSettings]
    //   }
    // }),
    // TranslateModule.forRoot({
    //   loader: {
    //     provide: TranslateLoader,
    //     useFactory: HttpLoaderFactory,
    //     deps: [HttpClient]
    //   }
    // }),
    NgbModule,
    NgxMaskModule.forRoot(maskConfigFunction),
  ],
  providers: [httpInterceptorProviders,{provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter}],
  bootstrap: [AppComponent]
})
export class AppModule { }
