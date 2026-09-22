import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MarcheService } from '../../services/marche.service';
import { Marche, FiltreRecherche } from '../../models/marche.model';

@Component({
  selector: 'app-marche-list',
  templateUrl: './marche-list.component.html',
  styleUrls: ['./marche-list.component.scss']
})
export class MarcheListComponent implements OnInit {
  marches: Marche[] = [];
  isLoading = false;

  // ===== MODALE FICHIERS =====
  showFilesModal = false;
  selectedMarcheId: number | null = null;
  selectedFiles: any[] = [];
  filesLoading = false;
  fileCounts: { [key: number]: number } = {};

  constructor(private marcheService: MarcheService, private router: Router) {}

  ngOnInit(): void {
    this.loadMarches();
  }

  loadMarches(): void {
    this.isLoading = true;
    this.marcheService.getAllMarches().subscribe({
      next: (data) => {
        this.marches = data;
        this.isLoading = false;
        this.loadAllFileCounts();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading marches:', error);
      }
    });
  }

  loadAllFileCounts(): void {
    this.marches.forEach(marche => {
      if (marche.id) {
        this.marcheService.getFiles(marche.id).subscribe({
          next: (files) => {
            this.fileCounts[marche.id!] = files.length;
          },
          error: () => { this.fileCounts[marche.id!] = 0; }
        });
      }
    });
  }

  getFileCount(marcheId: number): number {
    return this.fileCounts[marcheId] || 0;
  }

  rechercher(filtres: FiltreRecherche): void {
    this.isLoading = true;
    this.marcheService.rechercherMarches(filtres).subscribe({
      next: (data) => {
        this.marches = data;
        this.isLoading = false;
        this.loadAllFileCounts();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur de recherche:', error);
      }
    });
  }

  navigateToCreate(): void {
    this.router.navigate(['/marches/nouveau']);
  }

  editMarche(id: number): void {
    this.router.navigate([`/marches/editer/${id}`]);
  }

  deleteMarche(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce marché ?')) {
      this.marcheService.deleteMarche(id).subscribe({
        next: () => this.loadMarches(),
        error: (error) => console.error('Error deleting marche:', error)
      });
    }
  }

  refresh(): void {
    this.loadMarches();
  }

  voirDetails(id: number): void {
    this.router.navigate(['/marches/details', id]);
  }

  // ===== GESTION DES FICHIERS =====

  openFilesModal(marcheId: number): void {
    this.selectedMarcheId = marcheId;
    this.showFilesModal = true;
    this.filesLoading = true;
    this.selectedFiles = [];
    
    this.marcheService.getFiles(marcheId).subscribe({
      next: (files) => {
        this.selectedFiles = files;
        this.filesLoading = false;
      },
      error: (error) => {
        console.error('Erreur chargement fichiers:', error);
        this.filesLoading = false;
      }
    });
  }

  closeFilesModal(): void {
    this.showFilesModal = false;
    this.selectedMarcheId = null;
    this.selectedFiles = [];
    this.filesLoading = false;
  }

  downloadFile(marcheId: number, fileId: number, fileName: string): void {
    this.marcheService.downloadFile(marcheId, fileId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => console.error('Erreur téléchargement:', error)
    });
  }

  deleteFile(marcheId: number, fileId: number, index: number): void {
    if (confirm('Supprimer ce fichier ?')) {
      this.marcheService.deleteFile(marcheId, fileId).subscribe({
        next: () => {
          this.selectedFiles.splice(index, 1);
          this.fileCounts[marcheId] = (this.fileCounts[marcheId] || 1) - 1;
        },
        error: (error) => console.error('Erreur suppression:', error)
      });
    }
  }

  viewFile(url: string): void {
    window.open(url, '_blank');
  }

  getFileIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'pdf': '📄', 'doc': '📝', 'docx': '📝',
      'xls': '📊', 'xlsx': '📊',
      'ppt': '📽️', 'pptx': '📽️',
      'jpg': '🖼️', 'jpeg': '🖼️', 'png': '🖼️',
      'mp4': '🎬', 'avi': '🎬',
      'mp3': '🎵',
      'zip': '📦', 'rar': '📦',
      'txt': '📃',
      'default': '📎'
    };
    return icons[type] || icons['default'];
  }
}
