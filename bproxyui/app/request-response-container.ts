export class RequestResponseContainer {
  hostname: string;
  requestURL: string;

  httpMethod: string;
  responseStatusCode?: number;
  tlsVersion?: string;

  requestBody?: string;
  responseBody?: string;

  requestHeaders?: [Headerfield];
  responseHeaders?: [Headerfield];
}

export class Headerfield {
  key: string;
  value: string;
}