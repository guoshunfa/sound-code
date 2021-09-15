package java.util;

public class IllegalFormatPrecisionException extends IllegalFormatException {
   private static final long serialVersionUID = 18711008L;
   private int p;

   public IllegalFormatPrecisionException(int var1) {
      this.p = var1;
   }

   public int getPrecision() {
      return this.p;
   }

   public String getMessage() {
      return Integer.toString(this.p);
   }
}
