package sun.misc;

import java.util.concurrent.locks.ReentrantLock;

public final class GThreadHelper {
   private static final ReentrantLock LOCK = new ReentrantLock();
   private static boolean isGThreadInitialized = false;

   public static void lock() {
      LOCK.lock();
   }

   public static void unlock() {
      LOCK.unlock();
   }

   public static boolean getAndSetInitializationNeededFlag() {
      boolean var0 = isGThreadInitialized;
      isGThreadInitialized = true;
      return var0;
   }
}
