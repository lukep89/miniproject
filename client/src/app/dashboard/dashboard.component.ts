import { AfterViewInit, Component } from '@angular/core';
import { DashboardService } from '../services/dashboard.service';
import { SnackbarService } from '../services/snackbar.service';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { catchError, tap } from 'rxjs';
import { GlobalConstants } from '../shared/global.constants';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements AfterViewInit {
  responseMessage: any;

  data: any;

  constructor(
    private dashboardSvc: DashboardService,
    private snackbarSvc: SnackbarService,
    private ngxSvc: NgxUiLoaderService
  ) {
    // this.ngxSvc.start();
    ngxSvc.start();
    this.dashboardData();
  }

  dashboardData() {
    this.dashboardSvc
      .getCount()
      .pipe(
        tap((response: any) => {
          console.log(response);

          this.ngxSvc.stop();
          this.data = response;
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

  // dashboardData() {
  //   this.dashboardSvc
  //     .getDetails()
  //     .then((response) => {
  //       console.log(response);

  //       this.ngxSvc.stop();
  //       this.data = response;
  //     })
  //     .catch((error) =>{
  //       this.ngxSvc.stop();
  //             console.log(error);

  //             if (error.error?.message) {
  //               this.responseMessage = error.error?.message;
  //             } else {
  //               this.responseMessage = GlobalConstants.genericError;
  //             }
  //             this.snackbarSvc.openSnckBar(
  //               this.responseMessage,
  //               GlobalConstants.error
  //             );
  //     });
  // }

  ngAfterViewInit(): void {}
}
