import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainPageComponent} from './main-page/main-page.component';
import {RegisterComponent} from './register/register.component';
import {TermsOfUseComponent} from "./terms-of-use/terms-of-use.component";

export const routes: Routes = [
  {
    path: '',
    component: MainPageComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'confirm',
    component: RegisterComponent
  },
  {
    path: 'terms-of-use',
    component: TermsOfUseComponent
  },
  {
    path: 'merchant',
    loadChildren: () => import('./merchant-panel/merchant-panel.module').then(mod => mod.MerchantPanelModule)
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
