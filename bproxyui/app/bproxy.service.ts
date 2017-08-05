import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import 'rxjs/add/operator/toPromise';

import { BProxy } from './bproxy';
import { RequestResponseContainer, Headerfield } from './request-response-container';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';

@Injectable()
export class BProxyService {
  bproxy : BProxy = new BProxy();

  private proxyUrl = 'api/proxy';  // URL to web api

  constructor(private http: Http) { }

  private headers = new Headers({'Content-Type': 'application/json'});

  create(): Promise<BProxy> {
    return this.http
      .post(this.proxyUrl/*, JSON.stringify({name: name})*/, {headers: this.headers})
      .toPromise()
      .then(res => {
        this.bproxy = new BProxy();
        this.bproxy.copyInto(res.json());
        return this.bproxy;
      })
      .catch(this.handleError);
  }

  update(proxy: BProxy): Promise<BProxy> {
    const url = `${this.proxyUrl}/${proxy.proxyID}`;
    return this.http
      .patch(url, JSON.stringify(proxy), {headers: this.headers})
      .toPromise()
      .then(() => proxy)
      .catch(this.handleError);
  }

  startNewSession(proxy: BProxy): Promise<BProxy> {
    const url = `${this.proxyUrl}/${proxy.proxyID}/newSession`;
    return this.http
      .post(url, {headers: this.headers})
      .toPromise()
      .then(res => {
        this.bproxy = new BProxy();
        this.bproxy.copyInto(res.json());
        return this.bproxy;
      })
      .catch(this.handleError);
  }

  stop(proxy: BProxy): Promise<BProxy> {
    const url = `${this.proxyUrl}/${proxy.proxyID}/stop`;
    return this.http
      .post(url, {headers: this.headers})
      .toPromise()
      .then(() => proxy)
      .catch(this.handleError);
  }

  getProof(proxy: BProxy, domain: String, proxyAnalyzerIdentifier: string, column: number): Promise<RequestResponseContainer[]> {
    //return Promise.resolve(MOCK_REPORT)
    const url = `${this.proxyUrl}/${proxy.proxyID}/report/proof/${domain}/${proxyAnalyzerIdentifier}/${column}`;

    return this.http.get(url).toPromise()
          .then(response => {console.log(response.json()); return response.json();})
          .catch(this.handleError);

  }

  getReport(proxy: BProxy): Promise<BProxyReport> {
    //return Promise.resolve(MOCK_REPORT)
    const url = `${this.proxyUrl}/${proxy.proxyID}/report`;

    return this.http.get(url).toPromise()
          .then(response => {console.log(response.json()); return response.json();})
          .catch(this.handleError);

  }

  delete(id: number): Promise<void> {
    const url = `${this.proxyUrl}/${id}`;
    return this.http.delete(url, {headers: this.headers})
    .toPromise()
    .then(res => res.json())
    .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only (yeah right)
    return Promise.reject(error.message || error);
  }
}
