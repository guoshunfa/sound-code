package java.sql;

public class SQLTransientConnectionException extends SQLTransientException {
   private static final long serialVersionUID = -2520155553543391200L;

   public SQLTransientConnectionException() {
   }

   public SQLTransientConnectionException(String var1) {
      super(var1);
   }

   public SQLTransientConnectionException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLTransientConnectionException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLTransientConnectionException(Throwable var1) {
      super(var1);
   }

   public SQLTransientConnectionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLTransientConnectionException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLTransientConnectionException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
