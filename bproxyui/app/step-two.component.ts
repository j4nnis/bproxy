import { Component, Input, OnInit } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';

@Component({
  moduleId: module.id,
  selector: 'stepTwo',
  styleUrls: ['step-two.component.css'],
  templateUrl: 'step-two.component.html'
})

export class StepTwoComponent {
    constructor(private bproxyService: BProxyService) {};

    report : BProxyReport = MOCK_REPORT;

    

}
