package java.rmi;

public class ServerException extends RemoteException {
   private static final long serialVersionUID = -4775845313121906682L;

   public ServerException(String var1) {
      super(var1);
   }

   public ServerException(String var1, Exception var2) {
      super(var1, var2);
   }
}
