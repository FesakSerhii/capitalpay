import {Injectable} from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse
} from '@angular/common/http';

import {Observable} from 'rxjs';
import {finalize, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {AuthService} from '../service/auth.service';
import {NgxSpinnerService} from 'ngx-spinner';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  counter = 0;

  // private appErrorModalsService: AppErrorModalsService
  constructor(
    private router: Router,
    private auth: AuthService,
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
      () => {
        // this.spinnerService.hide();
      },
      (err: any) => {
        if (err instanceof HttpErrorResponse && err.status === 404) {
          // this.router.navigate(["/error/404"]);
          // this.spinnerService.hide();
        } else if (err instanceof HttpErrorResponse && err.status === 403) {
          this.auth.tokenStateChange.next(false);
          // this.spinnerService.hide();
        } else if ((err instanceof HttpErrorResponse && err.status === 500) || (err instanceof HttpErrorResponse && err.status === 503)) {
          // this.spinnerService.hide();
          // this.appErrorModalsService.modalError("server error");
        } else {
          // this.spinnerService.hide();
          // this.appErrorModalsService.modalError("error");
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
