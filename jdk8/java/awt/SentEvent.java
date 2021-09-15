package java.awt;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SentEvent extends AWTEvent implements ActiveEvent {
   private static final long serialVersionUID = -383615247028828931L;
   static final int ID = 1007;
   boolean dispatched;
   private AWTEvent nested;
   private AppContext toNotify;

   SentEvent() {
      this((AWTEvent)null);
   }

   SentEvent(AWTEvent var1) {
      this(var1, (AppContext)null);
   }

   SentEvent(AWTEvent var1, AppContext var2) {
      super(var1 != null ? var1.getSource() : Toolkit.getDefaultToolkit(), 1007);
      this.nested = var1;
      this.toNotify = var2;
   }

   public void dispatch() {
      boolean var9 = false;

      try {
         var9 = true;
         if (this.nested != null) {
            Toolkit.getEventQueue().dispatchEvent(this.nested);
            var9 = false;
         } else {
            var9 = false;
         }
      } finally {
         if (var9) {
            this.dispatched = true;
            if (this.toNotify != null) {
               SunToolkit.postEvent(this.toNotify, new SentEvent());
            }

            synchronized(this) {
               this.notifyAll();
            }
         }
      }

      this.dispatched = true;
      if (this.toNotify != null) {
         SunToolkit.postEvent(this.toNotify, new SentEvent());
      }

      synchronized(this) {
         this.notifyAll();
      }
   }

   final void dispose() {
      this.dispatched = true;
      if (this.toNotify != null) {
         SunToolkit.postEvent(this.toNotify, new SentEvent());
      }

      synchronized(this) {
         this.notifyAll();
      }
   }
}
