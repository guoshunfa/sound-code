package sun.swing;

public class StringUIClientPropertyKey implements UIClientPropertyKey {
   private final String key;

   public StringUIClientPropertyKey(String var1) {
      this.key = var1;
   }

   public String toString() {
      return this.key;
   }
}
