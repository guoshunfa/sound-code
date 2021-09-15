package javax.xml.crypto.dsig;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XMLSignatureException extends Exception {
   private static final long serialVersionUID = -3438102491013869995L;
   private Throwable cause;

   public XMLSignatureException() {
   }

   public XMLSignatureException(String var1) {
      super(var1);
   }

   public XMLSignatureException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public XMLSignatureException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.cause = var1;
   }

   public Throwable getCause() {
      return this.cause;
   }

   public void printStackTrace() {
      super.printStackTrace();
      if (this.cause != null) {
         this.cause.printStackTrace();
      }

   }

   public void printStackTrace(PrintStream var1) {
      super.printStackTrace(var1);
      if (this.cause != null) {
         this.cause.printStackTrace(var1);
      }

   }

   public void printStackTrace(PrintWriter var1) {
      super.printStackTrace(var1);
      if (this.cause != null) {
         this.cause.printStackTrace(var1);
      }

   }
}
