import { Component, Input, OnInit } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, Row, MOCK_REPORT } from './bproxy-report';
import { RequestResponseContainer } from './request-response-container';
import { RequestResponseComponent } from './request-response.component'
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import {Observable} from 'rxjs/Rx';


@Component({
  moduleId: module.id,
  selector: 'results',
  styleUrls: ['results.component.css'],
  templateUrl: 'results.component.html'
})

export class ResultsComponent implements OnInit {
  constructor(private bproxyService: BProxyService, private modalService: NgbModal) {

  };

  report: BProxyReport = MOCK_REPORT;

  ngOnInit(): void {
    let timer = Observable.timer(0, 3000);
    timer.subscribe(t => {
      this.getResults();
    });
  }

  getResults(): void {
    console.log("inited... getting report.")

    this.bproxyService.getReport(this.bproxyService.bproxy).then(report => {
      console.log(report);

      this.report = report;
    });
  }

  getSortedReportRecords(): Row[] {
    var sortedArray: Row[] = this.report.values.sort((n1, n2) => {

      console.log(n1)

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

      return 0

    });
    return sortedArray;
  }


  proof: RequestResponseContainer[];

  showProof(reportRecord: BProxyReportRecord): void {
    console.log("proofing " + reportRecord.analyzerIdentifier + " column" + reportRecord.column);


    this.bproxyService.getProof(this.bproxyService.bproxy, reportRecord.analyzerDomain, reportRecord.analyzerIdentifier, reportRecord.column).then(proof => {
      console.log(proof);
      this.proof = proof;
    });

  }

  showDetails(request: RequestResponseContainer): void {
    const modalRef = this.modalService.open(RequestResponseComponent, { windowClass: 'dark-modal', size: "lg" });
    modalRef.componentInstance.requestResponse = request;
  }

  newSession(): void {
    //this.bproxyService.bproxy.session++;

    this.bproxyService.startNewSession(this.bproxyService.bproxy).then(proxy => {
      this.bproxyService.bproxy = proxy;
      console.log(this.bproxyService.bproxy)
    });
  }

}
