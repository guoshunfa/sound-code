package sun.java2d.opengl;

import java.security.AccessController;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;
import sun.misc.ThreadGroupUtils;

public class OGLRenderQueue extends RenderQueue {
   private static OGLRenderQueue theInstance;
   private final OGLRenderQueue.QueueFlusher flusher = (OGLRenderQueue.QueueFlusher)AccessController.doPrivileged(() -> {
      return new OGLRenderQueue.QueueFlusher(ThreadGroupUtils.getRootThreadGroup());
   });

   private OGLRenderQueue() {
   }

   public static synchronized OGLRenderQueue getInstance() {
      if (theInstance == null) {
         theInstance = new OGLRenderQueue();
      }

      return theInstance;
   }

   public static void sync() {
      if (theInstance != null) {
         theInstance.lock();

         try {
            theInstance.ensureCapacity(4);
            theInstance.getBuffer().putInt(76);
            theInstance.flushNow();
         } finally {
            theInstance.unlock();
         }
      }

   }

   public static void disposeGraphicsConfig(long var0) {
      OGLRenderQueue var2 = getInstance();
      var2.lock();

      try {
         OGLContext.setScratchSurface(var0);
         RenderBuffer var3 = var2.getBuffer();
         var2.ensureCapacityAndAlignment(12, 4);
         var3.putInt(74);
         var3.putLong(var0);
         var2.flushNow();
      } finally {
         var2.unlock();
      }

   }

   public static boolean isQueueFlusherThread() {
      return Thread.currentThread() == getInstance().flusher;
   }

   public void flushNow() {
      try {
         this.flusher.flushNow();
      } catch (Exception var2) {
         System.err.println("exception in flushNow:");
         var2.printStackTrace();
      }

   }

   public void flushAndInvokeNow(Runnable var1) {
      try {
         this.flusher.flushAndInvokeNow(var1);
      } catch (Exception var3) {
         System.err.println("exception in flushAndInvokeNow:");
         var3.printStackTrace();
      }

   }

   private native void flushBuffer(long var1, int var3);

   private void flushBuffer() {
      int var1 = this.buf.position();
      if (var1 > 0) {
         this.flushBuffer(this.buf.getAddress(), var1);
      }

      this.buf.clear();
      this.refSet.clear();
   }

   private class QueueFlusher extends Thread {
      private boolean needsFlush;
      private Runnable task;
      private Error error;

      public QueueFlusher(ThreadGroup var2) {
         super(var2, "Java2D Queue Flusher");
         this.setDaemon(true);
         this.setPriority(10);
         this.start();
      }

      public synchronized void flushNow() {
         this.needsFlush = true;
         this.notify();

         while(this.needsFlush) {
            try {
               this.wait();
            } catch (InterruptedException var2) {
            }
         }

         if (this.error != null) {
            throw this.error;
         }
      }

      public synchronized void flushAndInvokeNow(Runnable var1) {
         this.task = var1;
         this.flushNow();
      }

      public synchronized void run() {
         boolean var1 = false;

         while(true) {
            while(this.needsFlush) {
               try {
                  this.error = null;
                  OGLRenderQueue.this.flushBuffer();
                  if (this.task != null) {
                     this.task.run();
                  }
               } catch (Error var9) {
                  this.error = var9;
               } catch (Exception var10) {
                  System.err.println("exception in QueueFlusher:");
                  var10.printStackTrace();
               } finally {
                  if (var1) {
                     OGLRenderQueue.this.unlock();
                  }

                  this.task = null;
                  this.needsFlush = false;
                  this.notify();
               }
            }

            try {
               var1 = false;
               this.wait(100L);
               if (!this.needsFlush && (var1 = OGLRenderQueue.this.tryLock())) {
                  if (OGLRenderQueue.this.buf.position() > 0) {
                     this.needsFlush = true;
                  } else {
                     OGLRenderQueue.this.unlock();
                  }
               }
            } catch (InterruptedException var8) {
            }
         }
      }
   }
}
