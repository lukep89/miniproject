import { Injectable } from '@angular/core';

export interface Menu {
  state: string;
  name: string;
  type: string;
  icon: string;
  role: string;
}

// this menu list will appear on the sidebar
const MENUITEMS = [
  {
    state: 'dashboard',
    name: 'Dashboard',
    type: 'link',
    icon: 'dashboard',
    role: '', // all role/person can access
  },
  {
    state: 'category',
    name: 'Manage Category',
    type: 'link',
    icon: 'category',
    role: 'admin', // only admin role can access
  },
  {
    state: 'product',
    name: 'Manage Product',
    type: 'link',
    icon: 'inventory_2',
    role: 'admin', // only admin role can access
  },
  {
    state: 'order',
    name: 'Manage Order',
    type: 'link',
    icon: 'shopping_cart',
    role: '', // all role can access
  },
  {
    state: 'bill',
    name: 'View Bill',
    type: 'link',
    icon: 'receipt_long',
    role: '', // all role can access
  },
  {
    state: 'user',
    name: 'Manage User',
    type: 'link',
    icon: 'people',
    role: 'admin', // admin can access only
  },
];

@Injectable()
export class MenuItems {
  getMenuItem(): Menu[] {
    return MENUITEMS;
  }
}
