package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Pipe;
import java.nio.channels.spi.SelectorProvider;

class SinkChannelImpl extends Pipe.SinkChannel implements SelChImpl {
   private static final NativeDispatcher nd = new FileDispatcherImpl();
   FileDescriptor fd;
   int fdVal;
   private volatile long thread = 0L;
   private final Object lock = new Object();
   private final Object stateLock = new Object();
   private static final int ST_UNINITIALIZED = -1;
   private static final int ST_INUSE = 0;
   private static final int ST_KILLED = 1;
   private volatile int state = -1;

   public FileDescriptor getFD() {
      return this.fd;
   }

   public int getFDVal() {
      return this.fdVal;
   }

   SinkChannelImpl(SelectorProvider var1, FileDescriptor var2) {
      super(var1);
      this.fd = var2;
      this.fdVal = IOUtil.fdVal(var2);
      this.state = 0;
   }

   protected void implCloseSelectableChannel() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 1) {
            nd.preClose(this.fd);
         }

         long var2 = this.thread;
         if (var2 != 0L) {
            NativeThread.signal(var2);
         }

         if (!this.isRegistered()) {
            this.kill();
         }

      }
   }

   public void kill() throws IOException {
      synchronized(this.stateLock) {
         if (this.state != 1) {
            if (this.state == -1) {
               this.state = 1;
            } else {
               assert !this.isOpen() && !this.isRegistered();

               nd.close(this.fd);
               this.state = 1;
            }
         }
      }
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      IOUtil.configureBlocking(this.fd, var1);
   }

   public boolean translateReadyOps(int var1, int var2, SelectionKeyImpl var3) {
      int var4 = var3.nioInterestOps();
      int var5 = var3.nioReadyOps();
      int var6 = var2;
      if ((var1 & Net.POLLNVAL) != 0) {
         throw new Error("POLLNVAL detected");
      } else if ((var1 & (Net.POLLERR | Net.POLLHUP)) != 0) {
         var3.nioReadyOps(var4);
         return (var4 & ~var5) != 0;
      } else {
         if ((var1 & Net.POLLOUT) != 0 && (var4 & 4) != 0) {
            var6 = var2 | 4;
         }

         var3.nioReadyOps(var6);
         return (var6 & ~var5) != 0;
      }
   }

   public boolean translateAndUpdateReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, var2.nioReadyOps(), var2);
   }

   public boolean translateAndSetReadyOps(int var1, SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, 0, var2);
   }

   public void translateAndSetInterestOps(int var1, SelectionKeyImpl var2) {
      if (var1 == 4) {
         var1 = Net.POLLOUT;
      }

      var2.selector.putEventOps(var2, var1);
   }

   private void ensureOpen() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      }
   }

   public int write(ByteBuffer var1) throws IOException {
      this.ensureOpen();
      synchronized(this.lock) {
         int var3 = 0;

         try {
            this.begin();
            if (!this.isOpen()) {
               byte var11 = 0;
               return var11;
            } else {
               this.thread = NativeThread.current();

               do {
                  var3 = IOUtil.write(this.fd, var1, -1L, nd);
               } while(var3 == -3 && this.isOpen());

               int var4 = IOStatus.normalize(var3);
               return var4;
            }
         } finally {
            this.thread = 0L;
            this.end(var3 > 0 || var3 == -2);

            assert IOStatus.check(var3);

         }
      }
   }

   public long write(ByteBuffer[] var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.ensureOpen();
         synchronized(this.lock) {
            long var3 = 0L;

            long var5;
            try {
               this.begin();
               if (this.isOpen()) {
                  this.thread = NativeThread.current();

                  do {
                     var3 = IOUtil.write(this.fd, var1, nd);
                  } while(var3 == -3L && this.isOpen());

                  var5 = IOStatus.normalize(var3);
                  return var5;
               }

               var5 = 0L;
            } finally {
               this.thread = 0L;
               this.end(var3 > 0L || var3 == -2L);

               assert IOStatus.check(var3);

            }

            return var5;
         }
      }
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         return this.write(Util.subsequence(var1, var2, var3));
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
