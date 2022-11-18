import {Component, OnInit} from '@angular/core';
import {Bet} from "../../model/bet";
import {BetsService} from "../../services/bets.service";

@Component({
  selector: 'app-bets-page',
  templateUrl: './bets-page.component.html',
  styleUrls: ['./bets-page.component.css']
})
export class BetsPageComponent implements OnInit {

  responsiveOptions;

  myOwnBets: Bet[] = [];
  allBets: Bet[] = [];

  constructor(private betsService: BetsService) {
    this.responsiveOptions = [
      {
        breakpoint: '1024px',
        numVisible: 3,
        numScroll: 3
      },
      {
        breakpoint: '768px',
        numVisible: 2,
        numScroll: 2
      },
      {
        breakpoint: '560px',
        numVisible: 1,
        numScroll: 1
      }
    ];
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
