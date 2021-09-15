package com.sun.org.apache.xerces.internal.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

public class EventImpl implements Event {
   public String type = null;
   public EventTarget target;
   public EventTarget currentTarget;
   public short eventPhase;
   public boolean initialized = false;
   public boolean bubbles = true;
   public boolean cancelable = false;
   public boolean stopPropagation = false;
   public boolean preventDefault = false;
   protected long timeStamp = System.currentTimeMillis();

   public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
      this.type = eventTypeArg;
      this.bubbles = canBubbleArg;
      this.cancelable = cancelableArg;
      this.initialized = true;
   }

   public boolean getBubbles() {
      return this.bubbles;
   }

   public boolean getCancelable() {
      return this.cancelable;
   }

   public EventTarget getCurrentTarget() {
      return this.currentTarget;
   }

   public short getEventPhase() {
      return this.eventPhase;
   }

   public EventTarget getTarget() {
      return this.target;
   }

   public String getType() {
      return this.type;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void stopPropagation() {
      this.stopPropagation = true;
   }

   public void preventDefault() {
      this.preventDefault = true;
   }
}
