package com.apple.eawt.event;

public class RotationEvent extends GestureEvent {
   final double rotation;

   RotationEvent(double var1) {
      this.rotation = var1;
   }

   public double getRotation() {
      return this.rotation;
   }
}
