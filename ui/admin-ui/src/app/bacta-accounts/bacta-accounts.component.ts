import { Component, OnInit } from '@angular/core';
import { BactaAccountService } from '../services/bacta-account.service';
import { BactaAccount } from '../models/bacta-account';

@Component({
  selector: 'app-bacta-accounts',
  templateUrl: './bacta-accounts.component.html',
  styleUrls: ['./bacta-accounts.component.scss']
})
export class BactaAccountsComponent implements OnInit {

  accounts: BactaAccount[];
  selectedAccount: BactaAccount;

  constructor(private accountsService: BactaAccountService) { }

  ngOnInit() {
    this.getAccounts();
  }

  getAccounts(): void {
    this.accountsService.all()
      .subscribe(accounts => this.accounts = accounts);
  }

  onSelect(account: BactaAccount): void {
    this.selectedAccount = account;
  }
}
