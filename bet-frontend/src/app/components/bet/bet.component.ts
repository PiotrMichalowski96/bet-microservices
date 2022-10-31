import {Component, Input} from '@angular/core';
import {Bet} from "../../model/bet";

@Component({
  selector: 'app-bet',
  templateUrl: './bet.component.html',
  styleUrls: ['./bet.component.scss']
})
export class BetComponent {

  @Input() bet: Bet | null = null;

}
