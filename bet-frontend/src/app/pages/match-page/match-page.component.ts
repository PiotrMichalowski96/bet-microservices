import { Component, OnInit } from '@angular/core';
import {Match} from "../../model/match";
import {MatchesService} from "../../services/matches.service";
import {ActivatedRoute} from "@angular/router";
import {first} from "rxjs";

@Component({
  selector: 'app-match-page',
  templateUrl: './match-page.component.html',
  styleUrls: ['./match-page.component.scss']
})
export class MatchPageComponent implements OnInit {

  match: Match | null = null;

  constructor(private route: ActivatedRoute, private matchesService: MatchesService) { }

  ngOnInit(): void {
    this.route.params.pipe(first()).subscribe(({id}) => {
      this.getMatch(id);
    });
  }

  private getMatch(id: number) {
    this.matchesService.getMatch(id).subscribe(match => {
      this.match = match;
    });
  }
}
