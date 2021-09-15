package java.rmi;

public class ConnectIOException extends RemoteException {
   private static final long serialVersionUID = -8087809532704668744L;

   public ConnectIOException(String var1) {
      super(var1);
   }

   public ConnectIOException(String var1, Exception var2) {
      super(var1, var2);
   }
}
