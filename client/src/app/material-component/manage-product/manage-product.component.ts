import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global.constants';
import { ProductComponent } from '../dialog/product/product.component';
import { ConfirmationComponent } from '../dialog/confirmation/confirmation.component';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.css'],
})
export class ManageProductComponent implements OnInit {
  displayedColumnsHeading: string[] = [
    'name',
    'categoryName',
    'description',
    'price',
    'edit',
  ];

  dataSource: any;

  responseMessage: any;

  constructor(
    private productSvc: ProductService,
    private router: Router,
    private snackbarSvc: SnackbarService,
    public dialog: MatDialog,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.ngxSvc.start();

    this.tableData();
  }

  tableData() {
    this.productSvc
      .getProducts()
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.dataSource = new MatTableDataSource(response);
          // console.log(this.dataSource);
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();
          // console.log(error.error?.message);

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

  applyFilter(event: Event) {
    // console.log(event);
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  handAddAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      action: 'Add',
    };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onAddProduct.subscribe(
      (response) => {
        this.tableData();
      }
    );
  }

  handleEditAction(values: any) {
    // console.log(values);
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      action: 'Edit',
      data: values,
    };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(ProductComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onEditProduct.subscribe(
      (response) => {
        this.tableData();
      }
    );
  }

  handleDeleteAction(values: any) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      message: ' delete ' + values.name,
      confirmation: true,
    };

    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);

    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe(
      (resposne) => {
        this.ngxSvc.start();
        this.deleteProduct(values.id);
        dialogRef.close();
      }
    );
  }

  deleteProduct(id: any) {
    this.productSvc
      .deleteProduct(id)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.tableData();
          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, 'success');
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

  onChange(status: any, id: any) {
    this.ngxSvc.start();

    if (status) {
      status = 'active';
    } else {
      status = 'inactive';
    }

    var data = {
      status: status,
      id: id,
    };
    // console.log(data);

    this.productSvc
      .updateProductStatus(data)
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, 'success');
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
}
