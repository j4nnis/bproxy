import { Component, Input, OnInit } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';

@Component({
  moduleId: module.id,
  selector: 'stepOne',
  styleUrls: ['step-one.component.css'],
  templateUrl: 'step-one.component.html'
})
export class StepOneComponent {
  constructor(private bproxyService: BProxyService) {
    this.bproxyService.create().then( proxy => {
        console.log(proxy);
    });
  };
}