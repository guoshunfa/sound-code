package sun.font;

public final class TextRecord {
   public char[] text;
   public int start;
   public int limit;
   public int min;
   public int max;

   public void init(char[] var1, int var2, int var3, int var4, int var5) {
      this.text = var1;
      this.start = var2;
      this.limit = var3;
      this.min = var4;
      this.max = var5;
   }
}
