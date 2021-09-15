package javax.swing.text;

import java.util.Vector;
import sun.awt.AppContext;

public class LayoutQueue {
   private static final Object DEFAULT_QUEUE = new Object();
   private Vector<Runnable> tasks = new Vector();
   private Thread worker;

   public static LayoutQueue getDefaultQueue() {
      AppContext var0 = AppContext.getAppContext();
      synchronized(DEFAULT_QUEUE) {
         LayoutQueue var2 = (LayoutQueue)var0.get(DEFAULT_QUEUE);
         if (var2 == null) {
            var2 = new LayoutQueue();
            var0.put(DEFAULT_QUEUE, var2);
         }

         return var2;
      }
   }

   public static void setDefaultQueue(LayoutQueue var0) {
      synchronized(DEFAULT_QUEUE) {
         AppContext.getAppContext().put(DEFAULT_QUEUE, var0);
      }
   }

   public synchronized void addTask(Runnable var1) {
      if (this.worker == null) {
         this.worker = new LayoutQueue.LayoutThread();
         this.worker.start();
      }

      this.tasks.addElement(var1);
      this.notifyAll();
   }

   protected synchronized Runnable waitForWork() {
      while(this.tasks.size() == 0) {
         try {
            this.wait();
         } catch (InterruptedException var2) {
            return null;
         }
      }

      Runnable var1 = (Runnable)this.tasks.firstElement();
      this.tasks.removeElementAt(0);
      return var1;
   }

   class LayoutThread extends Thread {
      LayoutThread() {
         super("text-layout");
         this.setPriority(1);
      }

      public void run() {
         Runnable var1;
         do {
            var1 = LayoutQueue.this.waitForWork();
            if (var1 != null) {
               var1.run();
            }
         } while(var1 != null);

      }
   }
}
