import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';
import { HttpModule }    from '@angular/http';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import './rxjs-extensions';

import { AppComponent }         from './app.component';
import { ProxyInfoComponent }   from './proxy-info.component';
import { ResultsComponent }  from './results.component';
import { RequestResponseComponent }  from './request-response.component';
import { BProxyService }  from './bproxy.service';

import { StepContainerComponent }  from './step-container.component';
import { StepOneComponent }  from './step-one.component';
import { StepTwoComponent }  from './step-two.component';
import { StepThreeComponent }  from './step-three.component';


@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    AppRoutingModule,
    NgbModule.forRoot()
  ],
    declarations: [
    AppComponent,
    ResultsComponent,
    ProxyInfoComponent,
    StepContainerComponent,
    StepOneComponent,
    StepTwoComponent,
    StepThreeComponent,
    RequestResponseComponent
  ],
  entryComponents: [ RequestResponseComponent ],
  providers: [ BProxyService ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
