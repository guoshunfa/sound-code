package java.net;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import sun.net.ConnectionResetException;

class SocketInputStream extends FileInputStream {
   private boolean eof;
   private AbstractPlainSocketImpl impl = null;
   private byte[] temp;
   private Socket socket = null;
   private boolean closing = false;

   SocketInputStream(AbstractPlainSocketImpl var1) throws IOException {
      super(var1.getFileDescriptor());
      this.impl = var1;
      this.socket = var1.getSocket();
   }

   public final FileChannel getChannel() {
      return null;
   }

   private native int socketRead0(FileDescriptor var1, byte[] var2, int var3, int var4, int var5) throws IOException;

   private int socketRead(FileDescriptor var1, byte[] var2, int var3, int var4, int var5) throws IOException {
      return this.socketRead0(var1, var2, var3, var4, var5);
   }

   public int read(byte[] var1) throws IOException {
      return this.read(var1, 0, var1.length);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.read(var1, var2, var3, this.impl.getTimeout());
   }

   int read(byte[] var1, int var2, int var3, int var4) throws IOException {
      if (this.eof) {
         return -1;
      } else if (this.impl.isConnectionReset()) {
         throw new SocketException("Connection reset");
      } else if (var3 > 0 && var2 >= 0 && var3 <= var1.length - var2) {
         boolean var6 = false;
         FileDescriptor var7 = this.impl.acquireFD();

         int var5;
         int var8;
         label236: {
            try {
               var5 = this.socketRead(var7, var1, var2, var3, var4);
               if (var5 <= 0) {
                  break label236;
               }

               var8 = var5;
            } catch (ConnectionResetException var21) {
               var6 = true;
               break label236;
            } finally {
               this.impl.releaseFD();
            }

            return var8;
         }

         if (var6) {
            label251: {
               this.impl.setConnectionResetPending();
               this.impl.acquireFD();

               try {
                  var5 = this.socketRead(var7, var1, var2, var3, var4);
                  if (var5 <= 0) {
                     break label251;
                  }

                  var8 = var5;
               } catch (ConnectionResetException var19) {
                  break label251;
               } finally {
                  this.impl.releaseFD();
               }

               return var8;
            }
         }

         if (this.impl.isClosedOrPending()) {
            throw new SocketException("Socket closed");
         } else {
            if (this.impl.isConnectionResetPending()) {
               this.impl.setConnectionReset();
            }

            if (this.impl.isConnectionReset()) {
               throw new SocketException("Connection reset");
            } else {
               this.eof = true;
               return -1;
            }
         }
      } else if (var3 == 0) {
         return 0;
      } else {
         throw new ArrayIndexOutOfBoundsException("length == " + var3 + " off == " + var2 + " buffer length == " + var1.length);
      }
   }

   public int read() throws IOException {
      if (this.eof) {
         return -1;
      } else {
         this.temp = new byte[1];
         int var1 = this.read(this.temp, 0, 1);
         return var1 <= 0 ? -1 : this.temp[0] & 255;
      }
   }

   public long skip(long var1) throws IOException {
      if (var1 <= 0L) {
         return 0L;
      } else {
         long var3 = var1;
         int var5 = (int)Math.min(1024L, var1);

         int var7;
         for(byte[] var6 = new byte[var5]; var3 > 0L; var3 -= (long)var7) {
            var7 = this.read(var6, 0, (int)Math.min((long)var5, var3));
            if (var7 < 0) {
               break;
            }
         }

         return var1 - var3;
      }
   }

   public int available() throws IOException {
      return this.impl.available();
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

   void setEOF(boolean var1) {
      this.eof = var1;
   }

   protected void finalize() {
   }

   private static native void init();

   static {
      init();
   }
}
