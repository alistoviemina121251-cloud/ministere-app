import { Region, Departement, Commune } from '../models/marche.model';

export const REGIONS: Region[] = [
  {
    id: 1,
    nom: 'Agadez',
    code: 'AG',
    departements: [
      { id: 1, nom: 'Arlit', code: 'ARL', regionId: 1 },
      { id: 2, nom: 'Bilma', code: 'BIL', regionId: 1 },
      { id: 3, nom: 'Tchirozérine', code: 'TCH', regionId: 1 }
    ]
  },
  {
    id: 2,
    nom: 'Diffa',
    code: 'DI',
    departements: [
      { id: 4, nom: 'Diffa', code: 'DIF', regionId: 2 },
      { id: 5, nom: 'Maïné-Soroa', code: 'MAI', regionId: 2 },
      { id: 6, nom: 'Nguigmi', code: 'NGU', regionId: 2 }
    ]
  },
  {
    id: 3,
    nom: 'Dosso',
    code: 'DO',
    departements: [
      { id: 7, nom: 'Boboye', code: 'BOB', regionId: 3 },
      { id: 8, nom: 'Dosso', code: 'DOS', regionId: 3 },
      { id: 9, nom: 'Gaya', code: 'GAY', regionId: 3 },
      { id: 10, nom: 'Loga', code: 'LOG', regionId: 3 }
    ]
  },
  {
    id: 4,
    nom: 'Maradi',
    code: 'MA',
    departements: [
      { id: 11, nom: 'Aguie', code: 'AGU', regionId: 4 },
      { id: 12, nom: 'Dakoro', code: 'DAK', regionId: 4 },
      { id: 13, nom: 'Guidan Roumdji', code: 'GUI', regionId: 4 },
      { id: 14, nom: 'Madarounfa', code: 'MAD', regionId: 4 },
      { id: 15, nom: 'Mayahi', code: 'MAY', regionId: 4 },
      { id: 16, nom: 'Tessaoua', code: 'TES', regionId: 4 }
    ]
  },
  {
    id: 5,
    nom: 'Niamey',
    code: 'NY',
    departements: [
      { id: 17, nom: 'Niamey I', code: 'NY1', regionId: 5 },
      { id: 18, nom: 'Niamey II', code: 'NY2', regionId: 5 },
      { id: 19, nom: 'Niamey III', code: 'NY3', regionId: 5 },
      { id: 20, nom: 'Niamey IV', code: 'NY4', regionId: 5 },
      { id: 21, nom: 'Niamey V', code: 'NY5', regionId: 5 }
    ]
  },
  {
    id: 6,
    nom: 'Tahoua',
    code: 'TA',
    departements: [
      { id: 22, nom: 'Abalak', code: 'ABA', regionId: 6 },
      { id: 23, nom: 'Birni N\'Konni', code: 'BIR', regionId: 6 },
      { id: 24, nom: 'Bouza', code: 'BOU', regionId: 6 },
      { id: 25, nom: 'Illela', code: 'ILL', regionId: 6 },
      { id: 26, nom: 'Keita', code: 'KEI', regionId: 6 },
      { id: 27, nom: 'Madaoua', code: 'MAD', regionId: 6 },
      { id: 28, nom: 'Tahoua', code: 'TAH', regionId: 6 },
      { id: 29, nom: 'Tchin-Tabaraden', code: 'TCT', regionId: 6 }
    ]
  },
  {
    id: 7,
    nom: 'Tillabéri',
    code: 'TI',
    departements: [
      { id: 30, nom: 'Filingue', code: 'FIL', regionId: 7 },
      { id: 31, nom: 'Kollo', code: 'KOL', regionId: 7 },
      { id: 32, nom: 'Ouallam', code: 'OUA', regionId: 7 },
      { id: 33, nom: 'Say', code: 'SAY', regionId: 7 },
      { id: 34, nom: 'Téra', code: 'TER', regionId: 7 },
      { id: 35, nom: 'Tillabéri', code: 'TIL', regionId: 7 }
    ]
  },
  {
    id: 8,
    nom: 'Zinder',
    code: 'ZI',
    departements: [
      { id: 36, nom: 'Gouré', code: 'GOU', regionId: 8 },
      { id: 37, nom: 'Magaria', code: 'MAG', regionId: 8 },
      { id: 38, nom: 'Matameye', code: 'MAT', regionId: 8 },
      { id: 39, nom: 'Mirriah', code: 'MIR', regionId: 8 },
      { id: 40, nom: 'Tanout', code: 'TAN', regionId: 8 }
    ]
  }
];

