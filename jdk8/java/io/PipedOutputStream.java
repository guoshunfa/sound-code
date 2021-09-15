package java.io;

public class PipedOutputStream extends OutputStream {
   private PipedInputStream sink;

   public PipedOutputStream(PipedInputStream var1) throws IOException {
      this.connect(var1);
   }

   public PipedOutputStream() {
   }

   public synchronized void connect(PipedInputStream var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.sink == null && !var1.connected) {
         this.sink = var1;
         var1.in = -1;
         var1.out = 0;
         var1.connected = true;
      } else {
         throw new IOException("Already connected");
      }
   }

   public void write(int var1) throws IOException {
      if (this.sink == null) {
         throw new IOException("Pipe not connected");
      } else {
         this.sink.receive(var1);
      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.sink == null) {
         throw new IOException("Pipe not connected");
      } else if (var1 == null) {
         throw new NullPointerException();
      } else if (var2 >= 0 && var2 <= var1.length && var3 >= 0 && var2 + var3 <= var1.length && var2 + var3 >= 0) {
         if (var3 != 0) {
            this.sink.receive(var1, var2, var3);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public synchronized void flush() throws IOException {
      if (this.sink != null) {
         synchronized(this.sink) {
            this.sink.notifyAll();
         }
      }

   }

   public void close() throws IOException {
      if (this.sink != null) {
         this.sink.receivedLast();
      }

   }
}
