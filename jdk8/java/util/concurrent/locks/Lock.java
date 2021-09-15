package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public interface Lock {
   void lock();

   void lockInterruptibly() throws InterruptedException;

   boolean tryLock();

   boolean tryLock(long var1, TimeUnit var3) throws InterruptedException;

   void unlock();

   Condition newCondition();
}
