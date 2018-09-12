import { Component, OnInit, Input } from '@angular/core';
import { BactaAccount } from '../models/bacta-account';

@Component({
  selector: 'app-bacta-account',
  templateUrl: './bacta-account.component.html',
  styleUrls: ['./bacta-account.component.scss']
})
export class BactaAccountComponent implements OnInit {
  
  @Input()
  account: BactaAccount;

  constructor() { }

  ngOnInit() {
  }
}
