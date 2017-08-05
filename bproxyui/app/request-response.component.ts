import { Component, Input, OnInit } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';
import { RequestResponseContainer } from './request-response-container';

import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  moduleId: module.id,
  selector: 'RequestResponseComponent',
  styleUrls: ['request-response.component.css'],
  templateUrl: 'request-response.component.html'
})
export class RequestResponseComponent {
    @Input() requestResponse : RequestResponseContainer;

    constructor(public activeModal: NgbActiveModal) {}
}