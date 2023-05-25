import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global.constants';
import { ForgotPasswordComponent } from '../forgot-password/forgot-password.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  hidePassword = true;

  loginForm!: FormGroup;

  responseMessage: any;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<LoginComponent>,
    private ngxSvc: NgxUiLoaderService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loginForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.emailRegex),
      ]),
      password: this.fb.control<string>('', [Validators.required]),
    });
  }

  handleSubmit() {
    this.ngxSvc.start();

    var formData = this.loginForm.value;
    var data = {
      email: formData.email,
      password: formData.password,
    };

    this.userSvc
      .login(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.dialogRef.close();
          localStorage.setItem('token', response?.token);

          this.router.navigate(['/cafe/dashboard']);
        }),
        catchError((error) => {
          this.ngxSvc.stop();
          if (error.error?.message) {
            this.responseMessage = error.error?.message;
          } else {
            this.responseMessage = GlobalConstants.genericError;
          }
          this.snackbarSvc.openSnckBar(
            this.responseMessage,
            GlobalConstants.error
          );
          return error;
        })
      )
      .subscribe();
  }

  forgotPasswordAction() {
    this.dialog.closeAll();
    const dialogConfig = new MatDialogConfig();

    dialogConfig.width = '550px';
    this.dialog.open(ForgotPasswordComponent, dialogConfig);
  }
}
