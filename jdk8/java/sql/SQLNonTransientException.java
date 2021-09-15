package java.sql;

public class SQLNonTransientException extends SQLException {
   private static final long serialVersionUID = -9104382843534716547L;

   public SQLNonTransientException() {
   }

   public SQLNonTransientException(String var1) {
      super(var1);
   }

   public SQLNonTransientException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLNonTransientException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLNonTransientException(Throwable var1) {
      super(var1);
   }

   public SQLNonTransientException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLNonTransientException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLNonTransientException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
