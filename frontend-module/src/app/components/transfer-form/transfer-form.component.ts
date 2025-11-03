import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio';

@Component({
  selector: 'app-transfer-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './transfer-form.component.html',
  styleUrl: './transfer-form.component.scss'
})
export class TransferFormComponent implements OnInit {
  transferForm: FormGroup;
  beneficios: Beneficio[] = [];
  loading: boolean = false;
  preSelectedFromId?: number;

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.transferForm = this.fb.group({
      fromId: ['', [Validators.required]],
      toId: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(0.01)]]
    });
  }

  ngOnInit(): void {
    // Verificar se veio um ID pela rota (quando clica em "Transferir" na lista)
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.preSelectedFromId = +id;
      this.transferForm.patchValue({ fromId: this.preSelectedFromId });
    }
    this.loadBeneficios();
  }

  loadBeneficios(): void {
    this.beneficioService.findAll().subscribe({
      next: (data) => {
        this.beneficios = data.filter(b => b.ativo !== false);
      },
      error: (error) => {
        console.error('Erro ao carregar benefícios:', error);
        this.snackBar.open('Erro ao carregar benefícios', 'Fechar', { duration: 3000 });
      }
    });
  }

  onSubmit(): void {
    if (this.transferForm.valid) {
      const fromId = this.transferForm.get('fromId')?.value;
      const toId = this.transferForm.get('toId')?.value;

      if (fromId === toId) {
        this.snackBar.open('Benefício origem e destino não podem ser iguais', 'Fechar', { duration: 3000 });
        return;
      }

      this.loading = true;
      const transferData = this.transferForm.value;

      this.beneficioService.transfer(transferData).subscribe({
        next: (response) => {
          this.snackBar.open(response, 'Fechar', { duration: 5000 });
          this.transferForm.reset();
          this.loading = false;
          // Redirecionar para a lista de benefícios após sucesso
          setTimeout(() => {
            this.router.navigate(['/beneficios']);
          }, 1500);
        },
        error: (error) => {
          console.error('Erro na transferência:', error);
          const errorMessage = error.error || 'Erro ao realizar transferência';
          this.snackBar.open(errorMessage, 'Fechar', { duration: 5000 });
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.router.navigate(['/beneficios']);
  }

  getBeneficioName(id: number): string {
    const beneficio = this.beneficios.find(b => b.id === id);
    return beneficio ? `${beneficio.nome} (R$ ${beneficio.valor?.toFixed(2)})` : '';
  }
}
