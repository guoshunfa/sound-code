package java.sql;

public class SQLFeatureNotSupportedException extends SQLNonTransientException {
   private static final long serialVersionUID = -1026510870282316051L;

   public SQLFeatureNotSupportedException() {
   }

   public SQLFeatureNotSupportedException(String var1) {
      super(var1);
   }

   public SQLFeatureNotSupportedException(String var1, String var2) {
      super(var1, var2);
   }

   public SQLFeatureNotSupportedException(String var1, String var2, int var3) {
      super(var1, var2, var3);
   }

   public SQLFeatureNotSupportedException(Throwable var1) {
      super(var1);
   }

   public SQLFeatureNotSupportedException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SQLFeatureNotSupportedException(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
   }

   public SQLFeatureNotSupportedException(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
   }
}
