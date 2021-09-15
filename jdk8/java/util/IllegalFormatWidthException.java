package java.util;

public class IllegalFormatWidthException extends IllegalFormatException {
   private static final long serialVersionUID = 16660902L;
   private int w;

   public IllegalFormatWidthException(int var1) {
      this.w = var1;
   }

   public int getWidth() {
      return this.w;
   }

   public String getMessage() {
      return Integer.toString(this.w);
   }
}
