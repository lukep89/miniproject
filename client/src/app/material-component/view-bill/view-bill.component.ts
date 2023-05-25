import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { BillService } from 'src/app/services/bill.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global.constants';
import { ViewBillDetailsComponent } from '../dialog/view-bill-details/view-bill-details.component';
import { ConfirmationComponent } from '../dialog/confirmation/confirmation.component';
import * as saveAs from 'file-saver';

@Component({
  selector: 'app-view-bill',
  templateUrl: './view-bill.component.html',
  styleUrls: ['./view-bill.component.css'],
})
export class ViewBillComponent implements OnInit {
  displayedColumnsHeading: string[] = [
    'uuid',
    // 'name',
    'email',
    // 'contactNumber',
    'paymentMethod',
    'totalAmount',
    'view',
  ];

  dataSource: any;

  responseMessage: any;

  constructor(
    private billSvc: BillService,
    private ngxSvc: NgxUiLoaderService,
    private snackbarSvc: SnackbarService,
    private dialog: MatDialog,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.ngxSvc.start();

    this.tableData();
  }

  tableData() {
    this.billSvc
      .getBills()
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.dataSource = new MatTableDataSource(response);
          console.log(this.dataSource);
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();
          console.log(error.error?.message);

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

  handleViewAction(values: any) {
    // console.log(values);
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      data: values,
    };
    dialogConfig.width = '100%';

    const dialogRef = this.dialog.open(ViewBillDetailsComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });
  }

  handleDeleteAction(values: any) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      message: ' delete ' + values.uuid + ' bill',
      confirmation: true,
    };

    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);

    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe(
      (resposne) => {
        this.ngxSvc.start();
        this.deleteBill(values.id);
        dialogRef.close();
      }
    );
  }

  deleteBill(id: any) {
    this.billSvc
      .deleteBill(id)
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.tableData();
          this.responseMessage = response?.message;
          this.snackbarSvc.openSnckBar(this.responseMessage, 'success');
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();
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

  handleDownloadAction(values: any) {
    this.ngxSvc.start();

    var data = {
      name: values.name,
      email: values.email,
      uuid: values.uuid,
      contactNumber: values.contactNumber,
      paymentMethod: values.paymentMethod,
      totalAmount: values.totalAmount.toString(),
      productDetails: values.productDetails,
    };

    this.downloadFile(values.uuid, data);
  }

  downloadFile(filename: string, data: any) {
    this.billSvc
      .getPdf(data)
      .pipe(
        tap((response: any) => {
          console.log(response);

          saveAs(response, filename + '.pdf');
          this.ngxSvc.stop();
        }),
        catchError((error: any) => {
          this.ngxSvc.stop();

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
