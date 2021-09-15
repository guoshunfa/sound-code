package java.util;

public class MissingFormatWidthException extends IllegalFormatException {
   private static final long serialVersionUID = 15560123L;
   private String s;

   public MissingFormatWidthException(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.s = var1;
      }
   }

   public String getFormatSpecifier() {
      return this.s;
   }

   public String getMessage() {
      return this.s;
   }
}
