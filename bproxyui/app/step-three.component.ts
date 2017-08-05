import { Component } from '@angular/core';

import { BProxyService } from './bproxy.service';
import { BProxy } from './bproxy';
import { BProxyReport, BProxyReportRecord, MOCK_REPORT } from './bproxy-report';

@Component({
  moduleId: module.id,
  selector: 'stepThree',
  styleUrls: ['step-three.component.css'],
  templateUrl: 'step-three.component.html'
})
export class StepThreeComponent {
      constructor(private bproxyService: BProxyService) {
        this.bproxyService.startNewSession(this.bproxyService.bproxy).then( proxy => {
          this.bproxyService.bproxy = proxy;
          console.log(this.bproxyService.bproxy)
        });
      };

      newSession(): void {
        //this.bproxyService.bproxy.session++;

        this.bproxyService.startNewSession(this.bproxyService.bproxy).then( proxy => {
          this.bproxyService.bproxy = proxy;
          console.log(this.bproxyService.bproxy)
        });
      }
}
