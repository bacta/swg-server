import { Directive, AfterViewInit, ElementRef } from '@angular/core';

@Directive({
  selector: '[appAutofocus]'
})
export class AutofocusDirective implements AfterViewInit {
  constructor(private element: ElementRef) { }

  ngAfterViewInit(): void {
    //A little hack here to make this work a little better.
    setTimeout(() => this.element.nativeElement.focus(), 1);
  }

}
