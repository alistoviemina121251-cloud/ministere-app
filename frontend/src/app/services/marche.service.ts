import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Marche, FiltreRecherche } from '../models/marche.model';

@Injectable({
  providedIn: 'root'
})
export class MarcheService {
  private apiUrl = 'https://ministere-app.onrender.com/api/marches';

  constructor(private http: HttpClient) { }

  getAllMarches(): Observable<Marche[]> {
    return this.http.get<Marche[]>(this.apiUrl);
  }

  getMarcheById(id: number): Observable<Marche> {
    return this.http.get<Marche>(`${this.apiUrl}/${id}`);
  }

  createMarche(marche: any): Observable<any> {
    const cleaned = Object.keys(marche).reduce((acc: any, key) => {
      if (marche[key] !== null && marche[key] !== undefined && marche[key] !== '') {
        acc[key] = marche[key];
      }
      return acc;
    }, {});
    return this.http.post<any>(this.apiUrl, cleaned);
  }

  updateMarche(id: number, marche: any): Observable<any> {
    const cleaned = Object.keys(marche).reduce((acc: any, key) => {
      if (marche[key] !== null && marche[key] !== undefined && marche[key] !== '') {
        acc[key] = marche[key];
      }
      return acc;
    }, {});
    return this.http.put<any>(`${this.apiUrl}/${id}`, cleaned);
  }

  deleteMarche(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  rechercherMarches(filtres: FiltreRecherche): Observable<Marche[]> {
    return this.http.post<Marche[]>(`${this.apiUrl}/recherche`, filtres);
  }

  // ===== PIÈCES JOINTES =====
  getFiles(marcheId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${marcheId}/pieces`);
  }

  uploadFile(marcheId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${marcheId}/pieces`, formData);
  }

  deleteFile(marcheId: number, pieceId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${marcheId}/pieces/${pieceId}`);
  }

  downloadFile(marcheId: number, pieceId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${marcheId}/pieces/${pieceId}`, {
      responseType: 'blob'
    });
  }
}
