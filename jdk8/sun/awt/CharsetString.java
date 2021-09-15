package sun.awt;

public class CharsetString {
   public char[] charsetChars;
   public int offset;
   public int length;
   public FontDescriptor fontDescriptor;

   public CharsetString(char[] var1, int var2, int var3, FontDescriptor var4) {
      this.charsetChars = var1;
      this.offset = var2;
      this.length = var3;
      this.fontDescriptor = var4;
   }
}
