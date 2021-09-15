package java.sql;

public class SQLDataException extends SQLNonTransientException {
   private static final long serialVersionUID = -6889123282670549800L;

   public SQLDataException() {
   }

   public SQLDataException(String var1) {
      super(var1);
   }

   public SQLDataException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLDataException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLDataException(Throwable var1) {
      super(var1);
   }

   public SQLDataException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLDataException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLDataException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
