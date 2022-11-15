import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainPageComponent } from './main-page/main-page.component';
import { RegisterComponent } from './register/register.component';
import { TermsOfUseComponent } from './terms-of-use/terms-of-use.component';
import { BlankPageComponent } from './blank-page/blank-page.component';
import { FeedbackComponent } from './feedback/feedback.component';
import { AuthGuard } from './service/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'feedback',
    component: FeedbackComponent
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
    canLoad: [AuthGuard],
    canActivate: [AuthGuard],
    loadChildren: () => import('./merchant-panel/merchant-panel.module').then(mod => mod.MerchantPanelModule)
  },
  {
    path: 'blank',
    component: BlankPageComponent
  },
  {
    path: '',
    component: MainPageComponent
  },
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
