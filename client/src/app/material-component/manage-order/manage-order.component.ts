import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { saveAs } from 'file-saver';

import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, pipe, tap } from 'rxjs';
import { BillService } from 'src/app/services/bill.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-manage-order',
  templateUrl: './manage-order.component.html',
  styleUrls: ['./manage-order.component.css'],
})
export class ManageOrderComponent implements OnInit {
  displayedColumnsHeading: string[] = [
    'name',
    'category',
    'price',
    'quantity',
    'total',
    'edit',
  ];

  dataSource: any = [];

  manageOrderForm!: FormGroup;

  categorysArr: any = [];
  productsArr: any = [];
  price: any;
  totalAmount: number = 0;

  responseMessage: any;

  constructor(
    private fb: FormBuilder,
    private categorySvc: CategoryService,
    private productSvc: ProductService,
    private billSvc: BillService,
    private snackbarSvc: SnackbarService,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.ngxSvc.start();

    this.getFilteredCategoryList();

    this.manageOrderForm = this.createForm();
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
      paymentMethod: this.fb.control<string>('', [Validators.required]),
      category: this.fb.control<string>('', [Validators.required]),
      product: this.fb.control<string>('', [Validators.required]),
      price: this.fb.control<string>('', [Validators.required]),
      quantity: this.fb.control<string>('', [Validators.required]),
      total: this.fb.control<number>(0, [Validators.required]),
    });
  }

  // get list of category , filtered list for role: User
  getFilteredCategoryList() {
    this.categorySvc
      .getFilteredCategoryList()
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.categorysArr = response;
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();

          // console.log(error);

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

  // get product list by category
  getProductListByCategory(value: any) {
    // console.log(value);

    this.productSvc
      .getProductListByCategory(value.id)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.productsArr = response;

          this.manageOrderForm.controls['price'].setValue('');
          this.manageOrderForm.controls['quantity'].setValue(0);
          this.manageOrderForm.controls['total'].setValue(0);
        }),
        catchError((error: any) => {
          // console.log(error);

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

  // get a product details
  getProductDetails(value: any) {
    // console.log(value);

    this.productSvc
      .getProductById(value.id)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.price = response.price;

          this.manageOrderForm.controls['price'].setValue(response.price);
          this.manageOrderForm.controls['quantity'].setValue('1');
          this.manageOrderForm.controls['total'].setValue(this.price * 1);
        }),
        catchError((error: any) => {
          // console.log(error);

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

  setQuantity(value: any) {
    var temp = this.manageOrderForm.controls['quantity'].value;

    if (temp > 0) {
      this.manageOrderForm.controls['total'].setValue(
        this.manageOrderForm.controls['quantity'].value *
          this.manageOrderForm.controls['price'].value
      );
    } else if (temp != '') {
      this.manageOrderForm.controls['quantity'].setValue('1');

      this.manageOrderForm.controls['total'].setValue(
        this.manageOrderForm.controls['quantity'].value *
          this.manageOrderForm.controls['price'].value
      );
    }
  }

  validateAddProduct() {
    if (
      this.manageOrderForm.controls['total'].value === 0 ||
      this.manageOrderForm.controls['quantity'].value === null ||
      this.manageOrderForm.controls['quantity'].value <= 0
    ) {
      return true;
    } else {
      return false;
    }
  }

  validateSubmit() {
    if (
      this.totalAmount == 0 ||
      this.manageOrderForm.controls['name'].value.trim() === '' ||
      this.manageOrderForm.controls['email'].value.trim() === '' ||
      this.manageOrderForm.controls['contactNumber'].value.trim() === '' ||
      this.manageOrderForm.controls['paymentMethod'].value.trim() === ''
    ) {
      return true;
    } else {
      return false;
    }
  }

  add() {
    var formData = this.manageOrderForm.value;

    var existingProduct = this.dataSource.find(
      (element: { id: number }) => element.id === formData.product.id
    );

    if (existingProduct) {
      // Product with the same ID already exists, update the quantity
      this.totalAmount -= Number(existingProduct.total);
      existingProduct.quantity = formData.quantity;
      existingProduct.total = (formData.quantity * formData.price).toFixed(2);
      this.totalAmount += Number(existingProduct.total);
    } else {
      // Product does not exist, add it to the data source
      this.dataSource.push({
        id: formData.product.id,
        name: formData.product.name,
        category: formData.category.name,
        quantity: formData.quantity,
        price: formData.price.toFixed(2),
        total: (formData.quantity * formData.price).toFixed(2),
      });

      this.totalAmount += formData.quantity * formData.price;
    }

    this.totalAmount = Number(this.totalAmount.toFixed(2));

    this.dataSource = [...this.dataSource];

    this.snackbarSvc.openSnckBar(
      GlobalConstants.productAddedMessage,
      'success'
    );
  }

  handleDeleteAction(value: any, element: any) {
    this.totalAmount -= Number(element.total);

    this.dataSource.splice(value, 1);
    this.dataSource = [...this.dataSource];
  }

  handleEditProduct(row: any) {
    this.manageOrderForm.patchValue({
      product: {
        id: row.id,
        name: row.name,
      },
      category: {
        name: row.category,
      },
      quantity: row.quantity,
      price: row.price.toString(),
      total: (row.quantity * row.price).toFixed(2),
    });
  }

  submitAction() {
    var formData = this.manageOrderForm.value;

    var data = {
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      paymentMethod: formData.paymentMethod,
      totalAmount: this.totalAmount.toFixed(2),
      productDetails: JSON.stringify(this.dataSource),
    };

    // console.log(data);

    this.ngxSvc.start();
    this.billSvc
      .generateReport(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.downloadFile(response?.uuid);

          this.manageOrderForm.reset();
          this.dataSource = [];
          this.totalAmount = 0;
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();

          // console.log(error);

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

  downloadFile(filename: string) {
    var data = {
      uuid: filename,
    };

    this.billSvc
      .getPdf(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          saveAs(response, filename + '.pdf');
          this.ngxSvc.stop();
        })
      )
      .subscribe();
  }
}
