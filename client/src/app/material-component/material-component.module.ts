import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MaterialModule } from '../shared/material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { CdkTableModule } from '@angular/cdk/table';
import { MaterialComponentRoutes } from './material-component.routing';
import { ConfirmationComponent } from './dialog/confirmation/confirmation.component';
import { ViewBillProductsComponent } from './dialog/view-bill-products/view-bill-products.component';
import { ChangePasswordComponent } from './dialog/change-password/change-password.component';
import { ManageCategoryComponent } from './manage-category/manage-category.component';
import { CategoryComponent } from './dialog/category/category.component';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    FlexLayoutModule,
    RouterModule.forChild(MaterialComponentRoutes),
    MaterialModule,
    HttpClientModule,
    ReactiveFormsModule,
    FlexLayoutModule,
    CdkTableModule,
  ],
  declarations: [
    ConfirmationComponent,
    ViewBillProductsComponent,
    ChangePasswordComponent,
    ManageCategoryComponent,
    CategoryComponent
  ],
})
export class MaterialComponentsModule {}
