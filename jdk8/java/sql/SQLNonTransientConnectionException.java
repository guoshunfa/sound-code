package java.sql;

public class SQLNonTransientConnectionException extends SQLNonTransientException {
   private static final long serialVersionUID = -5852318857474782892L;

   public SQLNonTransientConnectionException() {
   }

   public SQLNonTransientConnectionException(String var1) {
      super(var1);
   }

   public SQLNonTransientConnectionException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLNonTransientConnectionException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLNonTransientConnectionException(Throwable var1) {
      super(var1);
   }

   public SQLNonTransientConnectionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLNonTransientConnectionException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLNonTransientConnectionException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
