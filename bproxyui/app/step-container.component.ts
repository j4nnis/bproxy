import { Component, Input, OnInit } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';

import { StepOneComponent } from './step-one.component';
import { StepTwoComponent } from './step-two.component';
import { StepThreeComponent } from './step-three.component';


@Component({
  moduleId: module.id,
  selector: 'stepContainer',
  styleUrls: ['step-container.component.css'],
  templateUrl: 'step-container.component.html'
})
export class StepContainerComponent {
  step = 1


  nextStep(): void {
    this.step++;
  }

  restart(): void {
    this.step = 1;
  }
}