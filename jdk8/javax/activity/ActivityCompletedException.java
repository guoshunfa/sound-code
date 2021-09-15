package javax.activity;

import java.rmi.RemoteException;

public class ActivityCompletedException extends RemoteException {
   public ActivityCompletedException() {
   }

   public ActivityCompletedException(String var1) {
      super(var1);
   }

   public ActivityCompletedException(Throwable var1) {
      this("", var1);
   }

   public ActivityCompletedException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
