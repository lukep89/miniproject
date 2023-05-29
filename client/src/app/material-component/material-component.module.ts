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
import { ViewBillDetailsComponent } from './dialog/view-bill-details/view-bill-details.component';
import { ChangePasswordComponent } from './dialog/change-password/change-password.component';
import { ManageCategoryComponent } from './manage-category/manage-category.component';
import { CategoryComponent } from './dialog/category/category.component';
import { ManageProductComponent } from './manage-product/manage-product.component';
import { ProductComponent } from './dialog/product/product.component';
import { ManageOrderComponent } from './manage-order/manage-order.component';
import { ViewBillComponent } from './view-bill/view-bill.component';
import { ManageUserComponent } from './manage-user/manage-user.component';


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
    ViewBillDetailsComponent,
    ChangePasswordComponent,
    ManageCategoryComponent,
    CategoryComponent,
    ManageProductComponent,
    ProductComponent,
    ManageOrderComponent,
    ViewBillComponent,
    ManageUserComponent,
  ],
})
export class MaterialComponentsModule {}
