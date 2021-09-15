package java.rmi;

import java.io.IOException;

public class RemoteException extends IOException {
   private static final long serialVersionUID = -5148567311918794206L;
   public Throwable detail;

   public RemoteException() {
      this.initCause((Throwable)null);
   }

   public RemoteException(String var1) {
      super(var1);
      this.initCause((Throwable)null);
   }

   public RemoteException(String var1, Throwable var2) {
      super(var1);
      this.initCause((Throwable)null);
      this.detail = var2;
   }

   public String getMessage() {
      return this.detail == null ? super.getMessage() : super.getMessage() + "; nested exception is: \n\t" + this.detail.toString();
   }

   public Throwable getCause() {
      return this.detail;
   }
}
