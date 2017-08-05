export class BProxy {
  domain: string = "127.0.0.1";
  port: number = 23999;
  appName: string;
  session: number = 1; // 1 | 2
  proxyID: string;
  state: string; // stopped | runnning
  caCertDownloadLink : string = "/root_ca.cer";

  address() {
    return this.domain + ":" + this.port
  };

  copyInto(object: BProxy) {
   this.domain = object.domain;
   this.port = object.port;
   this.appName = object.appName;
   this.session = object.session;
   this.proxyID = object.proxyID;
   this.state = object.state;
   this.caCertDownloadLink = object.caCertDownloadLink;
  }
}
