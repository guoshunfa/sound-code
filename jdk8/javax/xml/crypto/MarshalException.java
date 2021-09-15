package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class MarshalException extends Exception {
   private static final long serialVersionUID = -863185580332643547L;
   private Throwable cause;

   public MarshalException() {
   }

   public MarshalException(String var1) {
      super(var1);
   }

   public MarshalException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public MarshalException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.cause = var1;
   }

   public Throwable getCause() {
      return this.cause;
   }

   public void printStackTrace() {
      super.printStackTrace();
   }

   public void printStackTrace(PrintStream var1) {
      super.printStackTrace(var1);
   }

   public void printStackTrace(PrintWriter var1) {
      super.printStackTrace(var1);
   }
}
