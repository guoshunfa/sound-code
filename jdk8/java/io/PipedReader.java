package java.io;

public class PipedReader extends Reader {
   boolean closedByWriter;
   boolean closedByReader;
   boolean connected;
   Thread readSide;
   Thread writeSide;
   private static final int DEFAULT_PIPE_SIZE = 1024;
   char[] buffer;
   int in;
   int out;

   public PipedReader(PipedWriter var1) throws IOException {
      this(var1, 1024);
   }

   public PipedReader(PipedWriter var1, int var2) throws IOException {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(var2);
      this.connect(var1);
   }

   public PipedReader() {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(1024);
   }

   public PipedReader(int var1) {
      this.closedByWriter = false;
      this.closedByReader = false;
      this.connected = false;
      this.in = -1;
      this.out = 0;
      this.initPipe(var1);
   }

   private void initPipe(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Pipe size <= 0");
      } else {
         this.buffer = new char[var1];
      }
   }

   public void connect(PipedWriter var1) throws IOException {
      var1.connect(this);
   }

   synchronized void receive(int var1) throws IOException {
      if (!this.connected) {
         throw new IOException("Pipe not connected");
      } else if (!this.closedByWriter && !this.closedByReader) {
         if (this.readSide != null && !this.readSide.isAlive()) {
            throw new IOException("Read end dead");
         } else {
            this.writeSide = Thread.currentThread();

            while(this.in == this.out) {
               if (this.readSide != null && !this.readSide.isAlive()) {
                  throw new IOException("Pipe broken");
               }

               this.notifyAll();

               try {
                  this.wait(1000L);
               } catch (InterruptedException var3) {
                  throw new InterruptedIOException();
               }
            }

            if (this.in < 0) {
               this.in = 0;
               this.out = 0;
            }

            this.buffer[this.in++] = (char)var1;
            if (this.in >= this.buffer.length) {
               this.in = 0;
            }

         }
      } else {
         throw new IOException("Pipe closed");
      }
   }

   synchronized void receive(char[] var1, int var2, int var3) throws IOException {
      while(true) {
         --var3;
         if (var3 < 0) {
            return;
         }

         this.receive(var1[var2++]);
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

         char var2 = this.buffer[this.out++];
         if (this.out >= this.buffer.length) {
            this.out = 0;
         }

         if (this.in == this.out) {
            this.in = -1;
         }

         return var2;
      }
   }

   public synchronized int read(char[] var1, int var2, int var3) throws IOException {
      if (!this.connected) {
         throw new IOException("Pipe not connected");
      } else if (this.closedByReader) {
         throw new IOException("Pipe closed");
      } else if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0) {
         throw new IOException("Write end dead");
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 == 0) {
            return 0;
         } else {
            int var4 = this.read();
            if (var4 < 0) {
               return -1;
            } else {
               var1[var2] = (char)var4;
               int var5 = 1;

               while(this.in >= 0) {
                  --var3;
                  if (var3 <= 0) {
                     break;
                  }

                  var1[var2 + var5] = this.buffer[this.out++];
                  ++var5;
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

   public synchronized boolean ready() throws IOException {
      if (!this.connected) {
         throw new IOException("Pipe not connected");
      } else if (this.closedByReader) {
         throw new IOException("Pipe closed");
      } else if (this.writeSide != null && !this.writeSide.isAlive() && !this.closedByWriter && this.in < 0) {
         throw new IOException("Write end dead");
      } else {
         return this.in >= 0;
      }
   }

   public void close() throws IOException {
      this.in = -1;
      this.closedByReader = true;
   }
}
