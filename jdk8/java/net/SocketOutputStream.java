package java.net;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import sun.net.ConnectionResetException;

class SocketOutputStream extends FileOutputStream {
   private AbstractPlainSocketImpl impl = null;
   private byte[] temp = new byte[1];
   private Socket socket = null;
   private boolean closing = false;

   SocketOutputStream(AbstractPlainSocketImpl var1) throws IOException {
      super(var1.getFileDescriptor());
      this.impl = var1;
      this.socket = var1.getSocket();
   }

   public final FileChannel getChannel() {
      return null;
   }

   private native void socketWrite0(FileDescriptor var1, byte[] var2, int var3, int var4) throws IOException;

   private void socketWrite(byte[] var1, int var2, int var3) throws IOException {
      if (var3 > 0 && var2 >= 0 && var3 <= var1.length - var2) {
         FileDescriptor var4 = this.impl.acquireFD();

         try {
            this.socketWrite0(var4, var1, var2, var3);
         } catch (SocketException var9) {
            SocketException var5 = var9;
            if (var9 instanceof ConnectionResetException) {
               this.impl.setConnectionResetPending();
               var5 = new SocketException("Connection reset");
            }

            if (this.impl.isClosedOrPending()) {
               throw new SocketException("Socket closed");
            }

            throw var5;
         } finally {
            this.impl.releaseFD();
         }

      } else if (var3 != 0) {
         throw new ArrayIndexOutOfBoundsException("len == " + var3 + " off == " + var2 + " buffer length == " + var1.length);
      }
   }

   public void write(int var1) throws IOException {
      this.temp[0] = (byte)var1;
      this.socketWrite(this.temp, 0, 1);
   }

   public void write(byte[] var1) throws IOException {
      this.socketWrite(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.socketWrite(var1, var2, var3);
   }

   public void close() throws IOException {
      if (!this.closing) {
         this.closing = true;
         if (this.socket != null) {
            if (!this.socket.isClosed()) {
               this.socket.close();
            }
         } else {
            this.impl.close();
         }

         this.closing = false;
      }
   }

   protected void finalize() {
   }

   private static native void init();

   static {
      init();
   }
}
