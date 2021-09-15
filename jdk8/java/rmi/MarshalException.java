package java.rmi;

public class MarshalException extends RemoteException {
   private static final long serialVersionUID = 6223554758134037936L;

   public MarshalException(String var1) {
      super(var1);
   }

   public MarshalException(String var1, Exception var2) {
      super(var1, var2);
   }
}
