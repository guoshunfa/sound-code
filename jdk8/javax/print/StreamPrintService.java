package javax.print;

import java.io.OutputStream;

public abstract class StreamPrintService implements PrintService {
   private OutputStream outStream;
   private boolean disposed = false;

   private StreamPrintService() {
   }

   protected StreamPrintService(OutputStream var1) {
      this.outStream = var1;
   }

   public OutputStream getOutputStream() {
      return this.outStream;
   }

   public abstract String getOutputFormat();

   public void dispose() {
      this.disposed = true;
   }

   public boolean isDisposed() {
      return this.disposed;
   }
}
