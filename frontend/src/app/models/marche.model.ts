export enum StatutArchive {
  ACTIF = 'ACTIF',
  EN_COURS = 'EN_COURS',
  EN_ATTENTE = 'EN_ATTENTE',
  ARCHIVE = 'ARCHIVE',
  ANNULE = 'ANNULE'
}

export enum TypeMarche {
  PRESTATION_SERVICE = 'PRESTATION_SERVICE',
  PRESTATION_INTELLECTUELLE = 'PRESTATION_INTELLECTUELLE',
  FOURNITURE = 'FOURNITURE',
  TRAVAUX = 'TRAVAUX',
  AUTRE = 'AUTRE'
}

export interface Region {
  id: number;
  nom: string;
  code: string;
  departements?: Departement[];
}

export interface Departement {
  id: number;
  nom: string;
  code: string;
  regionId: number;
  communes?: Commune[];
}

export interface Commune {
  id: number;
  nom: string;
  code: string;
  departementId: number;
  type: 'URBAINE' | 'RURALE';
}

// ===== DÉCOMPTE =====
export interface Decompte {
  numero: number;
  montant: number;
  date: string;
}

// ===== RÉCEPTION =====
export interface Reception {
  typeReception: 'PROVISOIRE' | 'DEFINITIVE';
  dateReception: string;
}

export interface Marche {
  id?: number;
  reference: string;
  objet: string;
  type: TypeMarche;
  direction: string;
  titulaire: string;
  montant: number;
  statut: StatutArchive;
  description?: string;
  dateCreation?: string;
  region?: string;
  departement?: string;
  commune?: string;
  decomptes?: Decompte[];
  reception?: Reception;
}

export interface FiltreRecherche {
  motCle?: string;
  direction?: string;
  statut?: string;
  type?: string;
  region?: string;
  departement?: string;
  commune?: string;
  titulaire?: string;
  reference?: string;
  dateDebut?: string;
  dateFin?: string;
  montantMin?: number;
  montantMax?: number;
}
