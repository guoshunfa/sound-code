package sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class MultiplexInputStream extends InputStream {
   private ConnectionMultiplexer manager;
   private MultiplexConnectionInfo info;
   private byte[] buffer;
   private int present = 0;
   private int pos = 0;
   private int requested = 0;
   private boolean disconnected = false;
   private Object lock = new Object();
   private int waterMark;
   private byte[] temp = new byte[1];

   MultiplexInputStream(ConnectionMultiplexer var1, MultiplexConnectionInfo var2, int var3) {
      this.manager = var1;
      this.info = var2;
      this.buffer = new byte[var3];
      this.waterMark = var3 / 2;
   }

   public synchronized int read() throws IOException {
      int var1 = this.read(this.temp, 0, 1);
      return var1 != 1 ? -1 : this.temp[0] & 255;
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 <= 0) {
         return 0;
      } else {
         int var4;
         int var6;
         synchronized(this.lock) {
            if (this.pos >= this.present) {
               this.pos = this.present = 0;
            } else if (this.pos >= this.waterMark) {
               System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
               this.present -= this.pos;
               this.pos = 0;
            }

            var6 = this.buffer.length - this.present;
            var4 = Math.max(var6 - this.requested, 0);
         }

         if (var4 > 0) {
            this.manager.sendRequest(this.info, var4);
         }

         synchronized(this.lock) {
            this.requested += var4;

            while(this.pos >= this.present && !this.disconnected) {
               try {
                  this.lock.wait();
               } catch (InterruptedException var9) {
               }
            }

            if (this.disconnected && this.pos >= this.present) {
               return -1;
            } else {
               var6 = this.present - this.pos;
               if (var3 < var6) {
                  System.arraycopy(this.buffer, this.pos, var1, var2, var3);
                  this.pos += var3;
                  return var3;
               } else {
                  System.arraycopy(this.buffer, this.pos, var1, var2, var6);
                  this.pos = this.present = 0;
                  return var6;
               }
            }
         }
      }
   }

   public int available() throws IOException {
      synchronized(this.lock) {
         return this.present - this.pos;
      }
   }

   public void close() throws IOException {
      this.manager.sendClose(this.info);
   }

   void receive(int var1, DataInputStream var2) throws IOException {
      synchronized(this.lock) {
         if (this.pos > 0 && this.buffer.length - this.present < var1) {
            System.arraycopy(this.buffer, this.pos, this.buffer, 0, this.present - this.pos);
            this.present -= this.pos;
            this.pos = 0;
         }

         if (this.buffer.length - this.present < var1) {
            throw new IOException("Receive buffer overflow");
         } else {
            var2.readFully(this.buffer, this.present, var1);
            this.present += var1;
            this.requested -= var1;
            this.lock.notifyAll();
         }
      }
   }

   void disconnect() {
      synchronized(this.lock) {
         this.disconnected = true;
         this.lock.notifyAll();
      }
   }
}
