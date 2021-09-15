package java.awt.event;

import java.awt.AWTEvent;
import java.util.EventListenerProxy;

public class AWTEventListenerProxy extends EventListenerProxy<AWTEventListener> implements AWTEventListener {
   private final long eventMask;

   public AWTEventListenerProxy(long var1, AWTEventListener var3) {
      super(var3);
      this.eventMask = var1;
   }

   public void eventDispatched(AWTEvent var1) {
      ((AWTEventListener)this.getListener()).eventDispatched(var1);
   }

   public long getEventMask() {
      return this.eventMask;
   }
}
