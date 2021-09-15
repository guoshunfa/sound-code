package javax.swing;

public abstract class InputVerifier {
   public abstract boolean verify(JComponent var1);

   public boolean shouldYieldFocus(JComponent var1) {
      return this.verify(var1);
   }
}
