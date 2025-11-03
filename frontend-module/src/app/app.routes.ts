import { Routes } from '@angular/router';
import { BeneficioListComponent } from './components/beneficio-list/beneficio-list.component';
import { BeneficioFormComponent } from './components/beneficio-form/beneficio-form.component';
import { TransferFormComponent } from './components/transfer-form/transfer-form.component';

export const routes: Routes = [
  { path: '', redirectTo: '/beneficios', pathMatch: 'full' },
  { path: 'beneficios', component: BeneficioListComponent },
  { path: 'beneficios/create', component: BeneficioFormComponent },
  { path: 'beneficios/edit/:id', component: BeneficioFormComponent },
  { path: 'beneficios/transfer/:id', component: TransferFormComponent },
  { path: 'transferencia', component: TransferFormComponent },
  { path: '**', redirectTo: '/beneficios' }
];
