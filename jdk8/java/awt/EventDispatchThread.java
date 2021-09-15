package java.awt;

import java.util.ArrayList;
import sun.awt.EventQueueDelegate;
import sun.awt.ModalExclude;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDragSourceContextPeer;
import sun.util.logging.PlatformLogger;

class EventDispatchThread extends Thread {
   private static final PlatformLogger eventLog = PlatformLogger.getLogger("java.awt.event.EventDispatchThread");
   private EventQueue theQueue;
   private volatile boolean doDispatch = true;
   private static final int ANY_EVENT = -1;
   private ArrayList<EventFilter> eventFilters = new ArrayList();

   EventDispatchThread(ThreadGroup var1, String var2, EventQueue var3) {
      super(var1, var2);
      this.setEventQueue(var3);
   }

   public void stopDispatching() {
      this.doDispatch = false;
   }

   public void run() {
      try {
         this.pumpEvents(new Conditional() {
            public boolean evaluate() {
               return true;
            }
         });
      } finally {
         this.getEventQueue().detachDispatchThread(this);
      }

   }

   void pumpEvents(Conditional var1) {
      this.pumpEvents(-1, var1);
   }

   void pumpEventsForHierarchy(Conditional var1, Component var2) {
      this.pumpEventsForHierarchy(-1, var1, var2);
   }

   void pumpEvents(int var1, Conditional var2) {
      this.pumpEventsForHierarchy(var1, var2, (Component)null);
   }

   void pumpEventsForHierarchy(int var1, Conditional var2, Component var3) {
      this.pumpEventsForFilter(var1, var2, new EventDispatchThread.HierarchyEventFilter(var3));
   }

   void pumpEventsForFilter(Conditional var1, EventFilter var2) {
      this.pumpEventsForFilter(-1, var1, var2);
   }

   void pumpEventsForFilter(int var1, Conditional var2, EventFilter var3) {
      this.addEventFilter(var3);
      this.doDispatch = true;

      while(this.doDispatch && !this.isInterrupted() && var2.evaluate()) {
         this.pumpOneEventForFilters(var1);
      }

      this.removeEventFilter(var3);
   }

   void addEventFilter(EventFilter var1) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
         eventLog.finest("adding the event filter: " + var1);
      }

      synchronized(this.eventFilters) {
         if (!this.eventFilters.contains(var1)) {
            if (var1 instanceof ModalEventFilter) {
               ModalEventFilter var3 = (ModalEventFilter)var1;
               boolean var4 = false;

               int var9;
               for(var9 = 0; var9 < this.eventFilters.size(); ++var9) {
                  EventFilter var5 = (EventFilter)this.eventFilters.get(var9);
                  if (var5 instanceof ModalEventFilter) {
                     ModalEventFilter var6 = (ModalEventFilter)var5;
                     if (var6.compareTo(var3) > 0) {
                        break;
                     }
                  }
               }

               this.eventFilters.add(var9, var1);
            } else {
               this.eventFilters.add(var1);
            }
         }

      }
   }

   void removeEventFilter(EventFilter var1) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
         eventLog.finest("removing the event filter: " + var1);
      }

      synchronized(this.eventFilters) {
         this.eventFilters.remove(var1);
      }
   }

   boolean filterAndCheckEvent(AWTEvent var1) {
      boolean var2 = true;
      synchronized(this.eventFilters) {
         int var4 = this.eventFilters.size() - 1;

         while(var4 >= 0) {
            EventFilter var5 = (EventFilter)this.eventFilters.get(var4);
            EventFilter.FilterAction var6 = var5.acceptEvent(var1);
            if (var6 == EventFilter.FilterAction.REJECT) {
               var2 = false;
            } else if (var6 != EventFilter.FilterAction.ACCEPT_IMMEDIATELY) {
               --var4;
               continue;
            }
            break;
         }
      }

      return var2 && SunDragSourceContextPeer.checkEvent(var1);
   }

   void pumpOneEventForFilters(int var1) {
      AWTEvent var2 = null;
      boolean var3 = false;

      try {
         EventQueue var4 = null;
         EventQueueDelegate.Delegate var5 = null;

         do {
            var4 = this.getEventQueue();
            var5 = EventQueueDelegate.getDelegate();
            if (var5 != null && var1 == -1) {
               var2 = var5.getNextEvent(var4);
            } else {
               var2 = var1 == -1 ? var4.getNextEvent() : var4.getNextEvent(var1);
            }

            var3 = this.filterAndCheckEvent(var2);
            if (!var3) {
               var2.consume();
            }
         } while(!var3);

         if (eventLog.isLoggable(PlatformLogger.Level.FINEST)) {
            eventLog.finest("Dispatching: " + var2);
         }

         Object var6 = null;
         if (var5 != null) {
            var6 = var5.beforeDispatch(var2);
         }

         var4.dispatchEvent(var2);
         if (var5 != null) {
            var5.afterDispatch(var2, var6);
         }
      } catch (ThreadDeath var7) {
         this.doDispatch = false;
         throw var7;
      } catch (InterruptedException var8) {
         this.doDispatch = false;
      } catch (Throwable var9) {
         this.processException(var9);
      }

   }

   private void processException(Throwable var1) {
      if (eventLog.isLoggable(PlatformLogger.Level.FINE)) {
         eventLog.fine("Processing exception: " + var1);
      }

      this.getUncaughtExceptionHandler().uncaughtException(this, var1);
   }

   public synchronized EventQueue getEventQueue() {
      return this.theQueue;
   }

   public synchronized void setEventQueue(EventQueue var1) {
      this.theQueue = var1;
   }

   private static class HierarchyEventFilter implements EventFilter {
      private Component modalComponent;

      public HierarchyEventFilter(Component var1) {
         this.modalComponent = var1;
      }

      public EventFilter.FilterAction acceptEvent(AWTEvent var1) {
         if (this.modalComponent != null) {
            int var2 = var1.getID();
            boolean var3 = var2 >= 500 && var2 <= 507;
            boolean var4 = var2 >= 1001 && var2 <= 1001;
            boolean var5 = var2 == 201;
            if (Component.isInstanceOf(this.modalComponent, "javax.swing.JInternalFrame")) {
               return var5 ? EventFilter.FilterAction.REJECT : EventFilter.FilterAction.ACCEPT;
            }

            if (var3 || var4 || var5) {
               Object var6 = var1.getSource();
               if (var6 instanceof ModalExclude) {
                  return EventFilter.FilterAction.ACCEPT;
               }

               if (var6 instanceof Component) {
                  Object var7 = (Component)var6;
                  boolean var8 = false;
                  if (this.modalComponent instanceof Container) {
                     while(var7 != this.modalComponent && var7 != null) {
                        if (var7 instanceof Window && SunToolkit.isModalExcluded((Window)var7)) {
                           var8 = true;
                           break;
                        }

                        var7 = ((Component)var7).getParent();
                     }
                  }

                  if (!var8 && var7 != this.modalComponent) {
                     return EventFilter.FilterAction.REJECT;
                  }
               }
            }
         }

         return EventFilter.FilterAction.ACCEPT;
      }
   }
}
