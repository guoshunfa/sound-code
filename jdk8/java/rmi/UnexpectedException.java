package java.rmi;

public class UnexpectedException extends RemoteException {
   private static final long serialVersionUID = 1800467484195073863L;

   public UnexpectedException(String var1) {
      super(var1);
   }

   public UnexpectedException(String var1, Exception var2) {
      super(var1, var2);
   }
}
