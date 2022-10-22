import {Component, Input, OnInit} from '@angular/core';
import {Bet} from "../../model/bet";

@Component({
  selector: 'app-bet',
  templateUrl: './bet.component.html',
  styleUrls: ['./bet.component.scss']
})
export class BetComponent implements OnInit {

  @Input() bet: Bet | null = null;

  constructor() { }

  ngOnInit(): void {
  }

}
