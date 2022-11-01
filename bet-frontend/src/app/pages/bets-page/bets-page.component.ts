import {Component, OnInit} from '@angular/core';
import {Bet} from "../../model/bet";
import {BetsService} from "../../services/bets.service";

@Component({
  selector: 'app-bets-page',
  templateUrl: './bets-page.component.html',
  styleUrls: ['./bets-page.component.css']
})
export class BetsPageComponent implements OnInit {

  myOwnBets: Bet[] = [];
  allBets: Bet[] = [];

  constructor(private betsService: BetsService) {
  }

  ngOnInit(): void {
    this.getMyOwnBets();
    this.getPagedAllBets();
  }

  private getMyOwnBets(): void {
    this.betsService.getMyOwnBets().subscribe(bets => {
      this.myOwnBets = bets;
    });
  }

  private getPagedAllBets(page: number = 0): void {
    this.betsService.getBets(page).subscribe(bets => {
      this.allBets = bets;
    });
  }

  paginate(event: any): void {
    this.getPagedAllBets(event.page);
  }
}
