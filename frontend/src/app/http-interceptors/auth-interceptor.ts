import {Injectable, TemplateRef} from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse
} from '@angular/common/http';

import {Observable, throwError} from 'rxjs';
import {finalize, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {AuthService} from '../service/auth.service';
import {NgxSpinnerService} from 'ngx-spinner';
import {MassageServiceService} from "../service/massage-service.service";


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  counter = 0;

  // private appErrorModalsService: AppErrorModalsService
  constructor(
    private router: Router,
    private auth: AuthService,
    private massageServiceService: MassageServiceService,
    private spinnerService: NgxSpinnerService
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authToken = 'Bearer ' + sessionStorage.getItem('token');
    const headers = req.headers;
    const authReq = req.clone(authToken ? {setHeaders: {Authorization: authToken}} : {});
    if (!req.url.toString().includes('numbersSession')) {
      this.spinnerService.show();
    }
    this.counter++;
    return next.handle(authReq).pipe(tap(
      (req:any) => {
        if(!req?.body?.result&&req?.body&&req.type!==0){
          this.massageServiceService.announceMassage('The problem has arisen, our specialists are already solving it')
        }
      },
      (err: any) => {
        if (err instanceof HttpErrorResponse && err.status === 404) {
            this.massageServiceService.announceMassage(err.statusText)
          } else if (err instanceof HttpErrorResponse && err.status === 403) {
            this.auth.tokenStateChange.next(false);
          } else if (err instanceof HttpErrorResponse && err.status === 400) {
            this.massageServiceService.announceMassage('req400Error')
          } else if (err instanceof HttpErrorResponse && err.status === 0) {
            this.massageServiceService.announceMassage('noInternetError')
          } else if ((err instanceof HttpErrorResponse && err.status === 500) || (err instanceof HttpErrorResponse && err.status === 503)) {
            this.massageServiceService.announceMassage('req500Error')
          } else {
            this.massageServiceService.announceMassage(err.statusText)
          }
      }
    ), finalize(() => {
      this.counter--;
      console.log(this.counter);
      if (!this.counter)
        this.spinnerService.hide();
    }));
  }
}
