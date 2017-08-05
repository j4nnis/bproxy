import { Component, Input } from '@angular/core';
import { BProxy } from './bproxy';
import { BProxyService } from './bproxy.service'

@Component({
  moduleId: module.id,
  selector: 'proxy-info',
  styleUrls: ['proxy-info.component.css'],
  templateUrl: 'proxy-info.component.html',
})

export class ProxyInfoComponent {
  constructor(private bproxyService: BProxyService) {};

 
}
