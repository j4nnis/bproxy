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
var platform_browser_1 = require('@angular/platform-browser');
var forms_1 = require('@angular/forms');
var http_1 = require('@angular/http');
var ng_bootstrap_1 = require('@ng-bootstrap/ng-bootstrap');
var app_routing_module_1 = require('./app-routing.module');
require('./rxjs-extensions');
var app_component_1 = require('./app.component');
var proxy_info_component_1 = require('./proxy-info.component');
var results_component_1 = require('./results.component');
var request_response_component_1 = require('./request-response.component');
var bproxy_service_1 = require('./bproxy.service');
var step_container_component_1 = require('./step-container.component');
var step_one_component_1 = require('./step-one.component');
var step_two_component_1 = require('./step-two.component');
var step_three_component_1 = require('./step-three.component');
var AppModule = (function () {
    function AppModule() {
    }
    AppModule = __decorate([
        core_1.NgModule({
            imports: [
                platform_browser_1.BrowserModule,
                forms_1.FormsModule,
                http_1.HttpModule,
                app_routing_module_1.AppRoutingModule,
                ng_bootstrap_1.NgbModule.forRoot()
            ],
            declarations: [
                app_component_1.AppComponent,
                results_component_1.ResultsComponent,
                proxy_info_component_1.ProxyInfoComponent,
                step_container_component_1.StepContainerComponent,
                step_one_component_1.StepOneComponent,
                step_two_component_1.StepTwoComponent,
                step_three_component_1.StepThreeComponent,
                request_response_component_1.RequestResponseComponent
            ],
            entryComponents: [request_response_component_1.RequestResponseComponent],
            providers: [bproxy_service_1.BProxyService],
            bootstrap: [app_component_1.AppComponent]
        }), 
        __metadata('design:paramtypes', [])
    ], AppModule);
    return AppModule;
}());
exports.AppModule = AppModule;
//# sourceMappingURL=app.module.js.map