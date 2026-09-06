import { Component, OnInit } from '@angular/core';
import { MarcheService } from '../../services/marche.service';
@Component({ selector: 'app-dashboard', templateUrl: './dashboard.component.html', styleUrls: ['./dashboard.component.scss'] })
export class DashboardComponent implements OnInit {
  totalMarches = 0; actifCount = 0; enAttenteCount = 0; archiveCount = 0; recentMarches: any[] = [];
  constructor(private marcheService: MarcheService) {}
  ngOnInit() { this.loadData(); }
  loadData() {
    this.marcheService.getAllMarches().subscribe({
      next: (marches) => {
        this.totalMarches = marches.length;
        this.actifCount = marches.filter((m:any) => m.statut === 'ACTIF').length;
        this.enAttenteCount = marches.filter((m:any) => m.statut === 'EN_ATTENTE').length;
        this.archiveCount = marches.filter((m:any) => m.statut === 'ARCHIVE').length;
        this.recentMarches = marches.slice(0, 5);
      },
      error: (error) => console.error(error)
    });
  }
}
