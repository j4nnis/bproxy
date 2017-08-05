"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var core_1 = require('@angular/core');
var http_1 = require('@angular/http');
require('rxjs/add/operator/toPromise');
var bproxy_1 = require('./bproxy');
var BProxyService = (function () {
    function BProxyService(http) {
        this.http = http;
        this.bproxy = new bproxy_1.BProxy();
        this.proxyUrl = 'api/proxy'; // URL to web api
        this.headers = new http_1.Headers({ 'Content-Type': 'application/json' });
    }
    BProxyService.prototype.create = function () {
        var _this = this;
        return this.http
            .post(this.proxyUrl /*, JSON.stringify({name: name})*/, { headers: this.headers })
            .toPromise()
            .then(function (res) {
            _this.bproxy = new bproxy_1.BProxy();
            _this.bproxy.copyInto(res.json());
            return _this.bproxy;
        })
            .catch(this.handleError);
    };
    BProxyService.prototype.update = function (proxy) {
        var url = this.proxyUrl + "/" + proxy.proxyID;
        return this.http
            .patch(url, JSON.stringify(proxy), { headers: this.headers })
            .toPromise()
            .then(function () { return proxy; })
            .catch(this.handleError);
    };
    BProxyService.prototype.startNewSession = function (proxy) {
        var _this = this;
        var url = this.proxyUrl + "/" + proxy.proxyID + "/newSession";
        return this.http
            .post(url, { headers: this.headers })
            .toPromise()
            .then(function (res) {
            _this.bproxy = new bproxy_1.BProxy();
            _this.bproxy.copyInto(res.json());
            return _this.bproxy;
        })
            .catch(this.handleError);
    };
    BProxyService.prototype.stop = function (proxy) {
        var url = this.proxyUrl + "/" + proxy.proxyID + "/stop";
        return this.http
            .post(url, { headers: this.headers })
            .toPromise()
            .then(function () { return proxy; })
            .catch(this.handleError);
    };
    BProxyService.prototype.getProof = function (proxy, domain, proxyAnalyzerIdentifier, column) {
        //return Promise.resolve(MOCK_REPORT)
        var url = this.proxyUrl + "/" + proxy.proxyID + "/report/proof/" + domain + "/" + proxyAnalyzerIdentifier + "/" + column;
        return this.http.get(url).toPromise()
            .then(function (response) { console.log(response.json()); return response.json(); })
            .catch(this.handleError);
    };
    BProxyService.prototype.getReport = function (proxy) {
        //return Promise.resolve(MOCK_REPORT)
        var url = this.proxyUrl + "/" + proxy.proxyID + "/report";
        return this.http.get(url).toPromise()
            .then(function (response) { console.log(response.json()); return response.json(); })
            .catch(this.handleError);
    };
    BProxyService.prototype.delete = function (id) {
        var url = this.proxyUrl + "/" + id;
        return this.http.delete(url, { headers: this.headers })
            .toPromise()
            .then(function (res) { return res.json(); })
            .catch(this.handleError);
    };
    BProxyService.prototype.handleError = function (error) {
        console.error('An error occurred', error); // for demo purposes only (yeah right)
        return Promise.reject(error.message || error);
    };
    BProxyService = __decorate([
        core_1.Injectable(), 
        __metadata('design:paramtypes', [http_1.Http])
    ], BProxyService);
    return BProxyService;
}());
exports.BProxyService = BProxyService;
//# sourceMappingURL=bproxy.service.js.map