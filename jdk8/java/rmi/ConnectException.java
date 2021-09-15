package java.rmi;

public class ConnectException extends RemoteException {
   private static final long serialVersionUID = 4863550261346652506L;

   public ConnectException(String var1) {
      super(var1);
   }

   public ConnectException(String var1, Exception var2) {
      super(var1, var2);
   }
}
