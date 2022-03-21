import {Injectable} from "@angular/core";
import {environment} from "../../environments/environment";
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly apiBase = environment;

  constructor(private http: HttpClient) {
  }
  public get(api,url: string, params = null): Observable<any> {
    const httpParams = this.prepareParams(params);
    return this.http.get(this.apiBase[api] + url + (httpParams ? '?' + httpParams.toString() : ''));
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
  log(api,url: string, data: any = {}, waitBlob: boolean = false){
    // const headers = new HttpHeaders({ 'Content-Type': 'application/json'});
    const body = JSON.stringify(data);
    return this.http.post(
      this.apiBase[api] + url, body, {observe: 'response'});
  }
  public post(api,url: string, data: any = {}, waitBlob: boolean = false): Observable<any> {
    const headers = { 'content-type': 'application/json'};
    const body = JSON.stringify(data);
    return this.http.post(this.apiBase[api] + url, body, {headers});
  }
  public postJwt(api,url: string, token, data: any = {},): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json','Authorization':token});
    const body = JSON.stringify(data);
    return this.http.post(this.apiBase[api] + url, body, {headers});
  }

  public postFormData(api,url, token, data: FormData): Observable<any> {
    const headers = new HttpHeaders({ 'Mime-Type': 'multipart/form-data','Authorization':token });
    return this.http.post(this.apiBase[api] + url, data, { headers });
  }

  public put(api,url: string, data: any = {}): Observable<any> {
    return this.http.put(this.apiBase[api] + url, data);
  }


  // tslint:disable-next-line:typedef
  public delete(api,url: string, param2: any = null) {
    if (param2 !== null) {
      const options = {
        headers: new HttpHeaders({'Content-Type': 'application/json'}),
        body: param2
      };
      return this.http.delete(this.apiBase[api] + url, options);
    }
    return this.http.delete(this.apiBase[api] + url);
  }
}
