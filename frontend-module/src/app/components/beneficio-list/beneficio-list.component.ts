import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio';

@Component({
  selector: 'app-beneficio-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatInputModule,
    MatFormFieldModule,
    MatTooltipModule,
    FormsModule
  ],
  templateUrl: './beneficio-list.component.html',
  styleUrl: './beneficio-list.component.scss'
})
export class BeneficioListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  displayedColumns: string[] = ['id', 'nome', 'descricao', 'valor', 'ativo', 'actions'];
  searchTerm: string = '';
  loading: boolean = false;

  constructor(
    private beneficioService: BeneficioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBeneficios();
  }

  loadBeneficios(): void {
    this.loading = true;
    this.beneficioService.findAll().subscribe({
      next: (data) => {
        this.beneficios = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar benefícios:', error);
        this.loading = false;
      }
    });
  }

  searchBeneficios(): void {
    if (this.searchTerm.trim()) {
      this.loading = true;
      this.beneficioService.searchByName(this.searchTerm).subscribe({
        next: (data) => {
          this.beneficios = data;
          this.loading = false;
        },
        error: (error) => {
          console.error('Erro na busca:', error);
          this.loading = false;
        }
      });
    } else {
      this.loadBeneficios();
    }
  }

  onCreate(): void {
    this.router.navigate(['/beneficios/create']);
  }

  onEdit(id: number): void {
    this.router.navigate(['/beneficios/edit', id]);
  }

  onTransfer(id: number): void {
    this.router.navigate(['/beneficios/transfer', id]);
  }

  deleteBeneficio(id: number): void {
    if (confirm('Tem certeza que deseja excluir este benefício?')) {
      this.beneficioService.delete(id).subscribe({
        next: () => {
          this.loadBeneficios();
        },
        error: (error) => {
          console.error('Erro ao excluir benefício:', error);
        }
      });
    }
  }
}
