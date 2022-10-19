import {Component, Input} from '@angular/core';
import {Match} from "../../model/match";

@Component({
  selector: 'app-match-banner',
  templateUrl: './match-banner.component.html',
  styleUrls: ['./match-banner.component.css']
})
export class MatchBannerComponent {

  @Input() matches: Match[] = [];
  @Input() title: string = '';

}
