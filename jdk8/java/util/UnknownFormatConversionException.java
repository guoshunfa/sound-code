package java.util;

public class UnknownFormatConversionException extends IllegalFormatException {
   private static final long serialVersionUID = 19060418L;
   private String s;

   public UnknownFormatConversionException(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.s = var1;
      }
   }

   public String getConversion() {
      return this.s;
   }

   public String getMessage() {
      return String.format("Conversion = '%s'", this.s);
   }
}
