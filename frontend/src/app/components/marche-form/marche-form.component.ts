import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MarcheService } from '../../services/marche.service';
import { PieceJointeService, PieceJointe } from '../../services/piece-jointe.service';
import { REGIONS, COMMUNES } from '../../data/regions.data';

@Component({
  selector: 'app-marche-form',
  templateUrl: './marche-form.component.html',
  styleUrls: ['./marche-form.component.scss']
})
export class MarcheFormComponent implements OnInit {
  marcheForm: FormGroup;
  isEditMode = false;
  marcheId: number | null = null;
  isLoading = false;

  regions = REGIONS;
  departements: any[] = [];
  communes: any[] = [];

  decomptes: { numero: number; montant: number; date: string }[] = [];
  maxDecomptes = 20;

  typeReception: string = 'AUCUNE';
  dateReception: string = '';

  fichiers: PieceJointe[] = [];
  fichiersSelectionnes: File[] = [];
  isDragging = false;
  espaceTotal = 10000000;
  espaceUtilise = 0;

  typeOptions = [
    { value: 'PRESTATION_SERVICE', label: 'Prestation de service' },
    { value: 'PRESTATION_INTELLECTUELLE', label: 'Prestation intellectuelle' },
    { value: 'FOURNITURE', label: 'Fourniture' },
    { value: 'TRAVAUX', label: 'Travaux' },
    { value: 'AUTRE', label: 'Autre' }
  ];

  statutOptions = [
    { value: 'EN_ATTENTE', label: 'En attente' },
    { value: 'ACTIF', label: 'Actif' },
    { value: 'ARCHIVE', label: 'Archivé' },
    { value: 'ANNULE', label: 'Annulé' }
  ];

  constructor(
    private fb: FormBuilder,
    private marcheService: MarcheService,
    private pieceJointeService: PieceJointeService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.marcheForm = this.fb.group({
      reference: ['', Validators.required],
      objet: ['', Validators.required],
      type: ['', Validators.required],
      direction: ['', Validators.required],
      statut: ['EN_ATTENTE'],
      montant: [null, Validators.required],
      description: [''],
      region: [''],
      departement: [''],
      commune: ['']
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.marcheId = +params['id'];
        this.loadMarche(this.marcheId);
        this.loadFiles(this.marcheId);
      }
    });

    this.marcheForm.get('region')?.valueChanges.subscribe((regionCode: string) => {
      if (regionCode) {
        const region = this.regions.find(r => r.code === regionCode);
        this.departements = region?.departements || [];
        this.marcheForm.get('departement')?.setValue('');
        this.marcheForm.get('commune')?.setValue('');
        this.communes = [];
      } else {
        this.departements = [];
        this.communes = [];
      }
    });

