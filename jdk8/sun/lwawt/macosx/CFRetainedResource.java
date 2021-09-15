package sun.lwawt.macosx;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CFRetainedResource {
   private final boolean disposeOnAppKitThread;
   protected volatile long ptr;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private final Lock writeLock;
   private final Lock readLock;

   private static native void nativeCFRelease(long var0, boolean var2);

   protected CFRetainedResource(long var1, boolean var3) {
      this.writeLock = this.lock.writeLock();
      this.readLock = this.lock.readLock();
      this.disposeOnAppKitThread = var3;
      this.ptr = var1;
   }

   protected void setPtr(long var1) {
      this.writeLock.lock();

      try {
         if (this.ptr != 0L) {
            this.dispose();
         }

         this.ptr = var1;
      } finally {
         this.writeLock.unlock();
      }

   }

   protected void dispose() {
      long var1 = 0L;
      this.writeLock.lock();

      try {
         if (this.ptr == 0L) {
            return;
         }

         var1 = this.ptr;
         this.ptr = 0L;
      } finally {
         this.writeLock.unlock();
      }

      nativeCFRelease(var1, this.disposeOnAppKitThread);
   }

   public final void execute(CFRetainedResource.CFNativeAction var1) {
      this.readLock.lock();

      try {
         if (this.ptr != 0L) {
            var1.run(this.ptr);
         }
      } finally {
         this.readLock.unlock();
      }

   }

   final long executeGet(CFRetainedResource.CFNativeActionGet var1) {
      this.readLock.lock();

      long var2;
      try {
         if (this.ptr == 0L) {
            return 0L;
         }

         var2 = var1.run(this.ptr);
      } finally {
         this.readLock.unlock();
      }

      return var2;
   }

   protected final void finalize() throws Throwable {
      this.dispose();
   }

   interface CFNativeActionGet {
      long run(long var1);
   }

   public interface CFNativeAction {
      void run(long var1);
   }
}
