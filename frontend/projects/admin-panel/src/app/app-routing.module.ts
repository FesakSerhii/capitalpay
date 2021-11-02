import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  {
    path: '',
    component: LoginComponent
  },
  {
    path: 'admin-panel',
    canLoad: [AuthGuard],
    canActivate: [AuthGuard],
    loadChildren: () => import('./admin-panel/admin-panel.module').then(mod => mod.AdminPanelModule)
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
