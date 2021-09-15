package sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

abstract class Port extends AsynchronousChannelGroupImpl {
   protected final ReadWriteLock fdToChannelLock = new ReentrantReadWriteLock();
   protected final Map<Integer, Port.PollableChannel> fdToChannel = new HashMap();

   Port(AsynchronousChannelProvider var1, ThreadPool var2) {
      super(var1, var2);
   }

   final void register(int var1, Port.PollableChannel var2) {
      this.fdToChannelLock.writeLock().lock();

      try {
         if (this.isShutdown()) {
            throw new ShutdownChannelGroupException();
         }

         this.fdToChannel.put(var1, var2);
      } finally {
         this.fdToChannelLock.writeLock().unlock();
      }

   }

   protected void preUnregister(int var1) {
   }

   final void unregister(int var1) {
      boolean var2 = false;
      this.preUnregister(var1);
      this.fdToChannelLock.writeLock().lock();

      try {
         this.fdToChannel.remove(var1);
         if (this.fdToChannel.isEmpty()) {
            var2 = true;
         }
      } finally {
         this.fdToChannelLock.writeLock().unlock();
      }

      if (var2 && this.isShutdown()) {
         try {
            this.shutdownNow();
         } catch (IOException var6) {
         }
      }

   }

   abstract void startPoll(int var1, int var2);

   final boolean isEmpty() {
      this.fdToChannelLock.writeLock().lock();

      boolean var1;
      try {
         var1 = this.fdToChannel.isEmpty();
      } finally {
         this.fdToChannelLock.writeLock().unlock();
      }

      return var1;
   }

   final Object attachForeignChannel(final Channel var1, FileDescriptor var2) {
      int var3 = IOUtil.fdVal(var2);
      this.register(var3, new Port.PollableChannel() {
         public void onEvent(int var1x, boolean var2) {
         }

         public void close() throws IOException {
            var1.close();
         }
      });
      return var3;
   }

   final void detachForeignChannel(Object var1) {
      this.unregister((Integer)var1);
   }

   final void closeAllChannels() {
      Port.PollableChannel[] var2 = new Port.PollableChannel[128];

      int var3;
      do {
         this.fdToChannelLock.writeLock().lock();
         var3 = 0;

         try {
            Iterator var4 = this.fdToChannel.keySet().iterator();

            while(var4.hasNext()) {
               Integer var5 = (Integer)var4.next();
               var2[var3++] = (Port.PollableChannel)this.fdToChannel.get(var5);
               if (var3 >= 128) {
                  break;
               }
            }
         } finally {
            this.fdToChannelLock.writeLock().unlock();
         }

         for(int var11 = 0; var11 < var3; ++var11) {
            try {
               var2[var11].close();
            } catch (IOException var9) {
            }
         }
      } while(var3 > 0);

   }

   interface PollableChannel extends Closeable {
      void onEvent(int var1, boolean var2);
   }
}
