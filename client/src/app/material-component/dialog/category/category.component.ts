import { Component, EventEmitter, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { catchError, tap } from 'rxjs';
import { CategoryService } from 'src/app/services/category.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-category',
  templateUrl: './category.component.html',
  styleUrls: ['./category.component.css'],
})
export class CategoryComponent implements OnInit {
  onAddCategory = new EventEmitter();
  onEditCategory = new EventEmitter();

  categoryForm!: FormGroup;

  dialogAction: any = 'Add';
  action: any = 'Add';

  responseMessage: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private fb: FormBuilder,
    private categorySvc: CategoryService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<CategoryComponent>
  ) {}

  ngOnInit(): void {
    this.categoryForm = this.createForm();

    if (this.dialogData.action === 'Edit') {
      // console.log(this.dialogData);

      this.dialogAction = 'Edit';
      this.action = 'Update';
      this.categoryForm.patchValue(this.dialogData.data);
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control<string>('', [Validators.required]),
    });
  }

  handleSubmit() {
    if (this.dialogAction === 'Edit') {
      this.edit();
    } else {
      this.add();
    }
  }

  add() {
    var formData = this.categoryForm.value;
    var data = {
      name: formData.name,
    };

    // console.log(data);

    this.categorySvc
      .addCategory(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.dialogRef.close();

          this.onAddCategory.emit(); // used to refresh the table after receiving the response from backend

          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, 'success');
        }),
        catchError((error) => {
          // console.log(error);

          this.dialogRef.close();
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

  edit() {
    var formData = this.categoryForm.value;
    var data = {
      id: this.dialogData.data.id,
      name: formData.name,
    };

    // console.log(data);

    this.categorySvc
      .updateCategory(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.dialogRef.close();

          this.onEditCategory.emit();

          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, '');
        }),
        catchError((error) => {
          // console.log(error);

          this.dialogRef.close();
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
