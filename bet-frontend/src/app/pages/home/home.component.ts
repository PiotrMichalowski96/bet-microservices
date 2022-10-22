import { Component, OnInit } from '@angular/core';
import {Match} from "../../model/match";
import {MatchesService} from "../../services/matches.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  upcomingMatches: Match[] = [];
  allMatches: Match[] = [];

  constructor(private matchesService: MatchesService) { }

  ngOnInit(): void {
    this.matchesService.getUpcomingMatches().subscribe((matches: Match[]) => {
      this.upcomingMatches = matches;
    });
    this.matchesService.getMatches().subscribe((matches: Match[]) => {
      this.allMatches = matches;
    });
  }

}
