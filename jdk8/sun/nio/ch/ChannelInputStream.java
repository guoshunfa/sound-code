package sun.nio.ch;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectableChannel;

public class ChannelInputStream extends InputStream {
   protected final ReadableByteChannel ch;
   private ByteBuffer bb = null;
   private byte[] bs = null;
   private byte[] b1 = null;

   public static int read(ReadableByteChannel var0, ByteBuffer var1, boolean var2) throws IOException {
      if (var0 instanceof SelectableChannel) {
         SelectableChannel var3 = (SelectableChannel)var0;
         synchronized(var3.blockingLock()) {
            boolean var5 = var3.isBlocking();
            if (!var5) {
               throw new IllegalBlockingModeException();
            } else {
               if (var5 != var2) {
                  var3.configureBlocking(var2);
               }

               int var6 = var0.read(var1);
               if (var5 != var2) {
                  var3.configureBlocking(var5);
               }

               return var6;
            }
         }
      } else {
         return var0.read(var1);
      }
   }

   public ChannelInputStream(ReadableByteChannel var1) {
      this.ch = var1;
   }

   public synchronized int read() throws IOException {
      if (this.b1 == null) {
         this.b1 = new byte[1];
      }

      int var1 = this.read(this.b1);
      return var1 == 1 ? this.b1[0] & 255 : -1;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            ByteBuffer var4 = this.bs == var1 ? this.bb : ByteBuffer.wrap(var1);
            var4.limit(Math.min(var2 + var3, var4.capacity()));
            var4.position(var2);
            this.bb = var4;
            this.bs = var1;
            return this.read(var4);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected int read(ByteBuffer var1) throws IOException {
      return read(this.ch, var1, true);
   }

   public int available() throws IOException {
      if (this.ch instanceof SeekableByteChannel) {
         SeekableByteChannel var1 = (SeekableByteChannel)this.ch;
         long var2 = Math.max(0L, var1.size() - var1.position());
         return var2 > 2147483647L ? Integer.MAX_VALUE : (int)var2;
      } else {
         return 0;
      }
   }

   public void close() throws IOException {
      this.ch.close();
   }
}
