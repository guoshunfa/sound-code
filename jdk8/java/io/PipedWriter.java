package java.io;

public class PipedWriter extends Writer {
   private PipedReader sink;
   private boolean closed = false;

   public PipedWriter(PipedReader var1) throws IOException {
      this.connect(var1);
   }

   public PipedWriter() {
   }

   public synchronized void connect(PipedReader var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.sink == null && !var1.connected) {
         if (!var1.closedByReader && !this.closed) {
            this.sink = var1;
            var1.in = -1;
            var1.out = 0;
            var1.connected = true;
         } else {
            throw new IOException("Pipe closed");
         }
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

   public void write(char[] var1, int var2, int var3) throws IOException {
      if (this.sink == null) {
         throw new IOException("Pipe not connected");
      } else if ((var2 | var3 | var2 + var3 | var1.length - (var2 + var3)) < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         this.sink.receive(var1, var2, var3);
      }
   }

   public synchronized void flush() throws IOException {
      if (this.sink != null) {
         if (this.sink.closedByReader || this.closed) {
            throw new IOException("Pipe closed");
         }

         synchronized(this.sink) {
            this.sink.notifyAll();
         }
      }

   }

   public void close() throws IOException {
      this.closed = true;
      if (this.sink != null) {
         this.sink.receivedLast();
      }

   }
}
