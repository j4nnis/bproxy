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
var request_response_container_1 = require('./request-response-container');
var ng_bootstrap_1 = require('@ng-bootstrap/ng-bootstrap');
var RequestResponseComponent = (function () {
    function RequestResponseComponent(activeModal) {
        this.activeModal = activeModal;
    }
    __decorate([
        core_1.Input(), 
        __metadata('design:type', request_response_container_1.RequestResponseContainer)
    ], RequestResponseComponent.prototype, "requestResponse", void 0);
    RequestResponseComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: 'RequestResponseComponent',
            styleUrls: ['request-response.component.css'],
            templateUrl: 'request-response.component.html'
        }), 
        __metadata('design:paramtypes', [ng_bootstrap_1.NgbActiveModal])
    ], RequestResponseComponent);
    return RequestResponseComponent;
}());
exports.RequestResponseComponent = RequestResponseComponent;
//# sourceMappingURL=request-response.component.js.map