package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

abstract class AbstractWatchService implements WatchService {
   private final LinkedBlockingDeque<WatchKey> pendingKeys = new LinkedBlockingDeque();
   private final WatchKey CLOSE_KEY = new AbstractWatchKey((Path)null, (AbstractWatchService)null) {
      public boolean isValid() {
         return true;
      }

      public void cancel() {
      }
   };
   private volatile boolean closed;
   private final Object closeLock = new Object();

   protected AbstractWatchService() {
   }

   abstract WatchKey register(Path var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException;

   final void enqueueKey(WatchKey var1) {
      this.pendingKeys.offer(var1);
   }

   private void checkOpen() {
      if (this.closed) {
         throw new ClosedWatchServiceException();
      }
   }

   private void checkKey(WatchKey var1) {
      if (var1 == this.CLOSE_KEY) {
         this.enqueueKey(var1);
      }

      this.checkOpen();
   }

   public final WatchKey poll() {
      this.checkOpen();
      WatchKey var1 = (WatchKey)this.pendingKeys.poll();
      this.checkKey(var1);
      return var1;
   }

   public final WatchKey poll(long var1, TimeUnit var3) throws InterruptedException {
      this.checkOpen();
      WatchKey var4 = (WatchKey)this.pendingKeys.poll(var1, var3);
      this.checkKey(var4);
      return var4;
   }

   public final WatchKey take() throws InterruptedException {
      this.checkOpen();
      WatchKey var1 = (WatchKey)this.pendingKeys.take();
      this.checkKey(var1);
      return var1;
   }

   final boolean isOpen() {
      return !this.closed;
   }

   final Object closeLock() {
      return this.closeLock;
   }

   abstract void implClose() throws IOException;

   public final void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.closed) {
            this.closed = true;
            this.implClose();
            this.pendingKeys.clear();
            this.pendingKeys.offer(this.CLOSE_KEY);
         }
      }
   }
}
