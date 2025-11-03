import { Routes } from '@angular/router';
import { BeneficioListComponent } from './components/beneficio-list/beneficio-list.component';
import { TransferFormComponent } from './components/transfer-form/transfer-form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/beneficios', pathMatch: 'full' },
  { path: 'beneficios', component: BeneficioListComponent },
  { path: 'transferencia', component: TransferFormComponent },
  { path: '**', redirectTo: '/beneficios' }
];
