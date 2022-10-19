import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchBannerComponent } from './match-banner.component';

describe('MatchBannerComponent', () => {
  let component: MatchBannerComponent;
  let fixture: ComponentFixture<MatchBannerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MatchBannerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MatchBannerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
