import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BeneficioService } from '../../services/beneficio.service';
import { BeneficioRequest } from '../../models/beneficio';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatCheckboxModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './beneficio-form.component.html',
  styleUrl: './beneficio-form.component.scss'
})
export class BeneficioFormComponent implements OnInit {
  beneficioForm: FormGroup;
  isEditMode = false;
  beneficioId?: number;
  loading = false;
  error: string | null = null;
  submitted = false;

  constructor(
    private fb: FormBuilder,
    private beneficioService: BeneficioService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.beneficioForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      descricao: [''],
      valor: ['', [Validators.required, Validators.min(0)]],
      ativo: [true]
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.beneficioId = +id;
      this.loadBeneficio(this.beneficioId);
    }
  }

  loadBeneficio(id: number): void {
    this.loading = true;
    this.beneficioService.findById(id).subscribe({
      next: (beneficio) => {
        this.beneficioForm.patchValue({
          nome: beneficio.nome,
          descricao: beneficio.descricao || '',
          valor: beneficio.valor,
          ativo: beneficio.ativo
        });
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar benefício';
        this.loading = false;
        console.error(err);
      }
    });
  }

  onSubmit(): void {
    this.submitted = true;
    
    if (this.beneficioForm.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;

    const request: BeneficioRequest = {
      nome: this.beneficioForm.value.nome,
      descricao: this.beneficioForm.value.descricao || undefined,
      valor: this.beneficioForm.value.valor,
      ativo: this.beneficioForm.value.ativo
    };

    const operation = this.isEditMode
      ? this.beneficioService.update(this.beneficioId!, request)
      : this.beneficioService.create(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/beneficios']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Erro ao salvar benefício';
        this.loading = false;
        console.error(err);
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/beneficios']);
  }

  get f() {
    return this.beneficioForm.controls;
  }
}
