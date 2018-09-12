import { Component, OnInit } from '@angular/core';
import { Galaxy } from '../models/galaxy';
import { GalaxyStatus } from '../models/galaxy-status';
import { GalaxyService } from '../services/galaxy.service';

@Component({
  selector: 'app-galaxies',
  templateUrl: './galaxies.component.html',
  styleUrls: ['./galaxies.component.scss']
})
export class GalaxiesComponent implements OnInit {

  GalaxyStatus = GalaxyStatus;

  registeringGalaxy = false;
  submitting = false;

  galaxies: Galaxy[] = [];

  constructor(private galaxyService: GalaxyService) { }

  ngOnInit() {
    this.getGalaxies();
  }

  getGalaxies(): void {
    this.galaxyService.all()
      .subscribe(galaxies => this.galaxies = galaxies);
  }

  onGalaxyRegistered(galaxy: Galaxy) {
    this.galaxies.push(galaxy);
  }

  beginRegisteringGalaxy(): void {
    this.registeringGalaxy = true;

    setTimeout(() => document.getElementById('galaxyNameInput').focus(), 1);
  }

  onSubmit(form) {

    this.submitting = true;

    let galaxy: Galaxy = {
      id: -1,
      name: form['galaxy-name'].value,
      address: form['galaxy-address'].value,
      port: form['galaxy-port'].value,
      timeZone: -6,
      created: 0,
      onlineCount: 0,
      status: GalaxyStatus.offline
    }

    this.galaxyService.register(galaxy)
      .subscribe(
        res => {
          this.galaxies.push(res)
          this.registeringGalaxy = false;
        },
        err => {

        },
        () => {
          this.submitting = false;
        })
  }

  cancelRegisteringGalaxy() {
    this.registeringGalaxy = false;
  }

  deleteGalaxy(id) {
    this.galaxyService.delete(id)
      .subscribe(
        res => {
          this.galaxies = this.galaxies.filter(galaxy => galaxy.id !== id)
        },
        err => {

        },
        () => {
          this.submitting = false
        }
      )
  }

}
