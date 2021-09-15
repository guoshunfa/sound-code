package javax.activity;

import java.rmi.RemoteException;

public class InvalidActivityException extends RemoteException {
   public InvalidActivityException() {
   }

   public InvalidActivityException(String var1) {
      super(var1);
   }

   public InvalidActivityException(Throwable var1) {
      this("", var1);
   }

   public InvalidActivityException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
