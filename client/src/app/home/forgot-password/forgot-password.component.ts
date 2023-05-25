import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent implements OnInit {
  forgotPasswordForm!: FormGroup;

  responseMessage: any;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<ForgotPasswordComponent>,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.forgotPasswordForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.emailRegex),
      ]),
    });
  }

  handleSubmit() {
    this.ngxSvc.start();

    var formData = this.forgotPasswordForm.value;
    var data = {
      email: formData.email,
    };

    this.userSvc
      .forgotPassword(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.dialogRef.close();
          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, '');
          this.router.navigate(['/']);
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
}
