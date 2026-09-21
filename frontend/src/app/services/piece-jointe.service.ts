import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PieceJointe {
  id?: number;
  nom: string;
  type: string;
  taille: number;
  dateUpload: string;
  chemin: string;
  icone: string;
  url?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PieceJointeService {
  private apiUrl = 'https://ministere-app.onrender.com/api/marches';

  constructor(private http: HttpClient) { }

  uploadFile(marcheId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/${marcheId}/pieces`, formData);
  }

  getFiles(marcheId: number): Observable<PieceJointe[]> {
    return this.http.get<PieceJointe[]>(`${this.apiUrl}/${marcheId}/pieces`);
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