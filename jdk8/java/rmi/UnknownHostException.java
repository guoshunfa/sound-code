package java.rmi;

public class UnknownHostException extends RemoteException {
   private static final long serialVersionUID = -8152710247442114228L;

   public UnknownHostException(String var1) {
      super(var1);
   }

   public UnknownHostException(String var1, Exception var2) {
      super(var1, var2);
   }
}
