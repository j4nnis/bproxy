export class BProxyReportRecord {
  value: string;
  detail: string;

  analyzerIdentifier?: string;
  column?: number;
  analyzerDomain?: string; 
}

export class Row {
  row: BProxyReportRecord[];
}

export class BProxyReport {
  name: string;
  legend : BProxyReportRecord[];
  values : Row[];
}

export const MOCK_REPORT : BProxyReport = {
  name : "SomeApp",
  legend : [ {value : "Domain", detail: "the domain"}, {value : "TLS", detail: "the worst security standart used by this app"}, {value : "Cookies", detail: "session hijacking?"}],
  values : [
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]},
            {"row":[ { value: "weirdapp.de", detail: "eg. all paths observed"}, { value:"SSL 3.0", detail: "not great"},{ value:"✓", detail: "not possible"} ]}]
  }

/*
{
  name: 0815HealthApp,

  legend: [ {value: "Domain", detail: "the domain"} ]
  values: [ {value: "weirdapp.de", detail: "eg. all paths observed"} ] //content should be allowed to be html
}
*/