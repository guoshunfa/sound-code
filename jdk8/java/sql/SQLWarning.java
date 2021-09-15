package java.sql;

public class SQLWarning extends SQLException {
   private static final long serialVersionUID = 3917336774604784856L;

   public SQLWarning(String var1, String var2, int var3) {
      super(var1, var2, var3);
      DriverManager.println("SQLWarning: reason(" + var1 + ") SQLState(" + var2 + ") vendor code(" + var3 + ")");
   }

   public SQLWarning(String var1, String var2) {
      super(var1, var2);
      DriverManager.println("SQLWarning: reason(" + var1 + ") SQLState(" + var2 + ")");
   }

   public SQLWarning(String var1) {
      super(var1);
      DriverManager.println("SQLWarning: reason(" + var1 + ")");
   }

   public SQLWarning() {
      DriverManager.println("SQLWarning: ");
   }

   public SQLWarning(Throwable var1) {
      super(var1);
      DriverManager.println("SQLWarning");
   }

   public SQLWarning(String var1, Throwable var2) {
      super(var1, var2);
      DriverManager.println("SQLWarning : reason(" + var1 + ")");
   }

   public SQLWarning(String var1, String var2, Throwable var3) {
      super(var1, var2, var3);
      DriverManager.println("SQLWarning: reason(" + var1 + ") SQLState(" + var2 + ")");
   }

   public SQLWarning(String var1, String var2, int var3, Throwable var4) {
      super(var1, var2, var3, var4);
      DriverManager.println("SQLWarning: reason(" + var1 + ") SQLState(" + var2 + ") vendor code(" + var3 + ")");
   }

   public SQLWarning getNextWarning() {
      try {
         return (SQLWarning)this.getNextException();
      } catch (ClassCastException var2) {
         throw new Error("SQLWarning chain holds value that is not a SQLWarning");
      }
   }

   public void setNextWarning(SQLWarning var1) {
      this.setNextException(var1);
   }
}
