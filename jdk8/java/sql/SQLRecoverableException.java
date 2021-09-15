package java.sql;

public class SQLRecoverableException extends SQLException {
   private static final long serialVersionUID = -4144386502923131579L;

   public SQLRecoverableException() {
   }

   public SQLRecoverableException(String var1) {
      super(var1);
   }

   public SQLRecoverableException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLRecoverableException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLRecoverableException(Throwable var1) {
      super(var1);
   }

   public SQLRecoverableException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLRecoverableException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLRecoverableException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
