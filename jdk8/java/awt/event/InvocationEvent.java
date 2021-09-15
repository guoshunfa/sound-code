package java.awt.event;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import sun.awt.AWTAccessor;

public class InvocationEvent extends AWTEvent implements ActiveEvent {
   public static final int INVOCATION_FIRST = 1200;
   public static final int INVOCATION_DEFAULT = 1200;
   public static final int INVOCATION_LAST = 1200;
   protected Runnable runnable;
   protected volatile Object notifier;
   private final Runnable listener;
   private volatile boolean dispatched;
   protected boolean catchExceptions;
   private Exception exception;
   private Throwable throwable;
   private long when;
   private static final long serialVersionUID = 436056344909459450L;

   public InvocationEvent(Object var1, Runnable var2) {
      this(var1, 1200, var2, (Object)null, (Runnable)null, false);
   }

   public InvocationEvent(Object var1, Runnable var2, Object var3, boolean var4) {
      this(var1, 1200, var2, var3, (Runnable)null, var4);
   }

   public InvocationEvent(Object var1, Runnable var2, Runnable var3, boolean var4) {
      this(var1, 1200, var2, (Object)null, var3, var4);
   }

   protected InvocationEvent(Object var1, int var2, Runnable var3, Object var4, boolean var5) {
      this(var1, var2, var3, var4, (Runnable)null, var5);
   }

   private InvocationEvent(Object var1, int var2, Runnable var3, Object var4, Runnable var5, boolean var6) {
      super(var1, var2);
      this.dispatched = false;
      this.exception = null;
      this.throwable = null;
      this.runnable = var3;
      this.notifier = var4;
      this.listener = var5;
      this.catchExceptions = var6;
      this.when = System.currentTimeMillis();
   }

   public void dispatch() {
      try {
         if (this.catchExceptions) {
            try {
               this.runnable.run();
            } catch (Throwable var5) {
               if (var5 instanceof Exception) {
                  this.exception = (Exception)var5;
               }

               this.throwable = var5;
            }
         } else {
            this.runnable.run();
         }
      } finally {
         this.finishedDispatching(true);
      }

   }

   public Exception getException() {
      return this.catchExceptions ? this.exception : null;
   }

   public Throwable getThrowable() {
      return this.catchExceptions ? this.throwable : null;
   }

   public long getWhen() {
      return this.when;
   }

   public boolean isDispatched() {
      return this.dispatched;
   }

   private void finishedDispatching(boolean var1) {
      this.dispatched = var1;
      if (this.notifier != null) {
         synchronized(this.notifier) {
            this.notifier.notifyAll();
         }
      }

      if (this.listener != null) {
         this.listener.run();
      }

   }

   public String paramString() {
      String var1;
      switch(this.id) {
      case 1200:
         var1 = "INVOCATION_DEFAULT";
         break;
      default:
         var1 = "unknown type";
      }

      return var1 + ",runnable=" + this.runnable + ",notifier=" + this.notifier + ",catchExceptions=" + this.catchExceptions + ",when=" + this.when;
   }

   static {
      AWTAccessor.setInvocationEventAccessor(new AWTAccessor.InvocationEventAccessor() {
         public void dispose(InvocationEvent var1) {
            var1.finishedDispatching(false);
         }
      });
   }
}
