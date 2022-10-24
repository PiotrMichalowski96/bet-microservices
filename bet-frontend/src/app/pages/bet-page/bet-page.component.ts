import { Component, OnInit } from '@angular/core';
import {Bet} from "../../model/bet";
import {ActivatedRoute} from "@angular/router";
import {BetsService} from "../../services/bets.service";
import {first} from "rxjs";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-bet-page',
  templateUrl: './bet-page.component.html',
  styleUrls: ['./bet-page.component.scss']
})
export class BetPageComponent implements OnInit {

  bet: Bet | null = null;

  betPredictionForm : FormGroup = this.formBuilder.group({
    homeTeamGoalsPrediction: 0,
    awayTeamGoalsPrediction: 0
  });

  constructor(private route: ActivatedRoute,
              private betsService: BetsService,
              private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.route.params.pipe(first()).subscribe(({id}) => {
      this.getBet(id);
    });
    console.log(this.bet);
  }

  private getBet(id: number) {
    this.betsService.getBet(id).subscribe(bet => {
      this.bet = bet;
    });
  }

  onSubmit(): void {
    console.log('Submit ', this.betPredictionForm.value)
  }

}
