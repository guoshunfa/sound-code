package java.rmi.server;

import java.rmi.RemoteException;

public class ExportException extends RemoteException {
   private static final long serialVersionUID = -9155485338494060170L;

   public ExportException(String var1) {
      super(var1);
   }

   public ExportException(String var1, Exception var2) {
      super(var1, var2);
   }
}
