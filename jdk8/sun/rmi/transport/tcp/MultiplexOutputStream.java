package sun.rmi.transport.tcp;

import java.io.IOException;
import java.io.OutputStream;

final class MultiplexOutputStream extends OutputStream {
   private ConnectionMultiplexer manager;
   private MultiplexConnectionInfo info;
   private byte[] buffer;
   private int pos = 0;
   private int requested = 0;
   private boolean disconnected = false;
   private Object lock = new Object();

   MultiplexOutputStream(ConnectionMultiplexer var1, MultiplexConnectionInfo var2, int var3) {
      this.manager = var1;
      this.info = var2;
      this.buffer = new byte[var3];
      this.pos = 0;
   }

   public synchronized void write(int var1) throws IOException {
      while(this.pos >= this.buffer.length) {
         this.push();
      }

      this.buffer[this.pos++] = (byte)var1;
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 > 0) {
         int var4 = this.buffer.length - this.pos;
         if (var3 <= var4) {
            System.arraycopy(var1, var2, this.buffer, this.pos, var3);
            this.pos += var3;
         } else {
            this.flush();

            while(true) {
               int var5;
               synchronized(this.lock) {
                  while((var5 = this.requested) < 1 && !this.disconnected) {
                     try {
                        this.lock.wait();
                     } catch (InterruptedException var12) {
                     }
                  }

                  if (this.disconnected) {
                     throw new IOException("Connection closed");
                  }
               }

               if (var5 >= var3) {
                  this.manager.sendTransmit(this.info, var1, var2, var3);
                  synchronized(this.lock) {
                     this.requested -= var3;
                     return;
                  }
               }

               this.manager.sendTransmit(this.info, var1, var2, var5);
               var2 += var5;
               var3 -= var5;
               synchronized(this.lock) {
                  this.requested -= var5;
               }
            }
         }
      }
   }

   public synchronized void flush() throws IOException {
      while(this.pos > 0) {
         this.push();
      }

   }

   public void close() throws IOException {
      this.manager.sendClose(this.info);
   }

   void request(int var1) {
      synchronized(this.lock) {
         this.requested += var1;
         this.lock.notifyAll();
      }
   }

   void disconnect() {
      synchronized(this.lock) {
         this.disconnected = true;
         this.lock.notifyAll();
      }
   }

   private void push() throws IOException {
      int var1;
      synchronized(this.lock) {
         while((var1 = this.requested) < 1 && !this.disconnected) {
            try {
               this.lock.wait();
            } catch (InterruptedException var9) {
            }
         }

         if (this.disconnected) {
            throw new IOException("Connection closed");
         }
      }

      if (var1 < this.pos) {
         this.manager.sendTransmit(this.info, this.buffer, 0, var1);
         System.arraycopy(this.buffer, var1, this.buffer, 0, this.pos - var1);
         this.pos -= var1;
         synchronized(this.lock) {
            this.requested -= var1;
         }
      } else {
         this.manager.sendTransmit(this.info, this.buffer, 0, this.pos);
         synchronized(this.lock) {
            this.requested -= this.pos;
         }

         this.pos = 0;
      }

   }
}
