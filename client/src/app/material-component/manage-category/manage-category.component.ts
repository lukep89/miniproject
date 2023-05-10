import { Component, OnInit } from '@angular/core';
import { CategoryService } from 'src/app/services/category.service';

import { Router } from '@angular/router';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { catchError, tap } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { GlobalConstants } from 'src/app/shared/global.constants';
import { CategoryComponent } from '../dialog/category/category.component';

@Component({
  selector: 'app-manage-category',
  templateUrl: './manage-category.component.html',
  styleUrls: ['./manage-category.component.css'],
})
export class ManageCategoryComponent implements OnInit {
  displayedColumnsHeading: string[] = ['name', 'edit'];

  dataSource: any;

  responseMessage: any;

  constructor(
    private categorySvc: CategoryService,
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
    this.categorySvc
      .getCategory()
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.dataSource = new MatTableDataSource(response);
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

  handAddAction() {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      action: 'Add',
    };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(CategoryComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onAddCategory.subscribe(
      (response) => {
        this.tableData();
      }
    );
  }

  handleEditAction(values: any) {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.data = {
      action: 'Edit',
      data: values,
    };
    dialogConfig.width = '850px';

    const dialogRef = this.dialog.open(CategoryComponent, dialogConfig);

    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onEditCategory.subscribe(
      (response) => {
        this.tableData();
      }
    );
  }
}
