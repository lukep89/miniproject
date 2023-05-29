import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global.constants';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.css'],
})
export class ManageUserComponent implements OnInit {
  displayedColumnsHeading: string[] = [
    'name',
    'email',
    'contactNumber',
    'status',
  ];

  dataSource: any;

  responseMessage: any;

  constructor(
    private userSvc: UserService,
    private snackbarSvc: SnackbarService,
    private ngxSvc: NgxUiLoaderService
  ) {}

  ngOnInit(): void {
    this.ngxSvc.start();

    this.tableData();
  }

  tableData() {
    this.userSvc
      .getUsers()
      .pipe(
        tap((response: any) => {
          // console.log(response);

          this.ngxSvc.stop();
          this.dataSource = new MatTableDataSource(response);
          // console.log(this.dataSource);
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

  applyFilter(event: Event) {
    // console.log(event);
    const filterValue = (event.target as HTMLInputElement).value;

    this.dataSource.filter = filterValue.trim().toLowerCase();
  }


  onChange(status: any, email: any) {
    this.ngxSvc.start();

    if (status) {
      status = 'active';
    } else {
      status = 'inactive';
    }

    var data = {
      status: status,
      // id: id,
      email: email,
    };
    // console.log(data);

    this.userSvc
      .updateUser(data)
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
