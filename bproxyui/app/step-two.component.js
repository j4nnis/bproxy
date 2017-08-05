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
var StepTwoComponent = (function () {
    function StepTwoComponent(bproxyService) {
        this.bproxyService = bproxyService;
        this.report = bproxy_report_1.MOCK_REPORT;
    }
    ;
    StepTwoComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'stepTwo',
            styleUrls: ['step-two.component.css'],
            templateUrl: 'step-two.component.html'
        }), 
        __metadata('design:paramtypes', [bproxy_service_1.BProxyService])
    ], StepTwoComponent);
    return StepTwoComponent;
}());
exports.StepTwoComponent = StepTwoComponent;
//# sourceMappingURL=step-two.component.js.map