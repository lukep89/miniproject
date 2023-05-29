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
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent implements OnInit {
  hidePassword = true;
  hideConfirmPassword = true;

  signupForm!: FormGroup;

  responseMessage: any;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<SignupComponent>,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.signupForm = this.createForm();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.nameRegex),
      ]),
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.emailRegex),
      ]),
      contactNumber: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.contactNumberRegex),
      ]),
      password: this.fb.control<string>('', [Validators.required]),
      confirmPassword: this.fb.control<string>('', [Validators.required]),
    });
  }

  validateSubmit() {
    if (
      this.signupForm.controls['password'].value !=
      this.signupForm.controls['confirmPassword'].value
    ) {
      return true;
    } else {
      return false;
    }
  }

  handleSubmit() {
    this.ngxSvc.start();

    var formData = this.signupForm.value;
    var data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      password: formData.password,
    };

    // console.log(data);

    // using observable
    this.userSvc
      .signup(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

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
