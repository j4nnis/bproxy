import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProxyInfoComponent }      from './proxy-info.component';


const routes: Routes = [
  { path: '', redirectTo: '/', pathMatch: 'full' }
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})

export class AppRoutingModule {}
