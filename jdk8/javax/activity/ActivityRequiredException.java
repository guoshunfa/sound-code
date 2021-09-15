package javax.activity;

import java.rmi.RemoteException;

public class ActivityRequiredException extends RemoteException {
   public ActivityRequiredException() {
   }

   public ActivityRequiredException(String var1) {
      super(var1);
   }

   public ActivityRequiredException(Throwable var1) {
      this("", var1);
   }

   public ActivityRequiredException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
