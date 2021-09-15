package sun.net.www.http;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import sun.net.NetProperties;

class KeepAliveStreamCleaner extends LinkedList<KeepAliveCleanerEntry> implements Runnable {
   protected static int MAX_DATA_REMAINING = 512;
   protected static int MAX_CAPACITY = 10;
   protected static final int TIMEOUT = 5000;
   private static final int MAX_RETRIES = 5;

   public boolean offer(KeepAliveCleanerEntry var1) {
      return this.size() >= MAX_CAPACITY ? false : super.offer(var1);
   }

   public void run() {
      KeepAliveCleanerEntry var1 = null;

      do {
         try {
            synchronized(this) {
               long var3 = System.currentTimeMillis();
               long var5 = 5000L;

               while((var1 = (KeepAliveCleanerEntry)this.poll()) == null) {
                  this.wait(var5);
                  long var7 = System.currentTimeMillis();
                  long var9 = var7 - var3;
                  if (var9 <= var5) {
                     var3 = var7;
                     var5 -= var9;
                  } else {
                     var1 = (KeepAliveCleanerEntry)this.poll();
                     break;
                  }
               }
            }

            if (var1 == null) {
               break;
            }

            KeepAliveStream var2 = var1.getKeepAliveStream();
            if (var2 != null) {
               synchronized(var2) {
                  HttpClient var4 = var1.getHttpClient();

                  try {
                     if (var4 != null && !var4.isInKeepAliveCache()) {
                        int var24 = var4.getReadTimeout();
                        var4.setReadTimeout(5000);
                        long var6 = var2.remainingToRead();
                        if (var6 > 0L) {
                           long var8 = 0L;
                           int var10 = 0;

                           while(var8 < var6 && var10 < 5) {
                              var6 -= var8;
                              var8 = var2.skip(var6);
                              if (var8 == 0L) {
                                 ++var10;
                              }
                           }

                           var6 -= var8;
                        }

                        if (var6 == 0L) {
                           var4.setReadTimeout(var24);
                           var4.finished();
                        } else {
                           var4.closeServer();
                        }
                     }
                  } catch (IOException var20) {
                     var4.closeServer();
                  } finally {
                     var2.setClosed();
                  }
               }
            }
         } catch (InterruptedException var23) {
         }
      } while(var1 != null);

   }

   static {
      int var1 = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            return NetProperties.getInteger("http.KeepAlive.remainingData", KeepAliveStreamCleaner.MAX_DATA_REMAINING);
         }
      }) * 1024;
      MAX_DATA_REMAINING = var1;
      int var3 = (Integer)AccessController.doPrivileged(new PrivilegedAction<Integer>() {
         public Integer run() {
            return NetProperties.getInteger("http.KeepAlive.queuedConnections", KeepAliveStreamCleaner.MAX_CAPACITY);
         }
      });
      MAX_CAPACITY = var3;
   }
}
