package sun.util.locale;

public class LocaleSyntaxException extends Exception {
   private static final long serialVersionUID = 1L;
   private int index;

   public LocaleSyntaxException(String var1) {
      this(var1, 0);
   }

   public LocaleSyntaxException(String var1, int var2) {
      super(var1);
      this.index = -1;
      this.index = var2;
   }

   public int getErrorIndex() {
      return this.index;
   }
}
