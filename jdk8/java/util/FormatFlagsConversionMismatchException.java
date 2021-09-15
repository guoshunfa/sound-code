package java.util;

public class FormatFlagsConversionMismatchException extends IllegalFormatException {
   private static final long serialVersionUID = 19120414L;
   private String f;
   private char c;

   public FormatFlagsConversionMismatchException(String var1, char var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.f = var1;
         this.c = var2;
      }
   }

   public String getFlags() {
      return this.f;
   }

   public char getConversion() {
      return this.c;
   }

   public String getMessage() {
      return "Conversion = " + this.c + ", Flags = " + this.f;
   }
}
