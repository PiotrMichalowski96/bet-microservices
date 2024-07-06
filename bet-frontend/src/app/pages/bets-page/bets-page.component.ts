import {Component, OnInit} from '@angular/core';
import {Bet} from "../../model/bet";
import {BetsService} from "../../services/bets.service";
import {RESPONSIVE_CAROUSEL_OPTIONS} from "../../util/carousel-properties";

@Component({
  selector: 'app-bets-page',
  templateUrl: './bets-page.component.html',
  styleUrls: ['./bets-page.component.scss']
})
export class BetsPageComponent implements OnInit {

  responsiveOptions;

  myOwnBets: Bet[] = [];
  allBets: Bet[] = [];
  totalBets: number = 0;

  constructor(private betsService: BetsService) {
    this.responsiveOptions = RESPONSIVE_CAROUSEL_OPTIONS;
  }

  ngOnInit(): void {
    this.getMyOwnBets();
    this.getPagedAllBets();
    this.getTotalBets();
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

  private getTotalBets(): void {
    this.betsService.getNumberOfBets().subscribe(betsNumber => {
      this.totalBets = betsNumber;
    });
  }

  paginate(event: any): void {
    this.getPagedAllBets(event.page);
  }

  protected readonly BetsService = BetsService;
}
