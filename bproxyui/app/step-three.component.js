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
var StepThreeComponent = (function () {
    function StepThreeComponent(bproxyService) {
        var _this = this;
        this.bproxyService = bproxyService;
        this.bproxyService.startNewSession(this.bproxyService.bproxy).then(function (proxy) {
            _this.bproxyService.bproxy = proxy;
            console.log(_this.bproxyService.bproxy);
        });
    }
    ;
    StepThreeComponent.prototype.newSession = function () {
        //this.bproxyService.bproxy.session++;
        var _this = this;
        this.bproxyService.startNewSession(this.bproxyService.bproxy).then(function (proxy) {
            _this.bproxyService.bproxy = proxy;
            console.log(_this.bproxyService.bproxy);
        });
    };
    StepThreeComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'stepThree',
            styleUrls: ['step-three.component.css'],
            templateUrl: 'step-three.component.html'
        }), 
        __metadata('design:paramtypes', [bproxy_service_1.BProxyService])
    ], StepThreeComponent);
    return StepThreeComponent;
}());
exports.StepThreeComponent = StepThreeComponent;
//# sourceMappingURL=step-three.component.js.map