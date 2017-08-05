"use strict";
var BProxy = (function () {
    function BProxy() {
        this.domain = "127.0.0.1";
        this.port = 23999;
        this.session = 1; // 1 | 2
        this.caCertDownloadLink = "/root_ca.cer";
    }
    BProxy.prototype.address = function () {
        return this.domain + ":" + this.port;
    };
    ;
    BProxy.prototype.copyInto = function (object) {
        this.domain = object.domain;
        this.port = object.port;
        this.appName = object.appName;
        this.session = object.session;
        this.proxyID = object.proxyID;
        this.state = object.state;
        this.caCertDownloadLink = object.caCertDownloadLink;
    };
    return BProxy;
}());
exports.BProxy = BProxy;
//# sourceMappingURL=bproxy.js.map