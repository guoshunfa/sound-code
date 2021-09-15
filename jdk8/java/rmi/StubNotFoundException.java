package java.rmi;

public class StubNotFoundException extends RemoteException {
   private static final long serialVersionUID = -7088199405468872373L;

   public StubNotFoundException(String var1) {
      super(var1);
   }

   public StubNotFoundException(String var1, Exception var2) {
      super(var1, var2);
   }
}
