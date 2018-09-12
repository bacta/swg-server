import { Injectable } from '@angular/core';
import { BactaAccount } from '../models/bacta-account';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BactaAccountService {

  constructor(private http: HttpClient) { }

  all(): Observable<BactaAccount[]> {
    return this.http.get<BactaAccount[]>('http://localhost:8080/api/accounts');
  }
}
