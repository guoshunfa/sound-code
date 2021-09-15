package java.rmi;

public class AccessException extends RemoteException {
   private static final long serialVersionUID = 6314925228044966088L;

   public AccessException(String var1) {
      super(var1);
   }

   public AccessException(String var1, Exception var2) {
      super(var1, var2);
   }
}
