package java.util;

public class MissingFormatArgumentException extends IllegalFormatException {
   private static final long serialVersionUID = 19190115L;
   private String s;

   public MissingFormatArgumentException(String var1) {
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
      return "Format specifier '" + this.s + "'";
   }
}
