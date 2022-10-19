import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./pages/home/home.component";
import {MatchesPageComponent} from "./pages/matches-page/matches-page.component";

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
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
