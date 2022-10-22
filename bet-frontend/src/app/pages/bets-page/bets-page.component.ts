import { Component, OnInit } from '@angular/core';
import {Bet} from "../../model/bet";
import {BetsService} from "../../services/bets.service";

@Component({
  selector: 'app-bets-page',
  templateUrl: './bets-page.component.html',
  styleUrls: ['./bets-page.component.css']
})
export class BetsPageComponent implements OnInit {

  bets: Bet[] = [];

  constructor(private betsService: BetsService) { }

  ngOnInit(): void {
    this.getPagedBets();
  }

  private getPagedBets(page: number = 0) {
    this.betsService.getBets(page).subscribe(bets => {
      this.bets = bets;
    });
  }

  paginate(event: any) {
    this.getPagedBets(event.page);
  }
}
