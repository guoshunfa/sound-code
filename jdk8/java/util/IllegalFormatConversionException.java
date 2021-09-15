package java.util;

public class IllegalFormatConversionException extends IllegalFormatException {
   private static final long serialVersionUID = 17000126L;
   private char c;
   private Class<?> arg;

   public IllegalFormatConversionException(char var1, Class<?> var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         this.c = var1;
         this.arg = var2;
      }
   }

   public char getConversion() {
      return this.c;
   }

   public Class<?> getArgumentClass() {
      return this.arg;
   }

   public String getMessage() {
      return String.format("%c != %s", this.c, this.arg.getName());
   }
}
