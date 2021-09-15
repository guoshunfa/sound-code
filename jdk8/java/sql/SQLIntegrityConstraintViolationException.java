package java.sql;

public class SQLIntegrityConstraintViolationException extends SQLNonTransientException {
   private static final long serialVersionUID = 8033405298774849169L;

   public SQLIntegrityConstraintViolationException() {
   }

   public SQLIntegrityConstraintViolationException(String var1) {
      super(var1);
   }

   public SQLIntegrityConstraintViolationException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLIntegrityConstraintViolationException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLIntegrityConstraintViolationException(Throwable var1) {
      super(var1);
   }

   public SQLIntegrityConstraintViolationException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLIntegrityConstraintViolationException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLIntegrityConstraintViolationException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
