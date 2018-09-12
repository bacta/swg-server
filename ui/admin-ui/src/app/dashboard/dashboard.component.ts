import { Component, AfterViewInit } from '@angular/core';
import {Chart} from 'chart.js';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit {

  canvas: any;

  constructor() { }

  ngAfterViewInit() {
    this.canvas = document.getElementById('loginHistogramChart');
    let ctx = this.canvas.getContext('2d');
    let myChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [
          '6/20', '6/21', '6/22', '6/23', '6/24', '6/25', '6/26', '6/27', '6/28', '6/29', '6/30'
        ],
        datasets: [{
          label: 'Logins (Last 10 Days)',
          data: [132, 198, 212, 234, 265, 243, 321, 287, 346, 389],
          borderColor: 'rgba(0, 0, 0, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: false,
      }
    });
  }

}
