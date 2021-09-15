package sun.print;

import java.util.Vector;
import javax.print.PrintService;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

class ServiceNotifier extends Thread {
   private PrintService service;
   private Vector listeners;
   private boolean stop = false;
   private PrintServiceAttributeSet lastSet;

   ServiceNotifier(PrintService var1) {
      super(var1.getName() + " notifier");
      this.service = var1;
      this.listeners = new Vector();

      try {
         this.setPriority(4);
         this.setDaemon(true);
         this.start();
      } catch (SecurityException var3) {
      }

   }

   void addListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.listeners != null) {
            this.listeners.add(var1);
         }
      }
   }

   void removeListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.listeners != null) {
            this.listeners.remove(var1);
         }
      }
   }

   boolean isEmpty() {
      return this.listeners == null || this.listeners.isEmpty();
   }

   void stopNotifier() {
      this.stop = true;
   }

   void wake() {
      try {
         this.interrupt();
      } catch (SecurityException var2) {
      }

   }

   public void run() {
      long var1 = 15000L;
      long var3 = 2000L;

      while(!this.stop) {
         try {
            Thread.sleep(var3);
         } catch (InterruptedException var14) {
         }

         synchronized(this) {
            if (this.listeners != null) {
               long var10 = System.currentTimeMillis();
               if (this.listeners != null) {
                  PrintServiceAttributeSet var8;
                  if (this.service instanceof AttributeUpdater) {
                     var8 = ((AttributeUpdater)this.service).getUpdatedAttributes();
                  } else {
                     var8 = this.service.getAttributes();
                  }

                  if (var8 != null && !var8.isEmpty()) {
                     for(int var12 = 0; var12 < this.listeners.size(); ++var12) {
                        PrintServiceAttributeListener var7 = (PrintServiceAttributeListener)this.listeners.elementAt(var12);
                        HashPrintServiceAttributeSet var5 = new HashPrintServiceAttributeSet(var8);
                        PrintServiceAttributeEvent var6 = new PrintServiceAttributeEvent(this.service, var5);
                        var7.attributeUpdate(var6);
                     }
                  }
               }

               var3 = (System.currentTimeMillis() - var10) * 10L;
               if (var3 < var1) {
                  var3 = var1;
               }
            }
         }
      }

   }
}
