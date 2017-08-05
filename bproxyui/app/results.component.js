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
var bproxy_service_1 = require('./bproxy.service');
var bproxy_report_1 = require('./bproxy-report');
var request_response_component_1 = require('./request-response.component');
var ng_bootstrap_1 = require('@ng-bootstrap/ng-bootstrap');
var Rx_1 = require('rxjs/Rx');
var ResultsComponent = (function () {
    function ResultsComponent(bproxyService, modalService) {
        this.bproxyService = bproxyService;
        this.modalService = modalService;
        this.report = bproxy_report_1.MOCK_REPORT;
    }
    ;
    ResultsComponent.prototype.ngOnInit = function () {
        var _this = this;
        var timer = Rx_1.Observable.timer(0, 3000);
        timer.subscribe(function (t) {
            _this.getResults();
        });
    };
    ResultsComponent.prototype.getResults = function () {
        var _this = this;
        console.log("inited... getting report.");
        this.bproxyService.getReport(this.bproxyService.bproxy).then(function (report) {
            console.log(report);
            _this.report = report;
        });
    };
    ResultsComponent.prototype.getSortedReportRecords = function () {
        var sortedArray = this.report.values.sort(function (n1, n2) {
            console.log(n1);
            if (n1.row[1].value > n2.row[1].value) {
                return -1;
            }
            if (n1.row[1].value < n2.row[1].value) {
                return 1;
            }
            if (n1.row[0].value > n2.row[0].value) {
                return -1;
            }
            if (n1.row[0].value < n2.row[0].value) {
                return 1;
            }
            return 0;
        });
        return sortedArray;
    };
    ResultsComponent.prototype.showProof = function (reportRecord) {
        var _this = this;
        console.log("proofing " + reportRecord.analyzerIdentifier + " column" + reportRecord.column);
        this.bproxyService.getProof(this.bproxyService.bproxy, reportRecord.analyzerDomain, reportRecord.analyzerIdentifier, reportRecord.column).then(function (proof) {
            console.log(proof);
            _this.proof = proof;
        });
    };
    ResultsComponent.prototype.showDetails = function (request) {
        var modalRef = this.modalService.open(request_response_component_1.RequestResponseComponent, { windowClass: 'dark-modal', size: "lg" });
        modalRef.componentInstance.requestResponse = request;
    };
    ResultsComponent.prototype.newSession = function () {
        //this.bproxyService.bproxy.session++;
        var _this = this;
        this.bproxyService.startNewSession(this.bproxyService.bproxy).then(function (proxy) {
            _this.bproxyService.bproxy = proxy;
            console.log(_this.bproxyService.bproxy);
        });
    };
    ResultsComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'results',
            styleUrls: ['results.component.css'],
            templateUrl: 'results.component.html'
        }), 
        __metadata('design:paramtypes', [bproxy_service_1.BProxyService, ng_bootstrap_1.NgbModal])
    ], ResultsComponent);
    return ResultsComponent;
}());
exports.ResultsComponent = ResultsComponent;
//# sourceMappingURL=results.component.js.map