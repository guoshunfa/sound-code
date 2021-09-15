package java.text;

import sun.text.bidi.BidiBase;

public final class Bidi {
   public static final int DIRECTION_LEFT_TO_RIGHT = 0;
   public static final int DIRECTION_RIGHT_TO_LEFT = 1;
   public static final int DIRECTION_DEFAULT_LEFT_TO_RIGHT = -2;
   public static final int DIRECTION_DEFAULT_RIGHT_TO_LEFT = -1;
   private BidiBase bidiBase;

   public Bidi(String var1, int var2) {
      if (var1 == null) {
         throw new IllegalArgumentException("paragraph is null");
      } else {
         this.bidiBase = new BidiBase(var1.toCharArray(), 0, (byte[])null, 0, var1.length(), var2);
      }
   }

   public Bidi(AttributedCharacterIterator var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("paragraph is null");
      } else {
         this.bidiBase = new BidiBase(0, 0);
         this.bidiBase.setPara(var1);
      }
   }

   public Bidi(char[] var1, int var2, byte[] var3, int var4, int var5, int var6) {
      if (var1 == null) {
         throw new IllegalArgumentException("text is null");
      } else if (var5 < 0) {
         throw new IllegalArgumentException("bad length: " + var5);
      } else if (var2 >= 0 && var5 <= var1.length - var2) {
         if (var3 == null || var4 >= 0 && var5 <= var3.length - var4) {
            this.bidiBase = new BidiBase(var1, var2, var3, var4, var5, var6);
         } else {
            throw new IllegalArgumentException("bad range: " + var4 + " length: " + var5 + " for embeddings of length: " + var1.length);
         }
      } else {
         throw new IllegalArgumentException("bad range: " + var2 + " length: " + var5 + " for text of length: " + var1.length);
      }
   }

   public Bidi createLineBidi(int var1, int var2) {
      AttributedString var3 = new AttributedString("");
      Bidi var4 = new Bidi(var3.getIterator());
      return this.bidiBase.setLine(this, this.bidiBase, var4, var4.bidiBase, var1, var2);
   }

   public boolean isMixed() {
      return this.bidiBase.isMixed();
   }

   public boolean isLeftToRight() {
      return this.bidiBase.isLeftToRight();
   }

   public boolean isRightToLeft() {
      return this.bidiBase.isRightToLeft();
   }

   public int getLength() {
      return this.bidiBase.getLength();
   }

   public boolean baseIsLeftToRight() {
      return this.bidiBase.baseIsLeftToRight();
   }

   public int getBaseLevel() {
      return this.bidiBase.getParaLevel();
   }

   public int getLevelAt(int var1) {
      return this.bidiBase.getLevelAt(var1);
   }

   public int getRunCount() {
      return this.bidiBase.countRuns();
   }

   public int getRunLevel(int var1) {
      return this.bidiBase.getRunLevel(var1);
   }

   public int getRunStart(int var1) {
      return this.bidiBase.getRunStart(var1);
   }

   public int getRunLimit(int var1) {
      return this.bidiBase.getRunLimit(var1);
   }

   public static boolean requiresBidi(char[] var0, int var1, int var2) {
      return BidiBase.requiresBidi(var0, var1, var2);
   }

   public static void reorderVisually(byte[] var0, int var1, Object[] var2, int var3, int var4) {
      BidiBase.reorderVisually(var0, var1, var2, var3, var4);
   }

   public String toString() {
      return this.bidiBase.toString();
   }
}
