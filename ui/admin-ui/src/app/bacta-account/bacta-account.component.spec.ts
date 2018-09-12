import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BactaAccountComponent } from './bacta-account.component';

describe('BactaAccountComponent', () => {
  let component: BactaAccountComponent;
  let fixture: ComponentFixture<BactaAccountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BactaAccountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BactaAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
