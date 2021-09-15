package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

abstract class AbstractPoller implements Runnable {
   private final LinkedList<AbstractPoller.Request> requestList = new LinkedList();
   private boolean shutdown = false;

   protected AbstractPoller() {
   }

   public void start() {
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            Thread var1 = new Thread(AbstractPoller.this);
            var1.setDaemon(true);
            var1.start();
            return null;
         }
      });
   }

   abstract void wakeup() throws IOException;

   abstract Object implRegister(Path var1, Set<? extends WatchEvent.Kind<?>> var2, WatchEvent.Modifier... var3);

   abstract void implCancelKey(WatchKey var1);

   abstract void implCloseAll();

   final WatchKey register(Path var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         HashSet var4 = new HashSet(var2.length);
         WatchEvent.Kind[] var5 = var2;
         int var6 = var2.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            WatchEvent.Kind var8 = var5[var7];
            if (var8 != StandardWatchEventKinds.ENTRY_CREATE && var8 != StandardWatchEventKinds.ENTRY_MODIFY && var8 != StandardWatchEventKinds.ENTRY_DELETE) {
               if (var8 != StandardWatchEventKinds.OVERFLOW) {
                  if (var8 == null) {
                     throw new NullPointerException("An element in event set is 'null'");
                  }

                  throw new UnsupportedOperationException(var8.name());
               }
            } else {
               var4.add(var8);
            }
         }

         if (var4.isEmpty()) {
            throw new IllegalArgumentException("No events to register");
         } else {
            return (WatchKey)this.invoke(AbstractPoller.RequestType.REGISTER, var1, var4, var3);
         }
      }
   }

   final void cancel(WatchKey var1) {
      try {
         this.invoke(AbstractPoller.RequestType.CANCEL, var1);
      } catch (IOException var3) {
         throw new AssertionError(var3.getMessage());
      }
   }

   final void close() throws IOException {
      this.invoke(AbstractPoller.RequestType.CLOSE);
   }

   private Object invoke(AbstractPoller.RequestType var1, Object... var2) throws IOException {
      AbstractPoller.Request var3 = new AbstractPoller.Request(var1, var2);
      synchronized(this.requestList) {
         if (this.shutdown) {
            throw new ClosedWatchServiceException();
         }

         this.requestList.add(var3);
      }

      this.wakeup();
      Object var4 = var3.awaitResult();
      if (var4 instanceof RuntimeException) {
         throw (RuntimeException)var4;
      } else if (var4 instanceof IOException) {
         throw (IOException)var4;
      } else {
         return var4;
      }
   }

   boolean processRequests() {
      AbstractPoller.Request var2;
      synchronized(this.requestList) {
         while((var2 = (AbstractPoller.Request)this.requestList.poll()) != null) {
            if (this.shutdown) {
               var2.release(new ClosedWatchServiceException());
            }

            Object[] var3;
            switch(var2.type()) {
            case REGISTER:
               var3 = var2.parameters();
               Path var9 = (Path)var3[0];
               Set var5 = (Set)var3[1];
               WatchEvent.Modifier[] var6 = (WatchEvent.Modifier[])((WatchEvent.Modifier[])var3[2]);
               var2.release(this.implRegister(var9, var5, var6));
               break;
            case CANCEL:
               var3 = var2.parameters();
               WatchKey var4 = (WatchKey)var3[0];
               this.implCancelKey(var4);
               var2.release((Object)null);
               break;
            case CLOSE:
               this.implCloseAll();
               var2.release((Object)null);
               this.shutdown = true;
               break;
            default:
               var2.release(new IOException("request not recognized"));
            }
         }
      }

      return this.shutdown;
   }

   private static class Request {
      private final AbstractPoller.RequestType type;
      private final Object[] params;
      private boolean completed = false;
      private Object result = null;

      Request(AbstractPoller.RequestType var1, Object... var2) {
         this.type = var1;
         this.params = var2;
      }

      AbstractPoller.RequestType type() {
         return this.type;
      }

      Object[] parameters() {
         return this.params;
      }

      void release(Object var1) {
         synchronized(this) {
            this.completed = true;
            this.result = var1;
            this.notifyAll();
         }
      }

      Object awaitResult() {
         boolean var1 = false;
         synchronized(this) {
            while(!this.completed) {
               try {
                  this.wait();
               } catch (InterruptedException var5) {
                  var1 = true;
               }
            }

            if (var1) {
               Thread.currentThread().interrupt();
            }

            return this.result;
         }
      }
   }

   private static enum RequestType {
      REGISTER,
      CANCEL,
      CLOSE;
   }
}
