import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FooterComponent } from './shared/footer/footer/footer.component';
import { HeaderComponent } from './shared/header/header/header.component';
import { HomeComponent } from './pages/home/home.component';
import { SliderComponent } from './components/slider/slider.component';
import {AppRoutingModule} from "./app-routing.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { MatchBannerComponent } from './components/match-banner/match-banner.component';
import { MatchComponent } from './components/match/match.component';
import {HttpClientModule} from "@angular/common/http";
import { MatchesPageComponent } from './pages/matches-page/matches-page.component';
import {PaginatorModule} from "primeng/paginator";
import { MatchPageComponent } from './pages/match-page/match-page.component';
import {TabViewModule} from "primeng/tabview";
import { BetComponent } from './components/bet/bet.component';
import { BetsPageComponent } from './pages/bets-page/bets-page.component';

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    HeaderComponent,
    HomeComponent,
    SliderComponent,
    MatchBannerComponent,
    MatchComponent,
    MatchesPageComponent,
    MatchPageComponent,
    BetComponent,
    BetsPageComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    PaginatorModule,
    TabViewModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
