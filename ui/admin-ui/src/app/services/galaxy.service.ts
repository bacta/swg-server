import { Injectable } from '@angular/core';
import { Galaxy } from '../models/galaxy';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GalaxyService {

  constructor(public http: HttpClient) { }

  all(): Observable<Galaxy[]> {
    return this.http.get<Galaxy[]>("http://localhost:8080/api/galaxies");
  }

  register(galaxy: Galaxy): Observable<Galaxy> {
    return this.http.post<Galaxy>("http://localhost:8080/api/galaxies", galaxy);
  }

  delete(id): Observable<any> {
    return this.http.delete(`http://localhost:8080/api/galaxies/${id}`)
  }
}
