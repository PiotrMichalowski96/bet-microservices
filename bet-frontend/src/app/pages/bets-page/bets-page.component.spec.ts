import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BetsPageComponent } from './bets-page.component';

describe('BetsPageComponent', () => {
  let component: BetsPageComponent;
  let fixture: ComponentFixture<BetsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BetsPageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BetsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
