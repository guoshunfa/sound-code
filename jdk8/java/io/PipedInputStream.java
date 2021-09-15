package java.io;

public class PipedInputStream extends InputStream {
   boolean closedByWriter;
   volatile boolean closedByReader;
   boolean connected;
   Thread readSide;
   Thread writeSide;
   private static final int DEFAULT_PIPE_SIZE = 1024;
   protected static final int PIPE_SIZE = 1024;
   protected byte[] buffer;
   protected int in;
   protected int out;

   public PipedInputStream(PipedOutputStream var1) throws IOException {
      this(var1, 1024);
   }

   public PipedInputStream(PipedOutputStream var1, int var2) throws IOException {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(var2);
      this.connect(var1);
   }

   public PipedInputStream() {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(1024);
   }

   public PipedInputStream(int var1) {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(var1);
   }

   private void initPipe(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Pipe Size <= 0");
      } else {
         this.buffer = new byte[var1];
      }
   }

   public void connect(PipedOutputStream var1) throws IOException {
      var1.connect(this);
   }

   protected synchronized void receive(int var1) throws IOException {
      this.checkStateForReceive();
      this.writeSide = Thread.currentThread();
      if (this.in == this.out) {
         this.awaitSpace();
      }

      if (this.in < 0) {
         this.in = 0;
         this.out = 0;
      }

      this.buffer[this.in++] = (byte)(var1 & 255);
      if (this.in >= this.buffer.length) {
         this.in = 0;
      }

   }

   synchronized void receive(byte[] var1, int var2, int var3) throws IOException {
      this.checkStateForReceive();
      this.writeSide = Thread.currentThread();
      int var4 = var3;

      while(var4 > 0) {
         if (this.in == this.out) {
            this.awaitSpace();
         }

         int var5 = 0;
         if (this.out < this.in) {
            var5 = this.buffer.length - this.in;
         } else if (this.in < this.out) {
            if (this.in == -1) {
               this.in = this.out = 0;
               var5 = this.buffer.length - this.in;
            } else {
               var5 = this.out - this.in;
            }
         }

         if (var5 > var4) {
            var5 = var4;
         }

         assert var5 > 0;

         System.arraycopy(var1, var2, this.buffer, this.in, var5);
         var4 -= var5;
         var2 += var5;
         this.in += var5;
         if (this.in >= this.buffer.length) {
            this.in = 0;
         }
      }

   }

   private void checkStateForReceive() throws IOException {
      if (!this.connected) {
         throw new IOException("Pipe not connected");
      } else if (!this.closedByWriter && !this.closedByReader) {
         if (this.readSide != null && !this.readSide.isAlive()) {
            throw new IOException("Read end dead");
         }
      } else {
         throw new IOException("Pipe closed");
      }
   }

   private void awaitSpace() throws IOException {
      while(this.in == this.out) {
         this.checkStateForReceive();
         this.notifyAll();

         try {
            this.wait(1000L);
         } catch (InterruptedException var2) {
            throw new InterruptedIOException();
         }
      }

   }

   synchronized void receivedLast() {
      this.closedByWriter = true;
      this.notifyAll();
   }

   public synchronized int read() throws IOException {
      if (!this.connected) {
         throw new IOException("Pipe not connected");
      } else if (this.closedByReader) {
         throw new IOException("Pipe closed");
      } else if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0) {
         throw new IOException("Write end dead");
      } else {
         this.readSide = Thread.currentThread();
         int var1 = 2;

         while(this.in < 0) {
            if (this.closedByWriter) {
               return -1;
            }

            if (this.writeSide != null && !this.writeSide.isAlive()) {
               --var1;
               if (var1 < 0) {
                  throw new IOException("Pipe broken");
               }
            }

            this.notifyAll();

            try {
               this.wait(1000L);
            } catch (InterruptedException var3) {
               throw new InterruptedIOException();
            }
         }

         int var2 = this.buffer[this.out++] & 255;
         if (this.out >= this.buffer.length) {
            this.out = 0;
         }

         if (this.in == this.out) {
            this.in = -1;
         }

         return var2;
      }
   }

   public synchronized int read(byte[] var1, int var2, int var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var3 >= 0 && var3 <= var1.length - var2) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.read();
            if (var4 < 0) {
               return -1;
            } else {
               var1[var2] = (byte)var4;
               int var5 = 1;

               while(this.in >= 0 && var3 > 1) {
                  int var6;
                  if (this.in > this.out) {
                     var6 = Math.min(this.buffer.length - this.out, this.in - this.out);
                  } else {
                     var6 = this.buffer.length - this.out;
                  }

                  if (var6 > var3 - 1) {
                     var6 = var3 - 1;
                  }

                  System.arraycopy(this.buffer, this.out, var1, var2 + var5, var6);
                  this.out += var6;
                  var5 += var6;
                  var3 -= var6;
                  if (this.out >= this.buffer.length) {
                     this.out = 0;
                  }

                  if (this.in == this.out) {
                     this.in = -1;
                  }
               }

               return var5;
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized int available() throws IOException {
      if (this.in < 0) {
         return 0;
      } else if (this.in == this.out) {
         return this.buffer.length;
      } else {
         return this.in > this.out ? this.in - this.out : this.in + this.buffer.length - this.out;
      }
   }

   public void close() throws IOException {
      this.closedByReader = true;
      synchronized(this) {
         this.in = -1;
      }
   }
}
