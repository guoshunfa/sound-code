package javax.security.sasl;

import java.io.IOException;

public class SaslException extends IOException {
   private Throwable _exception;
   private static final long serialVersionUID = 4579784287983423626L;

   public SaslException() {
   }

   public SaslException(String var1) {
      super(var1);
   }

   public SaslException(String var1, Throwable var2) {
      super(var1);
      if (var2 != null) {
         this.initCause(var2);
      }

   }

   public Throwable getCause() {
      return this._exception;
   }

   public Throwable initCause(Throwable var1) {
      super.initCause(var1);
      this._exception = var1;
      return this;
   }

   public String toString() {
      String var1 = super.toString();
      if (this._exception != null && this._exception != this) {
         var1 = var1 + " [Caused by " + this._exception.toString() + "]";
      }

      return var1;
   }
}
