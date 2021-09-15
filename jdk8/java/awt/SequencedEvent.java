package java.awt;

import java.util.LinkedList;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

class SequencedEvent extends AWTEvent implements ActiveEvent {
   private static final long serialVersionUID = 547742659238625067L;
   private static final int ID = 1006;
   private static final LinkedList<SequencedEvent> list = new LinkedList();
   private final AWTEvent nested;
   private AppContext appContext;
   private boolean disposed;

   public SequencedEvent(AWTEvent var1) {
      super(var1.getSource(), 1006);
      this.nested = var1;
      SunToolkit.setSystemGenerated(var1);
      Class var2 = SequencedEvent.class;
      synchronized(SequencedEvent.class) {
         list.add(this);
      }
   }

   public final void dispatch() {
      try {
         this.appContext = AppContext.getAppContext();
         if (getFirst() != this) {
            if (EventQueue.isDispatchThread()) {
               EventDispatchThread var11 = (EventDispatchThread)Thread.currentThread();
               var11.pumpEvents(1007, new Conditional() {
                  public boolean evaluate() {
                     return !SequencedEvent.this.isFirstOrDisposed();
                  }
               });
            } else {
               while(!this.isFirstOrDisposed()) {
                  Class var1 = SequencedEvent.class;
                  synchronized(SequencedEvent.class) {
                     try {
                        SequencedEvent.class.wait(1000L);
                     } catch (InterruptedException var8) {
                        break;
                     }
                  }
               }
            }
         }

         if (!this.disposed) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent(this);
            Toolkit.getEventQueue().dispatchEvent(this.nested);
         }
      } finally {
         this.dispose();
      }

   }

   private static final boolean isOwnerAppContextDisposed(SequencedEvent var0) {
      if (var0 != null) {
         Object var1 = var0.nested.getSource();
         if (var1 instanceof Component) {
            return ((Component)var1).appContext.isDisposed();
         }
      }

      return false;
   }

   public final boolean isFirstOrDisposed() {
      if (this.disposed) {
         return true;
      } else {
         return this == getFirstWithContext() || this.disposed;
      }
   }

   private static final synchronized SequencedEvent getFirst() {
      return (SequencedEvent)list.getFirst();
   }

   private static final SequencedEvent getFirstWithContext() {
      SequencedEvent var0;
      for(var0 = getFirst(); isOwnerAppContextDisposed(var0); var0 = getFirst()) {
         var0.dispose();
      }

      return var0;
   }

   final void dispose() {
      Class var1 = SequencedEvent.class;
      synchronized(SequencedEvent.class) {
         if (this.disposed) {
            return;
         }

         if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getCurrentSequencedEvent() == this) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().setCurrentSequencedEvent((SequencedEvent)null);
         }

         this.disposed = true;
      }

      if (this.appContext != null) {
         SunToolkit.postEvent(this.appContext, new SentEvent());
      }

      SequencedEvent var6 = null;
      Class var2 = SequencedEvent.class;
      synchronized(SequencedEvent.class) {
         SequencedEvent.class.notifyAll();
         if (list.getFirst() == this) {
            list.removeFirst();
            if (!list.isEmpty()) {
               var6 = (SequencedEvent)list.getFirst();
            }
         } else {
            list.remove(this);
         }
      }

      if (var6 != null && var6.appContext != null) {
         SunToolkit.postEvent(var6.appContext, new SentEvent());
      }

   }

   static {
      AWTAccessor.setSequencedEventAccessor(new AWTAccessor.SequencedEventAccessor() {
         public AWTEvent getNested(AWTEvent var1) {
            return ((SequencedEvent)var1).nested;
         }

         public boolean isSequencedEvent(AWTEvent var1) {
            return var1 instanceof SequencedEvent;
         }
      });
   }
}
