import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio, BeneficioRequest, TransferRequest } from '../models/beneficio';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private baseUrl = 'http://localhost:8080/api/v1/beneficios';

  constructor(private http: HttpClient) { }

  // GET /api/v1/beneficios
  findAll(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.baseUrl);
  }

  // GET /api/v1/beneficios/{id}
  findById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.baseUrl}/${id}`);
  }

  // POST /api/v1/beneficios
  create(beneficio: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, beneficio);
  }

  // PUT /api/v1/beneficios/{id}
  update(id: number, beneficio: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, beneficio);
  }

  // DELETE /api/v1/beneficios/{id}
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // GET /api/v1/beneficios/search?nome={nome}
  searchByName(nome: string): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.baseUrl}/search`, {
      params: { nome }
    });
  }

  // POST /api/v1/beneficios/transfer
  transfer(transferRequest: TransferRequest): Observable<string> {
    return this.http.post(`${this.baseUrl}/transfer`, transferRequest, {
      responseType: 'text'
    });
  }
}
