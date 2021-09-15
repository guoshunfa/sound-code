package sun.text.normalizer;

public class ReplaceableString implements Replaceable {
   private StringBuffer buf;

   public ReplaceableString(String var1) {
      this.buf = new StringBuffer(var1);
   }

   public ReplaceableString(StringBuffer var1) {
      this.buf = var1;
   }

   public int length() {
      return this.buf.length();
   }

   public char charAt(int var1) {
      return this.buf.charAt(var1);
   }

   public void getChars(int var1, int var2, char[] var3, int var4) {
      Utility.getChars(this.buf, var1, var2, var3, var4);
   }
}
