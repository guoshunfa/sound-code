package java.util.concurrent.locks;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public interface Condition {
   void await() throws InterruptedException;

   void awaitUninterruptibly();

   long awaitNanos(long var1) throws InterruptedException;

   boolean await(long var1, TimeUnit var3) throws InterruptedException;

   boolean awaitUntil(Date var1) throws InterruptedException;

   void signal();

   void signalAll();
}
