package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockImpl extends FileLock {
   private volatile boolean valid = true;

   FileLockImpl(FileChannel var1, long var2, long var4, boolean var6) {
      super(var1, var2, var4, var6);
   }

   FileLockImpl(AsynchronousFileChannel var1, long var2, long var4, boolean var6) {
      super(var1, var2, var4, var6);
   }

   public boolean isValid() {
      return this.valid;
   }

   void invalidate() {
      assert Thread.holdsLock(this);

      this.valid = false;
   }

   public synchronized void release() throws IOException {
      Channel var1 = this.acquiredBy();
      if (!var1.isOpen()) {
         throw new ClosedChannelException();
      } else {
         if (this.valid) {
            if (var1 instanceof FileChannelImpl) {
               ((FileChannelImpl)var1).release(this);
            } else {
               if (!(var1 instanceof AsynchronousFileChannelImpl)) {
                  throw new AssertionError();
               }

               ((AsynchronousFileChannelImpl)var1).release(this);
            }

            this.valid = false;
         }

      }
   }
}
