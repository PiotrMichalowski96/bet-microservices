import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {MatchesPageComponent} from "./pages/matches-page/matches-page.component";
import {MatchPageComponent} from "./pages/match-page/match-page.component";

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'matches',
    component: MatchesPageComponent
  },
  {
    path: 'match/:id',
    component: MatchPageComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
