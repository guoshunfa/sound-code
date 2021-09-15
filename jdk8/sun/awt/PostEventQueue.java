package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

class PostEventQueue {
   private EventQueueItem queueHead = null;
   private EventQueueItem queueTail = null;
   private final EventQueue eventQueue;
   private Thread flushThread = null;

   PostEventQueue(EventQueue var1) {
      this.eventQueue = var1;
   }

   public void flush() {
      Thread var1 = Thread.currentThread();

      try {
         EventQueueItem var2;
         synchronized(this) {
            if (var1 == this.flushThread) {
               return;
            }

            while(true) {
               if (this.flushThread == null) {
                  if (this.queueHead == null) {
                     return;
                  }

                  this.flushThread = var1;
                  var2 = this.queueHead;
                  this.queueHead = this.queueTail = null;
                  break;
               }

               this.wait();
            }
         }

         while(true) {
            boolean var14 = false;

            try {
               var14 = true;
               if (var2 == null) {
                  var14 = false;
                  break;
               }

               this.eventQueue.postEvent(var2.event);
               var2 = var2.next;
            } finally {
               if (var14) {
                  synchronized(this) {
                     this.flushThread = null;
                     this.notifyAll();
                  }
               }
            }
         }

         synchronized(this) {
            this.flushThread = null;
            this.notifyAll();
         }
      } catch (InterruptedException var19) {
         var1.interrupt();
      }

   }

   void postEvent(AWTEvent var1) {
      EventQueueItem var2 = new EventQueueItem(var1);
      synchronized(this) {
         if (this.queueHead == null) {
            this.queueHead = this.queueTail = var2;
         } else {
            this.queueTail.next = var2;
            this.queueTail = var2;
         }
      }

      SunToolkit.wakeupEventQueue(this.eventQueue, var1.getSource() == AWTAutoShutdown.getInstance());
   }
}
