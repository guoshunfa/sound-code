package sun.text;

import sun.text.normalizer.NormalizerImpl;

public final class ComposedCharIter {
   public static final int DONE = -1;
   private static int[] chars;
   private static String[] decomps;
   private static int decompNum;
   private int curChar = -1;

   public int next() {
      return this.curChar == decompNum - 1 ? -1 : chars[++this.curChar];
   }

   public String decomposition() {
      return decomps[this.curChar];
   }

   static {
      short var0 = 2000;
      chars = new int[var0];
      decomps = new String[var0];
      decompNum = NormalizerImpl.getDecompose(chars, decomps);
   }
}
