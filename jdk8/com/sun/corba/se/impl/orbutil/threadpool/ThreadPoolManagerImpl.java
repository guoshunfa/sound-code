package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchThreadPoolException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolChooser;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManagerImpl implements ThreadPoolManager {
   private ThreadPool threadPool;
   private ThreadGroup threadGroup = this.getThreadGroup();
   private static final ORBUtilSystemException wrapper = ORBUtilSystemException.get("rpc.transport");
   private static AtomicInteger tgCount = new AtomicInteger();

   public ThreadPoolManagerImpl() {
      this.threadPool = new ThreadPoolImpl(this.threadGroup, "default-threadpool");
   }

   private ThreadGroup getThreadGroup() {
      ThreadGroup var1;
      try {
         var1 = (ThreadGroup)AccessController.doPrivileged(new PrivilegedAction<ThreadGroup>() {
            public ThreadGroup run() {
               ThreadGroup var1 = Thread.currentThread().getThreadGroup();
               ThreadGroup var2 = var1;

               try {
                  while(var2 != null) {
                     var1 = var2;
                     var2 = var2.getParent();
                  }
               } catch (SecurityException var4) {
               }

               return new ThreadGroup(var1, "ORB ThreadGroup " + ThreadPoolManagerImpl.tgCount.getAndIncrement());
            }
         });
      } catch (SecurityException var3) {
         var1 = Thread.currentThread().getThreadGroup();
      }

      return var1;
   }

   public void close() {
      try {
         this.threadPool.close();
      } catch (IOException var5) {
         wrapper.threadPoolCloseError();
      }

      try {
         boolean var1 = this.threadGroup.isDestroyed();
         int var2 = this.threadGroup.activeCount();
         int var3 = this.threadGroup.activeGroupCount();
         if (var1) {
            wrapper.threadGroupIsDestroyed(this.threadGroup);
         } else {
            if (var2 > 0) {
               wrapper.threadGroupHasActiveThreadsInClose(this.threadGroup, var2);
            }

            if (var3 > 0) {
               wrapper.threadGroupHasSubGroupsInClose(this.threadGroup, var3);
            }

            this.threadGroup.destroy();
         }
      } catch (IllegalThreadStateException var4) {
         wrapper.threadGroupDestroyFailed((Throwable)var4, this.threadGroup);
      }

      this.threadGroup = null;
   }

   public ThreadPool getThreadPool(String var1) throws NoSuchThreadPoolException {
      return this.threadPool;
   }

   public ThreadPool getThreadPool(int var1) throws NoSuchThreadPoolException {
      return this.threadPool;
   }

   public int getThreadPoolNumericId(String var1) {
      return 0;
   }

   public String getThreadPoolStringId(int var1) {
      return "";
   }

   public ThreadPool getDefaultThreadPool() {
      return this.threadPool;
   }

   public ThreadPoolChooser getThreadPoolChooser(String var1) {
      return null;
   }

   public ThreadPoolChooser getThreadPoolChooser(int var1) {
      return null;
   }

   public void setThreadPoolChooser(String var1, ThreadPoolChooser var2) {
   }

   public int getThreadPoolChooserNumericId(String var1) {
      return 0;
   }
}
