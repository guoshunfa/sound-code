package javax.management;

public class BadBinaryOpValueExpException extends Exception {
   private static final long serialVersionUID = 5068475589449021227L;
   private ValueExp exp;

   public BadBinaryOpValueExpException(ValueExp var1) {
      this.exp = var1;
   }

   public ValueExp getExp() {
      return this.exp;
   }

   public String toString() {
      return "BadBinaryOpValueExpException: " + this.exp;
   }
}
