package javax.management;

public class JMRuntimeException extends RuntimeException {
   private static final long serialVersionUID = 6573344628407841861L;

   public JMRuntimeException() {
   }

   public JMRuntimeException(String var1) {
      super(var1);
   }

   JMRuntimeException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
