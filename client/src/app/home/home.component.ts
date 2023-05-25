import { Component, OnInit } from '@angular/core';
import {
  MatDialog,
  MatDialogConfig,
} from '@angular/material/dialog';
import { SignupComponent } from '../home/signup/signup.component';

import { LoginComponent } from '../home/login/login.component';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { catchError, tap } from 'rxjs';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  token: any;

  constructor(
    private dialog: MatDialog,
    private userSvc: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // when at home page and valid token is availble route to cafe/dashboard
    this.userSvc
      .checkToken()
      .pipe(
        tap((response: any) => {
          this.router.navigate(['/cafe/dashboard']);
        }),
        catchError((error) => {
          console.log(error);
          return error;
        })
      )
      .subscribe();

  }

  signupAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.width = '550px';
    this.dialog.open(SignupComponent, dialogConfig);
  }

  loginAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.width = '550px';
    this.dialog.open(LoginComponent, dialogConfig);
  }

  greetings() {
    const currentTime = new Date();
    const currentHour = currentTime.getHours();
    let greeting;

    switch (true) {
      case currentHour < 12:
        greeting = 'Good morning';
        break;
      case currentHour < 18:
        greeting = 'Good afternoon';
        break;
      default:
        greeting = 'Good evening';
        break;
    }
    return greeting;
  }


}
