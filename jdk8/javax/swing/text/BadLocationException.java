package javax.swing.text;

public class BadLocationException extends Exception {
   private int offs;

   public BadLocationException(String var1, int var2) {
      super(var1);
      this.offs = var2;
   }

   public int offsetRequested() {
      return this.offs;
   }
}
