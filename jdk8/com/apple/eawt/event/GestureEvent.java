package com.apple.eawt.event;

public abstract class GestureEvent {
   boolean consumed;

   GestureEvent() {
   }

   public void consume() {
      this.consumed = true;
   }

   protected boolean isConsumed() {
      return this.consumed;
   }
}
