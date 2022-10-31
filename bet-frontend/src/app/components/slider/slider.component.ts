import {Component, Input, OnInit} from '@angular/core';
import {Match} from "../../model/match";
import {animate, state, style, transition, trigger} from "@angular/animations";

@Component({
  selector: 'app-slider',
  templateUrl: './slider.component.html',
  styleUrls: ['./slider.component.scss'],
  animations: [
    trigger('slideFade', [
      state('void', style({opacity: 0})),
      transition('void <=> *', [animate('1s')])
    ])
  ]
})
export class SliderComponent implements OnInit {

  @Input() items: Match[] = [];
  @Input() isBanner: boolean = false;

  currentSlideIndex: number = 0;

  constructor() {
  }

  ngOnInit(): void {
    if (this.isBanner) {
      return;
    }
    setInterval(() => {
      this.currentSlideIndex = ++this.currentSlideIndex % this.items.length;
    }, 5000)
  }
}
