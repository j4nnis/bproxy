import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { AppModule } from './app.module';

const platform = platformBrowserDynamic();

platform.bootstrapModule(AppModule);
