"use strict";
var BProxyReportRecord = (function () {
    function BProxyReportRecord() {
    }
    return BProxyReportRecord;
}());
exports.BProxyReportRecord = BProxyReportRecord;
var Row = (function () {
    function Row() {
    }
    return Row;
}());
exports.Row = Row;
var BProxyReport = (function () {
    function BProxyReport() {
    }
    return BProxyReport;
}());
exports.BProxyReport = BProxyReport;
exports.MOCK_REPORT = {
    name: "SomeApp",
    legend: [{ value: "Domain", detail: "the domain" }, { value: "TLS", detail: "the worst security standart used by this app" }, { value: "Cookies", detail: "session hijacking?" }],
    values: [
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] },
        { "row": [{ value: "weirdapp.de", detail: "eg. all paths observed" }, { value: "SSL 3.0", detail: "not great" }, { value: "✓", detail: "not possible" }] }]
};
/*
{
  name: 0815HealthApp,

  legend: [ {value: "Domain", detail: "the domain"} ]
  values: [ {value: "weirdapp.de", detail: "eg. all paths observed"} ] //content should be allowed to be html
}
*/ 
//# sourceMappingURL=bproxy-report.js.map