import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';

import { ActivatedRoute, Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { ConfirmationComponent } from 'src/app/material-component/dialog/confirmation/confirmation.component';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  token: any;

  hideNewPassword = true;
  hideConfirmPassword = true;

  resetPasswordForm!: FormGroup;

  responseMessage: any;
  oldPassword: any;

  constructor(
    private activatedRoute: ActivatedRoute,
    private fb: FormBuilder,
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    private ngxSvc: NgxUiLoaderService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.ngxSvc.start();
    this.activatedRoute.queryParams.subscribe((params) => {
      this.token = params['token'];
    });

    this.resetPasswordForm = this.createForm();
    this.ngxSvc.stop();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      newPassword: this.fb.control<string>('', [Validators.required]),
      confirmPassword: this.fb.control<string>('', [Validators.required]),
    });
  }

  validateSubmit() {
    if (
      this.resetPasswordForm.controls['newPassword'].value !=
      this.resetPasswordForm.controls['confirmPassword'].value
    ) {
      return true;
    } else {
      return false;
    }
  }

  handlePasswordChangeSubmit() {
    var formData = this.resetPasswordForm.value;
    var data = {
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmPassword,
    };
    // console.log(data);

    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      message: ' reset password',
      confirmation: true,
    };

    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);

    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe(
      (resposne) => {
        this.ngxSvc.start();
        this.resetPassword(data, this.token);
        dialogRef.close();
      }
    );
  }

  resetPassword(data: any, token: string) {
    this.userSvc
      .resetPassword(data, this.token)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.responseMessage = response?.message;

          this.snackbarSvc.openSnckBar(this.responseMessage, '');
          this.router.navigate(['/']);
        }),
        catchError((error) => {
          console.log(error);

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
