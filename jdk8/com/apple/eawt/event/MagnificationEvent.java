package com.apple.eawt.event;

public class MagnificationEvent extends GestureEvent {
   final double magnification;

   MagnificationEvent(double var1) {
      this.magnification = var1;
   }

   public double getMagnification() {
      return this.magnification;
   }
}
