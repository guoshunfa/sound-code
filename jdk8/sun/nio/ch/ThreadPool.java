package sun.nio.ch;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import sun.misc.InnocuousThread;
import sun.security.action.GetPropertyAction;

public class ThreadPool {
   private static final String DEFAULT_THREAD_POOL_THREAD_FACTORY = "java.nio.channels.DefaultThreadPool.threadFactory";
   private static final String DEFAULT_THREAD_POOL_INITIAL_SIZE = "java.nio.channels.DefaultThreadPool.initialSize";
   private final ExecutorService executor;
   private final boolean isFixed;
   private final int poolSize;

   private ThreadPool(ExecutorService var1, boolean var2, int var3) {
      this.executor = var1;
      this.isFixed = var2;
      this.poolSize = var3;
   }

   ExecutorService executor() {
      return this.executor;
   }

   boolean isFixedThreadPool() {
      return this.isFixed;
   }

   int poolSize() {
      return this.poolSize;
   }

   static ThreadFactory defaultThreadFactory() {
      return System.getSecurityManager() == null ? (var0) -> {
         Thread var1 = new Thread(var0);
         var1.setDaemon(true);
         return var1;
      } : (var0) -> {
         PrivilegedAction var1 = () -> {
            InnocuousThread var1 = new InnocuousThread(var0);
            var1.setDaemon(true);
            return var1;
         };
         return (Thread)AccessController.doPrivileged(var1);
      };
   }

   static ThreadPool getDefault() {
      return ThreadPool.DefaultThreadPoolHolder.defaultThreadPool;
   }

   static ThreadPool createDefault() {
      int var0 = getDefaultThreadPoolInitialSize();
      if (var0 < 0) {
         var0 = Runtime.getRuntime().availableProcessors();
      }

      ThreadFactory var1 = getDefaultThreadPoolThreadFactory();
      if (var1 == null) {
         var1 = defaultThreadFactory();
      }

      ExecutorService var2 = Executors.newCachedThreadPool(var1);
      return new ThreadPool(var2, false, var0);
   }

   static ThreadPool create(int var0, ThreadFactory var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException("'nThreads' must be > 0");
      } else {
         ExecutorService var2 = Executors.newFixedThreadPool(var0, var1);
         return new ThreadPool(var2, true, var0);
      }
   }

   public static ThreadPool wrap(ExecutorService var0, int var1) {
      if (var0 == null) {
         throw new NullPointerException("'executor' is null");
      } else {
         if (var0 instanceof ThreadPoolExecutor) {
            int var2 = ((ThreadPoolExecutor)var0).getMaximumPoolSize();
            if (var2 == Integer.MAX_VALUE) {
               if (var1 < 0) {
                  var1 = Runtime.getRuntime().availableProcessors();
               } else {
                  var1 = 0;
               }
            }
         } else if (var1 < 0) {
            var1 = 0;
         }

         return new ThreadPool(var0, false, var1);
      }
   }

   private static int getDefaultThreadPoolInitialSize() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.nio.channels.DefaultThreadPool.initialSize")));
      if (var0 != null) {
         try {
            return Integer.parseInt(var0);
         } catch (NumberFormatException var2) {
            throw new Error("Value of property 'java.nio.channels.DefaultThreadPool.initialSize' is invalid: " + var2);
         }
      } else {
         return -1;
      }
   }

   private static ThreadFactory getDefaultThreadPoolThreadFactory() {
      String var0 = (String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("java.nio.channels.DefaultThreadPool.threadFactory")));
      if (var0 != null) {
         try {
            Class var1 = Class.forName(var0, true, ClassLoader.getSystemClassLoader());
            return (ThreadFactory)var1.newInstance();
         } catch (ClassNotFoundException var2) {
            throw new Error(var2);
         } catch (InstantiationException var3) {
            throw new Error(var3);
         } catch (IllegalAccessException var4) {
            throw new Error(var4);
         }
      } else {
         return null;
      }
   }

   private static class DefaultThreadPoolHolder {
      static final ThreadPool defaultThreadPool = ThreadPool.createDefault();
   }
}
