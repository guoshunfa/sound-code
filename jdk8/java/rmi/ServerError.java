package java.rmi;

public class ServerError extends RemoteException {
   private static final long serialVersionUID = 8455284893909696482L;

   public ServerError(String var1, Error var2) {
      super(var1, var2);
   }
}
