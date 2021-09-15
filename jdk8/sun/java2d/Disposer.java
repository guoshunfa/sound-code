package sun.java2d;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.misc.ThreadGroupUtils;
import sun.security.action.GetPropertyAction;

public class Disposer implements Runnable {
   private static final ReferenceQueue queue = new ReferenceQueue();
   private static final Hashtable records = new Hashtable();
   private static Disposer disposerInstance;
   public static final int WEAK = 0;
   public static final int PHANTOM = 1;
   public static int refType = 1;
   private static ArrayList<DisposerRecord> deferredRecords;
   public static volatile boolean pollingQueue;

   public static void addRecord(Object var0, long var1, long var3) {
      disposerInstance.add(var0, new DefaultDisposerRecord(var1, var3));
   }

   public static void addRecord(Object var0, DisposerRecord var1) {
      disposerInstance.add(var0, var1);
   }

   synchronized void add(Object var1, DisposerRecord var2) {
      if (var1 instanceof DisposerTarget) {
         var1 = ((DisposerTarget)var1).getDisposerReferent();
      }

      Object var3;
      if (refType == 1) {
         var3 = new PhantomReference(var1, queue);
      } else {
         var3 = new WeakReference(var1, queue);
      }

      records.put(var3, var2);
   }

   public void run() {
      while(true) {
         try {
            Reference var1 = queue.remove();
            ((Reference)var1).clear();
            DisposerRecord var2 = (DisposerRecord)records.remove(var1);
            var2.dispose();
            var1 = null;
            var2 = null;
            clearDeferredRecords();
         } catch (Exception var3) {
            System.out.println("Exception while removing reference.");
         }
      }
   }

   private static void clearDeferredRecords() {
      if (deferredRecords != null && !deferredRecords.isEmpty()) {
         for(int var0 = 0; var0 < deferredRecords.size(); ++var0) {
            try {
               DisposerRecord var1 = (DisposerRecord)deferredRecords.get(var0);
               var1.dispose();
            } catch (Exception var2) {
               System.out.println("Exception while disposing deferred rec.");
            }
         }

         deferredRecords.clear();
      }
   }

   public static void pollRemove() {
      if (!pollingQueue) {
         pollingQueue = true;
         int var1 = 0;
         int var2 = 0;

         try {
            Reference var0;
            try {
               while((var0 = queue.poll()) != null && var1 < 10000 && var2 < 100) {
                  ++var1;
                  ((Reference)var0).clear();
                  DisposerRecord var3 = (DisposerRecord)records.remove(var0);
                  if (var3 instanceof Disposer.PollDisposable) {
                     var3.dispose();
                     var0 = null;
                     var3 = null;
                  } else if (var3 != null) {
                     ++var2;
                     if (deferredRecords == null) {
                        deferredRecords = new ArrayList(5);
                     }

                     deferredRecords.add(var3);
                  }
               }
            } catch (Exception var7) {
               System.out.println("Exception while removing reference.");
            }
         } finally {
            pollingQueue = false;
         }

      }
   }

   private static native void initIDs();

   public static void addReference(Reference var0, DisposerRecord var1) {
      records.put(var0, var1);
   }

   public static void addObjectRecord(Object var0, DisposerRecord var1) {
      records.put(new WeakReference(var0, queue), var1);
   }

   public static ReferenceQueue getQueue() {
      return queue;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
      initIDs();
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("sun.java2d.reftype")));
      if (var0 != null) {
         if (var0.equals("weak")) {
            refType = 0;
            System.err.println("Using WEAK refs");
         } else {
            refType = 1;
            System.err.println("Using PHANTOM refs");
         }
      }

      disposerInstance = new Disposer();
      AccessController.doPrivileged(() -> {
         ThreadGroup var0 = ThreadGroupUtils.getRootThreadGroup();
         Thread var1 = new Thread(var0, disposerInstance, "Java2D Disposer");
         var1.setContextClassLoader((ClassLoader)null);
         var1.setDaemon(true);
         var1.setPriority(10);
         var1.start();
         return null;
      });
      deferredRecords = null;
      pollingQueue = false;
   }

   public interface PollDisposable {
   }
}
