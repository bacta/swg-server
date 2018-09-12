import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BactaAccountsComponent } from './bacta-accounts.component';

describe('BactaAccountsComponent', () => {
  let component: BactaAccountsComponent;
  let fixture: ComponentFixture<BactaAccountsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BactaAccountsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BactaAccountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
