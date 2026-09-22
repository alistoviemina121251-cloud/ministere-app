import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { StatutArchive, TypeMarche, FiltreRecherche } from '../../models/marche.model';
import { REGIONS, COMMUNES } from '../../data/regions.data';

@Component({
  selector: 'app-recherche-marche',
  templateUrl: './recherche-marche.component.html',
  styleUrls: ['./recherche-marche.component.scss']
})
export class RechercheMarcheComponent implements OnInit {
  @Output() search = new EventEmitter<FiltreRecherche>();

  filtersVisible: boolean = true;
  totalResults: number | null = null;
  tempsRecherche: number = 0;

  regions = REGIONS;
  departements: any[] = [];
  communes: any[] = [];

  searchForm: FormGroup;

  statutOptions = [
    { value: 'ACTIF', label: '✅ Actif' },
    { value: 'EN_COURS', label: '⏳ En cours' },
    { value: 'EN_ATTENTE', label: '⏳ En attente' },
    { value: 'ARCHIVE', label: '📁 Archivé' },
    { value: 'ANNULE', label: '❌ Annulé' }
  ];

  typeOptions = [
    { value: 'PRESTATION_SERVICE', label: 'Prestation de service' },
    { value: 'PRESTATION_INTELLECTUELLE', label: 'Prestation intellectuelle' },
    { value: 'FOURNITURE', label: 'Fourniture' },
    { value: 'TRAVAUX', label: 'Travaux' },
    { value: 'AUTRE', label: 'Autre' }
  ];

  constructor(private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      motCle: [''],
      direction: [''],
      statut: [''],
      type: [''],
      dateDebut: [''],
      dateFin: [''],
      montantMin: [''],
      montantMax: [''],
      region: [''],
      departement: [''],
      commune: [''],
      titulaire: [''],
      reference: ['']
    });
  }

  ngOnInit(): void {
    // ✅ Surveiller les changements de région
    this.searchForm.get('region')?.valueChanges.subscribe((regionCode: string) => {
      console.log('🔵 Recherche - Région sélectionnée:', regionCode);
      this.onRegionChange(regionCode);
    });

    // ✅ Surveiller les changements de département
    this.searchForm.get('departement')?.valueChanges.subscribe((deptCode: string) => {
      console.log('🟢 Recherche - Département sélectionné:', deptCode);
      this.onDepartementChange(deptCode);
    });
  }

  // ✅ Méthode appelée quand la région change
  onRegionChange(regionCode: string): void {
    if (regionCode) {
      const region = this.regions.find(r => r.code === regionCode);
      this.departements = region?.departements || [];
      this.searchForm.get('departement')?.setValue('');
      this.searchForm.get('commune')?.setValue('');
      this.communes = [];
    } else {
      this.departements = [];
      this.communes = [];
    }
    this.onSearch();
  }

  // ✅ Méthode appelée quand le département change
  onDepartementChange(deptCode: string): void {
    if (deptCode) {
      this.communes = COMMUNES[deptCode] || [];
      this.searchForm.get('commune')?.setValue('');
    } else {
      this.communes = [];
    }
    this.onSearch();
  }

  // ✅ Méthode appelée depuis le HTML (change)
  onRegionSelect(event: any): void {
    const regionCode = event.target.value;
    console.log('🔴 Recherche - onRegionSelect:', regionCode);
    this.onRegionChange(regionCode);
  }

  // ✅ Méthode appelée depuis le HTML (change)
  onDepartementSelect(event: any): void {
    const deptCode = event.target.value;
    console.log('🔴 Recherche - onDepartementSelect:', deptCode);
    this.onDepartementChange(deptCode);
  }

  onSearch(): void {
    const debut = Date.now();
    const formValues = this.searchForm.value;

    const filtres: FiltreRecherche = {
      motCle: formValues.motCle || undefined,
      direction: formValues.direction || undefined,
      statut: formValues.statut || undefined,
      type: formValues.type || undefined,
      dateDebut: formValues.dateDebut || undefined,
      dateFin: formValues.dateFin || undefined,
      montantMin: formValues.montantMin || undefined,
      montantMax: formValues.montantMax || undefined,
      region: formValues.region || undefined,
      departement: formValues.departement || undefined,
      commune: formValues.commune || undefined,
      titulaire: formValues.titulaire || undefined,
      reference: formValues.reference || undefined
    };

    this.search.emit(filtres);
    this.tempsRecherche = Date.now() - debut;
  }

  resetFilters(): void {
    this.searchForm.reset({
      statut: '',
      type: '',
      region: '',
      departement: '',
      commune: ''
    });
    this.departements = [];
    this.communes = [];
    this.onSearch();
  }

  clearSearch(): void {
    this.searchForm.get('motCle')?.setValue('');
    this.onSearch();
  }
}
