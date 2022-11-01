import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetPageComponent } from './bet-page.component';

describe('BetPageComponent', () => {
  let component: BetPageComponent;
  let fixture: ComponentFixture<BetPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BetPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BetPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
