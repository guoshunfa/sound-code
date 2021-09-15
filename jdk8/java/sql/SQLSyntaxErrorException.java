package java.sql;

public class SQLSyntaxErrorException extends SQLNonTransientException {
   private static final long serialVersionUID = -1843832610477496053L;

   public SQLSyntaxErrorException() {
   }

   public SQLSyntaxErrorException(String var1) {
      super(var1);
   }

   public SQLSyntaxErrorException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLSyntaxErrorException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLSyntaxErrorException(Throwable var1) {
      super(var1);
   }

   public SQLSyntaxErrorException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLSyntaxErrorException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLSyntaxErrorException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
