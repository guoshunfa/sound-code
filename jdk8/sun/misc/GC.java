package sun.misc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedSet;
import java.util.TreeSet;

public class GC {
   private static final long NO_TARGET = Long.MAX_VALUE;
   private static long latencyTarget = Long.MAX_VALUE;
   private static Thread daemon = null;
   private static Object lock = new GC.LatencyLock();

   private GC() {
   }

   public static native long maxObjectInspectionAge();

   private static void setLatencyTarget(long var0) {
      latencyTarget = var0;
      if (daemon == null) {
         GC.Daemon.create();
      } else {
         lock.notify();
      }

   }

   public static GC.LatencyRequest requestLatency(long var0) {
      return new GC.LatencyRequest(var0);
   }

   public static long currentLatencyTarget() {
      long var0 = latencyTarget;
      return var0 == Long.MAX_VALUE ? 0L : var0;
   }

   public static class LatencyRequest implements Comparable<GC.LatencyRequest> {
      private static long counter = 0L;
      private static SortedSet<GC.LatencyRequest> requests = null;
      private long latency;
      private long id;

      private static void adjustLatencyIfNeeded() {
         if (requests != null && !requests.isEmpty()) {
            GC.LatencyRequest var0 = (GC.LatencyRequest)requests.first();
            if (var0.latency != GC.latencyTarget) {
               GC.setLatencyTarget(var0.latency);
            }
         } else if (GC.latencyTarget != Long.MAX_VALUE) {
            GC.setLatencyTarget(Long.MAX_VALUE);
         }

      }

      private LatencyRequest(long var1) {
         if (var1 <= 0L) {
            throw new IllegalArgumentException("Non-positive latency: " + var1);
         } else {
            this.latency = var1;
            synchronized(GC.lock) {
               this.id = ++counter;
               if (requests == null) {
                  requests = new TreeSet();
               }

               requests.add(this);
               adjustLatencyIfNeeded();
            }
         }
      }

      public void cancel() {
         synchronized(GC.lock) {
            if (this.latency == Long.MAX_VALUE) {
               throw new IllegalStateException("Request already cancelled");
            } else if (!requests.remove(this)) {
               throw new InternalError("Latency request " + this + " not found");
            } else {
               if (requests.isEmpty()) {
                  requests = null;
               }

               this.latency = Long.MAX_VALUE;
               adjustLatencyIfNeeded();
            }
         }
      }

      public int compareTo(GC.LatencyRequest var1) {
         long var2 = this.latency - var1.latency;
         if (var2 == 0L) {
            var2 = this.id - var1.id;
         }

         return var2 < 0L ? -1 : (var2 > 0L ? 1 : 0);
      }

      public String toString() {
         return GC.LatencyRequest.class.getName() + "[" + this.latency + "," + this.id + "]";
      }

      // $FF: synthetic method
      LatencyRequest(long var1, Object var3) {
         this(var1);
      }
   }

   private static class Daemon extends Thread {
      public void run() {
         while(true) {
            synchronized(GC.lock) {
               long var1 = GC.latencyTarget;
               if (var1 == Long.MAX_VALUE) {
                  GC.daemon = null;
                  return;
               }

               long var4 = GC.maxObjectInspectionAge();
               if (var4 >= var1) {
                  System.gc();
                  var4 = 0L;
               }

               try {
                  GC.lock.wait(var1 - var4);
               } catch (InterruptedException var8) {
               }
            }
         }
      }

      private Daemon(ThreadGroup var1) {
         super(var1, "GC Daemon");
      }

      public static void create() {
         PrivilegedAction var0 = new PrivilegedAction<Void>() {
            public Void run() {
               ThreadGroup var1 = Thread.currentThread().getThreadGroup();

               for(ThreadGroup var2 = var1; var2 != null; var2 = var2.getParent()) {
                  var1 = var2;
               }

               GC.Daemon var3 = new GC.Daemon(var1);
               var3.setDaemon(true);
               var3.setPriority(2);
               var3.start();
               GC.daemon = var3;
               return null;
            }
         };
         AccessController.doPrivileged(var0);
      }

      // $FF: synthetic method
      Daemon(ThreadGroup var1, Object var2) {
         this(var1);
      }
   }

   private static class LatencyLock {
      private LatencyLock() {
      }

      // $FF: synthetic method
      LatencyLock(Object var1) {
         this();
      }
   }
}
