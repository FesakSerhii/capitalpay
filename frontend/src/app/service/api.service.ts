import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly apiBase = environment.api;

  constructor(private http: HttpClient) {
  }
  public get(url: string, params = null): Observable<any> {
    const httpParams = this.prepareParams(params);
    return this.http.get(this.apiBase + url + (httpParams ? '?' + httpParams.toString() : ''));
  }

  // tslint:disable-next-line:typedef
  private prepareParams(params: any) {
    // @ts-ignore
    let httpParams: HttpParams = null;
    if (params) {
      httpParams = new HttpParams();
      for (const i in params) {
        if (params[i] !== null) {
          httpParams = httpParams.append(i, params[i]);
        }
      }
    }
    return httpParams;
  }

  public post(url: string, data: any = {}, waitBlob: boolean = false): Observable<any> {
    const headers = { 'content-type': 'application/json'};
    const body = JSON.stringify(data);
    return this.http.post(this.apiBase + url, body, {headers});
  }


  public put(url: string, data: any = {}): Observable<any> {
    console.log(url);
    return this.http.put(this.apiBase + url, data);
  }


  // tslint:disable-next-line:typedef
  public delete(url: string, param2: any = null) {
    if (param2 !== null) {
      const options = {
        headers: new HttpHeaders({'Content-Type': 'application/json'}),
        body: param2
      };
      return this.http.delete(this.apiBase + url, options);
    }
    return this.http.delete(this.apiBase + url);
  }
}