    this.marcheForm.get('departement')?.valueChanges.subscribe((deptCode: string) => {
      if (deptCode) {
        this.communes = COMMUNES[deptCode] || [];
        this.marcheForm.get('commune')?.setValue('');
      } else {
        this.communes = [];
      }
    });
  }

  onRegionSelect(event: any): void {
    const regionCode = event.target.value;
    if (regionCode) {
      const region = this.regions.find(r => r.code === regionCode);
      this.departements = region?.departements || [];
      this.marcheForm.get('departement')?.setValue('');
      this.marcheForm.get('commune')?.setValue('');
      this.communes = [];
    } else {
      this.departements = [];
      this.communes = [];
    }
  }

  onDepartementSelect(event: any): void {
    const deptCode = event.target.value;
    if (deptCode) {
      this.communes = COMMUNES[deptCode] || [];
      this.marcheForm.get('commune')?.setValue('');
    } else {
      this.communes = [];
    }
  }

  loadMarche(id: number): void {
    this.marcheService.getMarcheById(id).subscribe({
      next: (marche) => {
        this.marcheForm.patchValue(marche);
        if (marche.region) {
          const region = this.regions.find(r => r.code === marche.region);
          this.departements = region?.departements || [];
          this.marcheForm.get('region')?.setValue(marche.region);
        }
        if (marche.departement) {
          this.communes = COMMUNES[marche.departement] || [];
          this.marcheForm.get('departement')?.setValue(marche.departement);
        }
        if (marche.commune) {
          this.marcheForm.get('commune')?.setValue(marche.commune);
        }
        if (marche.decomptes) {
          this.decomptes = marche.decomptes;
        }
        if (marche.reception) {
          this.typeReception = marche.reception.typeReception;
          this.dateReception = marche.reception.dateReception || '';
        }
      },
      error: (error) => {
        console.error('Error loading marche:', error);
        alert('❌ Erreur lors du chargement du marché');
        this.router.navigate(['/marches']);
      }
    });
  }

  loadFiles(marcheId: number): void {
    this.pieceJointeService.getFiles(marcheId).subscribe({
      next: (files) => {
        this.fichiers = files;
        this.calculerEspace();
      },
      error: (error) => console.error('Erreur chargement fichiers:', error)
    });
  }

  ajouterDecompte(): void {
    if (this.decomptes.length < this.maxDecomptes) {
      this.decomptes.push({
        numero: this.decomptes.length + 1,
        montant: 0,
        date: new Date().toISOString().split('T')[0]
      });
    } else {
      alert('Maximum 20 décomptes atteint');
    }
  }

  supprimerDecompte(index: number): void {
    this.decomptes.splice(index, 1);
    this.decomptes.forEach((d, i) => d.numero = i + 1);
  }

  onTypeReceptionChange(): void {
    if (this.typeReception === 'AUCUNE') {
      this.dateReception = '';
    }
  }

  getIcone(type: string): string {
    const icones: { [key: string]: string } = {
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
    return icones[type] || icones['default'];
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDropFile(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    const files = event.dataTransfer?.files;
    if (files) {
      this.ajouterFichiers(files);
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (files) {
      this.ajouterFichiers(files);
    }
    input.value = '';
  }

  ajouterFichiers(files: FileList): void {
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.size > 100 * 1024 * 1024) {
        alert(`Le fichier ${file.name} dépasse 100MB`);
        continue;
      }
      this.fichiersSelectionnes.push(file);
      this.espaceUtilise += file.size;
    }
    this.calculerEspace();
  }

  removeFileSelection(index: number): void {
    const file = this.fichiersSelectionnes[index];
    this.espaceUtilise -= file.size;
    this.fichiersSelectionnes.splice(index, 1);
    this.calculerEspace();
  }

  supprimerFichier(marcheId: number, pieceId: number, index: number): void {
    if (confirm('Supprimer ce fichier ?')) {
      this.pieceJointeService.deleteFile(marcheId, pieceId).subscribe({
        next: () => {
          this.fichiers.splice(index, 1);
          this.calculerEspace();
        },
        error: (error) => console.error('Erreur suppression:', error)
      });
    }
  }

  telechargerFichier(marcheId: number, pieceId: number, nom: string): void {
    this.pieceJointeService.downloadFile(marcheId, pieceId).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nom;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => console.error('Erreur téléchargement:', error)
    });
  }

  visualiserFichier(url: string): void {
    window.open(url, '_blank');
  }

  calculerEspace(): void {
    let total = 0;
    this.fichiers.forEach(f => total += f.taille);
    this.fichiersSelectionnes.forEach(f => total += f.size);
    this.espaceUtilise = total;
  }

  getEspaceUtilise(): string {
    return (this.espaceUtilise / (1024 * 1024 * 1024)).toFixed(2);
  }

  getEspaceTotal(): string {
    return '10 000';
  }

  getPourcentageEspace(): number {
    return (this.espaceUtilise / (this.espaceTotal * 1024 * 1024 * 1024)) * 100;
  }

  uploadFiles(marcheId: number): void {
    this.fichiersSelectionnes.forEach((file) => {
      this.pieceJointeService.uploadFile(marcheId, file).subscribe({
        next: (response) => {
          this.fichiers.push(response);
          this.calculerEspace();
        },
        error: (error) => console.error('Erreur upload:', error)
      });
    });
    this.fichiersSelectionnes = [];
  }

  onSubmit(): void {
    if (this.marcheForm.valid) {
      this.isLoading = true;
      const formValue = this.marcheForm.value;

      const marche = {
        reference: formValue.reference,
        objet: formValue.objet,
        type: formValue.type || 'AUTRE',
        direction: formValue.direction || '',
        statut: formValue.statut || 'EN_ATTENTE',
        montant: formValue.montant,
        description: formValue.description || '',
        region: formValue.region || '',
        departement: formValue.departement || '',
        commune: formValue.commune || '',
        titulaire: 'Non spécifié',
        sourceFinancement: 'Budget National',
        duree: '12 mois',
        dateCreation: new Date().toISOString()
      };

      console.log('📦 Données envoyées:', marche);

      if (this.isEditMode && this.marcheId) {
        this.marcheService.updateMarche(this.marcheId, marche).subscribe({
          next: (response) => {
            this.isLoading = false;
            this.uploadFiles(response.id);
            this.router.navigate(['/marches']);
          },
          error: (error) => {
            this.isLoading = false;
            console.error('Error updating marche:', error);
            alert('❌ Erreur lors de la mise à jour');
          }
        });
      } else {
        this.marcheService.createMarche(marche).subscribe({
          next: (response) => {
            this.isLoading = false;
            if (this.fichiersSelectionnes.length > 0) {
              this.uploadFiles(response.id);
            }
            this.router.navigate(['/marches']);
          },
          error: (error) => {
            this.isLoading = false;
            console.error('❌ Erreur création:', error);
            alert('❌ Erreur lors de la création: ' + (error.error?.message || error.message));
          }
        });
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/marches']);
  }
}
