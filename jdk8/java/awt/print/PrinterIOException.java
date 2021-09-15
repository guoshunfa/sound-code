package java.awt.print;

import java.io.IOException;

public class PrinterIOException extends PrinterException {
   static final long serialVersionUID = 5850870712125932846L;
   private IOException mException;

   public PrinterIOException(IOException var1) {
      this.initCause((Throwable)null);
      this.mException = var1;
   }

   public IOException getIOException() {
      return this.mException;
   }

   public Throwable getCause() {
      return this.mException;
   }
}
