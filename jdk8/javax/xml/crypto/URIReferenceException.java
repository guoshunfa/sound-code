package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class URIReferenceException extends Exception {
   private static final long serialVersionUID = 7173469703932561419L;
   private Throwable cause;
   private URIReference uriReference;

   public URIReferenceException() {
   }

   public URIReferenceException(String var1) {
      super(var1);
   }

   public URIReferenceException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public URIReferenceException(String var1, Throwable var2, URIReference var3) {
      this(var1, var2);
      if (var3 == null) {
         throw new NullPointerException("uriReference cannot be null");
      } else {
         this.uriReference = var3;
      }
   }

   public URIReferenceException(Throwable var1) {
      super(var1 == null ? null : var1.toString());
      this.cause = var1;
   }

   public URIReference getURIReference() {
      return this.uriReference;
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
