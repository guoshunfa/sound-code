package java.sql;

public class SQLTransactionRollbackException extends SQLTransientException {
   private static final long serialVersionUID = 5246680841170837229L;

   public SQLTransactionRollbackException() {
   }

   public SQLTransactionRollbackException(String var1) {
      super(var1);
   }

   public SQLTransactionRollbackException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLTransactionRollbackException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLTransactionRollbackException(Throwable var1) {
      super(var1);
   }

   public SQLTransactionRollbackException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLTransactionRollbackException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLTransactionRollbackException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
