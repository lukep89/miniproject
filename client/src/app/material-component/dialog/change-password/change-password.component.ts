import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
})
export class ChangePasswordComponent implements OnInit {
  hideOldPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  changePasswordForm!: FormGroup;

  responseMessage: any;
  oldPassword: any;

  constructor(
    private fb: FormBuilder,
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<ChangePasswordComponent>,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.changePasswordForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      oldPassword: this.fb.control<string>('', [Validators.required]),
      newPassword: this.fb.control<string>('', [Validators.required]),
      confirmPassword: this.fb.control<string>('', [Validators.required]),
    });
  }

  validateSubmit() {
    if (
      this.changePasswordForm.controls['newPassword'].value !=
      this.changePasswordForm.controls['confirmPassword'].value
    ) {
      return true;
    } else {
      return false;
    }
  }

  handlePasswordChangeSubmit() {
    this.ngxSvc.start();

    var formData = this.changePasswordForm.value;
    var data = {
      oldPassword: formData.oldPassword,
      newPassword: formData.newPassword,
      confirmPassword: formData.confirmPassword,
    };

    // console.log(data);

    this.userSvc
      .changePassword(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.responseMessage = response?.message;
          this.dialogRef.close();
          this.snackbarSvc.openSnckBar(this.responseMessage, '');
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
