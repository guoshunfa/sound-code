package java.text;

public class ParseException extends Exception {
   private static final long serialVersionUID = 2703218443322787634L;
   private int errorOffset;

   public ParseException(String var1, int var2) {
      super(var1);
      this.errorOffset = var2;
   }

   public int getErrorOffset() {
      return this.errorOffset;
   }
}
