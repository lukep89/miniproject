import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class SnackbarService {
  // for notification message

  constructor(private snackBar: MatSnackBar) {}

  openSnckBar(message: string, action: String) {
    if (action === 'error') {
      this.snackBar.open(message, '', {
        horizontalPosition: 'center',
        verticalPosition: 'top',
        duration: 3500,
        panelClass: ['red-snackbar'],
      });
    } else {
      this.snackBar.open(message, '', {
        horizontalPosition: 'center',
        verticalPosition: 'top',
        duration: 3500,
        panelClass: ['green-snackbar'],
      });
    }
  }
}
