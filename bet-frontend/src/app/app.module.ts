import {APP_INITIALIZER, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {FooterComponent} from './shared/footer/footer/footer.component';
import {HeaderComponent} from './shared/header/header/header.component';
import {HomeComponent} from './pages/home/home.component';
import {SliderComponent} from './components/slider/slider.component';
import {AppRoutingModule} from "./app-routing.module";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatchBannerComponent} from './components/match-banner/match-banner.component';
import {MatchComponent} from './components/match/match.component';
import {HttpClientModule, HTTP_INTERCEPTORS, HttpClient} from "@angular/common/http";
import {MatchesPageComponent} from './pages/matches-page/matches-page.component';
import {PaginatorModule} from "primeng/paginator";
import {MatchPageComponent} from './pages/match-page/match-page.component';
import {TabViewModule} from "primeng/tabview";
import {BetComponent} from './components/bet/bet.component';
import {BetsPageComponent} from './pages/bets-page/bets-page.component';
import {CarouselModule} from "primeng/carousel";
import {BetPageComponent} from './pages/bet-page/bet-page.component';
import {ReactiveFormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {UsersPageComponent} from './pages/users-page/users-page.component';
import {TableModule} from "primeng/table";
import {AuthInterceptor} from "./interceptors/auth-interceptor";
import {AppConfig} from "./config/app.config";

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
    BetsPageComponent,
    BetPageComponent,
    UsersPageComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    PaginatorModule,
    TabViewModule,
    CarouselModule,
    ReactiveFormsModule,
    ButtonModule,
    TableModule
  ],
  providers: [
    AppConfig,
    {provide: APP_INITIALIZER, useFactory: initConfig, deps: [AppConfig], multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor,  multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

export function initConfig(config: AppConfig) {
  return () => config.load();
}
