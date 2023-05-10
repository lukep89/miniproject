import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { SignupComponent } from '../components/signup/signup.component';
import { ForgotPasswordComponent } from '../components/forgot-password/forgot-password.component';
import { LoginComponent } from '../components/login/login.component';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { catchError, tap } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  constructor(
    private dialog: MatDialog,
    private userSvc: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // when at home pag and valid token is availble route to cafe/dashboard
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

  forgotPasswordAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.width = '550px';
    this.dialog.open(ForgotPasswordComponent, dialogConfig);
  }

  loginAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.width = '550px';
    this.dialog.open(LoginComponent, dialogConfig);
  }
}
