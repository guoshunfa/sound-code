package java.sql;

public class SQLTransientException extends SQLException {
   private static final long serialVersionUID = -9042733978262274539L;

   public SQLTransientException() {
   }

   public SQLTransientException(String var1) {
      super(var1);
   }

   public SQLTransientException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLTransientException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLTransientException(Throwable var1) {
      super(var1);
   }

   public SQLTransientException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLTransientException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLTransientException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
