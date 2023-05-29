import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-view-bill-details',
  templateUrl: './view-bill-details.component.html',
  styleUrls: ['./view-bill-details.component.css'],
})
export class ViewBillDetailsComponent implements OnInit {
  displayedColumnsHeading: string[] = [
    'name',
    'category',
    'price',
    'quantity',
    'total',
  ];

  dataSource: any;

  data: any;

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    public dialogRef: MatDialogRef<ViewBillDetailsComponent>
  ) {}

  ngOnInit(): void {
    this.data = this.dialogData.data;
    // console.log(this.data);

    this.dataSource = JSON.parse(this.dialogData.data.productDetails);
  }
}
