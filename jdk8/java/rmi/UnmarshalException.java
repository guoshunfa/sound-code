package java.rmi;

public class UnmarshalException extends RemoteException {
   private static final long serialVersionUID = 594380845140740218L;

   public UnmarshalException(String var1) {
      super(var1);
   }

   public UnmarshalException(String var1, Exception var2) {
      super(var1, var2);
   }
}
