package java.rmi.activation;

import java.rmi.RemoteException;

public class ActivateFailedException extends RemoteException {
   private static final long serialVersionUID = 4863550261346652506L;

   public ActivateFailedException(String var1) {
      super(var1);
   }

   public ActivateFailedException(String var1, Exception var2) {
      super(var1, var2);
   }
}
