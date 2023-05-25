import { Component, EventEmitter, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { catchError, tap } from 'rxjs';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css'],
})
export class ProductComponent implements OnInit {
  onAddProduct = new EventEmitter();
  onEditProduct = new EventEmitter();

  productForm!: FormGroup;
  formattedPrice: any;

  dialogAction: any = 'Add';
  action: any = 'Add';

  responseMessage: any;

  categoryArr: any = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private fb: FormBuilder,
    private productSvc: ProductService,
    private snackbarSvc: SnackbarService,
    public dialogRef: MatDialogRef<ProductComponent>,
    private categorySvc: CategoryService
  ) {}

  ngOnInit(): void {
    this.productForm = this.createForm();

    if (this.dialogData.action === 'Edit') {
      console.log(this.dialogData);

      this.dialogAction = 'Edit';
      this.action = 'Update';
      this.productForm.patchValue(this.dialogData.data);
    }

    this.getCategoryList();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: this.fb.control<string>('', [
        Validators.required,
        Validators.pattern(GlobalConstants.nameRegex),
      ]),
      description: this.fb.control<string>('', [Validators.required]),
      price: this.fb.control<string>('', [Validators.required]),
      categoryId: this.fb.control<string>('', [Validators.required]),
    });
  }

  getCategoryList() {
    this.categorySvc
      .getCategoryList()
      .pipe(
        tap((response: any) => {
          this.categoryArr = response;
        }),
        catchError((error: any) => {
          console.log(error);

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

  handleSubmit() {
    if (this.dialogAction === 'Edit') {
      this.edit();
    } else {
      this.add();
    }
  }

  add() {
    var formData = this.productForm.value;
    var data = {
      name: formData.name,
      description: formData.description,
      price: formData.price,
      categoryId: formData.categoryId,
    };
    console.log(data);

    this.productSvc
      .addProduct(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.dialogRef.close();

          this.onAddProduct.emit();

          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, '');
        }),
        catchError((error: any) => {
          console.log(error);

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
    var formData = this.productForm.value;
    var data = {
      id: this.dialogData.data.id,
      name: formData.name,
      description: formData.description,
      price: formData.price,
      categoryId: formData.categoryId,
    };
    console.log(data);

    this.productSvc
      .updateProduct(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.dialogRef.close();

          this.onEditProduct.emit();

          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, '');
        }),
        catchError((error: any) => {
          console.log(error);

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