export const COMMUNES: { [key: string]: Commune[] } = {
  // ========== NIAMEY ==========
  'NY1': [
    { id: 1, nom: 'Niamey Centre', code: 'NYC', departementId: 17, type: 'URBAINE' },
    { id: 2, nom: 'Niamey Nord', code: 'NYN', departementId: 17, type: 'URBAINE' }
  ],
  'NY2': [
    { id: 3, nom: 'Niamey Est', code: 'NYE', departementId: 18, type: 'URBAINE' },
    { id: 4, nom: 'Niamey Ouest', code: 'NYO', departementId: 18, type: 'URBAINE' }
  ],
  'NY3': [
    { id: 5, nom: 'Niamey Sud', code: 'NYS', departementId: 19, type: 'URBAINE' }
  ],
  'NY4': [
    { id: 6, nom: 'Niamey Plateau', code: 'NYP', departementId: 20, type: 'URBAINE' }
  ],
  'NY5': [
    { id: 7, nom: 'Niamey Poudrière', code: 'NYP2', departementId: 21, type: 'URBAINE' }
  ],
  // ========== DOSSO ==========
  'DOS': [
    { id: 8, nom: 'Dosso Ville', code: 'DOV', departementId: 8, type: 'URBAINE' },
    { id: 9, nom: 'Dosso Rural', code: 'DOR', departementId: 8, type: 'RURALE' }
  ],
  'GAY': [
    { id: 10, nom: 'Gaya Ville', code: 'GAV', departementId: 9, type: 'URBAINE' },
    { id: 11, nom: 'Gaya Rural', code: 'GAR', departementId: 9, type: 'RURALE' }
  ],
  'LOG': [
    { id: 12, nom: 'Loga Ville', code: 'LOV', departementId: 10, type: 'URBAINE' }
  ],
  'BOB': [
    { id: 13, nom: 'Boboye Ville', code: 'BOV', departementId: 7, type: 'URBAINE' }
  ],
  // ========== MARADI ==========
  'AGU': [
    { id: 14, nom: 'Aguie Ville', code: 'AGV', departementId: 11, type: 'URBAINE' }
  ],
  'DAK': [
    { id: 15, nom: 'Dakoro Ville', code: 'DAV', departementId: 12, type: 'URBAINE' }
  ],
  'GUI': [
    { id: 16, nom: 'Guidan Roumdji Ville', code: 'GRV', departementId: 13, type: 'URBAINE' }
  ],
  'MAD': [
    { id: 17, nom: 'Madarounfa Ville', code: 'MDV', departementId: 14, type: 'URBAINE' }
  ],
  'MAY': [
    { id: 18, nom: 'Mayahi Ville', code: 'MYV', departementId: 15, type: 'URBAINE' }
  ],
  'TES': [
    { id: 19, nom: 'Tessaoua Ville', code: 'TSV', departementId: 16, type: 'URBAINE' }
  ],
  // ========== TAHOUA ==========
  'TAH': [
    { id: 20, nom: 'Tahoua Ville', code: 'TAV', departementId: 28, type: 'URBAINE' },
    { id: 21, nom: 'Tahoua Rural', code: 'TAR', departementId: 28, type: 'RURALE' }
  ],
  'ABA': [
    { id: 22, nom: 'Abalak Ville', code: 'ABV', departementId: 22, type: 'URBAINE' }
  ],
  'BIR': [
    { id: 23, nom: 'Birni N\'Konni Ville', code: 'BKV', departementId: 23, type: 'URBAINE' }
  ],
  'BOU': [
    { id: 24, nom: 'Bouza Ville', code: 'BZV', departementId: 24, type: 'URBAINE' }
  ],
  'ILL': [
    { id: 25, nom: 'Illela Ville', code: 'ILV', departementId: 25, type: 'URBAINE' }
  ],
  'KEI': [
    { id: 26, nom: 'Keita Ville', code: 'KEV', departementId: 26, type: 'URBAINE' }
  ],
  // ========== TILLABÉRI ==========
  'TIL': [
    { id: 27, nom: 'Tillabéri Ville', code: 'TIV', departementId: 35, type: 'URBAINE' }
  ],
  'FIL': [
    { id: 28, nom: 'Filingue Ville', code: 'FIV', departementId: 30, type: 'URBAINE' }
  ],
  'KOL': [
    { id: 29, nom: 'Kollo Ville', code: 'KOV', departementId: 31, type: 'URBAINE' }
  ],
  'OUA': [
    { id: 30, nom: 'Ouallam Ville', code: 'OUV', departementId: 32, type: 'URBAINE' }
  ],
  'SAY': [
    { id: 31, nom: 'Say Ville', code: 'SAV', departementId: 33, type: 'URBAINE' }
  ],
  'TER': [
    { id: 32, nom: 'Téra Ville', code: 'TEV', departementId: 34, type: 'URBAINE' }
  ],
  // ========== ZINDER ==========
  'MIR': [
    { id: 33, nom: 'Mirriah Ville', code: 'MIV', departementId: 39, type: 'URBAINE' }
  ],
  'GOU': [
    { id: 34, nom: 'Gouré Ville', code: 'GOV', departementId: 36, type: 'URBAINE' }
  ],
  'MAG': [
    { id: 35, nom: 'Magaria Ville', code: 'MGV', departementId: 37, type: 'URBAINE' }
  ],
  'MAT': [
    { id: 36, nom: 'Matameye Ville', code: 'MTV', departementId: 38, type: 'URBAINE' }
  ],
  'TAN': [
    { id: 37, nom: 'Tanout Ville', code: 'TNV', departementId: 40, type: 'URBAINE' }
  ],
  // ========== AGADEZ ==========
  'ARL': [
    { id: 38, nom: 'Arlit Ville', code: 'ALV', departementId: 1, type: 'URBAINE' }
  ],
  'BIL': [
    { id: 39, nom: 'Bilma Ville', code: 'BLV', departementId: 2, type: 'URBAINE' }
  ],
  // ========== DIFFA ==========
  'DIF': [
    { id: 40, nom: 'Diffa Ville', code: 'DFV', departementId: 4, type: 'URBAINE' }
  ],
  'MAI': [
    { id: 41, nom: 'Maïné-Soroa Ville', code: 'MSV', departementId: 5, type: 'URBAINE' }
  ],
  'NGU': [
    { id: 42, nom: 'Nguigmi Ville', code: 'NGV', departementId: 6, type: 'URBAINE' }
  ]
};
