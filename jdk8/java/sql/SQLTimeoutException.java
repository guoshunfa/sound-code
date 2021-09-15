package java.sql;

public class SQLTimeoutException extends SQLTransientException {
   private static final long serialVersionUID = -4487171280562520262L;

   public SQLTimeoutException() {
   }

   public SQLTimeoutException(String var1) {
      super(var1);
   }

   public SQLTimeoutException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLTimeoutException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLTimeoutException(Throwable var1) {
      super(var1);
   }

   public SQLTimeoutException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLTimeoutException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLTimeoutException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
