package java.lang;

public class NumberFormatException extends IllegalArgumentException {
   static final long serialVersionUID = -2848938806368998894L;

   public NumberFormatException() {
   }

   public NumberFormatException(String var1) {
      super(var1);
   }

   static NumberFormatException forInputString(String var0) {
      return new NumberFormatException("For input string: \"" + var0 + "\"");
   }
}
