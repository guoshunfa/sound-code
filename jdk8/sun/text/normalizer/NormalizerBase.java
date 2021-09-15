package sun.text.normalizer;

import java.text.CharacterIterator;
import java.text.Normalizer;

public final class NormalizerBase implements Cloneable {
   private char[] buffer;
   private int bufferStart;
   private int bufferPos;
   private int bufferLimit;
   private UCharacterIterator text;
   private NormalizerBase.Mode mode;
   private int options;
   private int currentIndex;
   private int nextIndex;
   public static final int UNICODE_3_2 = 32;
   public static final int DONE = -1;
   public static final NormalizerBase.Mode NONE = new NormalizerBase.Mode(1);
   public static final NormalizerBase.Mode NFD = new NormalizerBase.NFDMode(2);
   public static final NormalizerBase.Mode NFKD = new NormalizerBase.NFKDMode(3);
   public static final NormalizerBase.Mode NFC = new NormalizerBase.NFCMode(4);
   public static final NormalizerBase.Mode NFKC = new NormalizerBase.NFKCMode(5);
   public static final NormalizerBase.QuickCheckResult NO = new NormalizerBase.QuickCheckResult(0);
   public static final NormalizerBase.QuickCheckResult YES = new NormalizerBase.QuickCheckResult(1);
   public static final NormalizerBase.QuickCheckResult MAYBE = new NormalizerBase.QuickCheckResult(2);
   private static final int MAX_BUF_SIZE_COMPOSE = 2;
   private static final int MAX_BUF_SIZE_DECOMPOSE = 3;
   public static final int UNICODE_3_2_0_ORIGINAL = 262432;
   public static final int UNICODE_LATEST = 0;

   public NormalizerBase(String var1, NormalizerBase.Mode var2, int var3) {
      this.buffer = new char[100];
      this.bufferStart = 0;
      this.bufferPos = 0;
      this.bufferLimit = 0;
      this.mode = NFC;
      this.options = 0;
      this.text = UCharacterIterator.getInstance(var1);
      this.mode = var2;
      this.options = var3;
   }

   public NormalizerBase(CharacterIterator var1, NormalizerBase.Mode var2) {
      this((CharacterIterator)var1, var2, 0);
   }

   public NormalizerBase(CharacterIterator var1, NormalizerBase.Mode var2, int var3) {
      this.buffer = new char[100];
      this.bufferStart = 0;
      this.bufferPos = 0;
      this.bufferLimit = 0;
      this.mode = NFC;
      this.options = 0;
      this.text = UCharacterIterator.getInstance((CharacterIterator)var1.clone());
      this.mode = var2;
      this.options = var3;
   }

   public Object clone() {
      try {
         NormalizerBase var1 = (NormalizerBase)super.clone();
         var1.text = (UCharacterIterator)this.text.clone();
         if (this.buffer != null) {
            var1.buffer = new char[this.buffer.length];
            System.arraycopy(this.buffer, 0, var1.buffer, 0, this.buffer.length);
         }

         return var1;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError(var2.toString(), var2);
      }
   }

   public static String compose(String var0, boolean var1, int var2) {
      char[] var3;
      char[] var4;
      if (var2 == 262432) {
         String var5 = NormalizerImpl.convert(var0);
         var3 = new char[var5.length() * 2];
         var4 = var5.toCharArray();
      } else {
         var3 = new char[var0.length() * 2];
         var4 = var0.toCharArray();
      }

      boolean var7 = false;
      UnicodeSet var6 = NormalizerImpl.getNX(var2);
      var2 &= -12544;
      if (var1) {
         var2 |= 4096;
      }

      while(true) {
         int var8 = NormalizerImpl.compose(var4, 0, var4.length, var3, 0, var3.length, var2, var6);
         if (var8 <= var3.length) {
            return new String(var3, 0, var8);
         }

         var3 = new char[var8];
      }
   }

   public static String decompose(String var0, boolean var1) {
      return decompose(var0, var1, 0);
   }

   public static String decompose(String var0, boolean var1, int var2) {
      int[] var3 = new int[1];
      boolean var4 = false;
      UnicodeSet var5 = NormalizerImpl.getNX(var2);
      char[] var6;
      int var8;
      if (var2 == 262432) {
         String var7 = NormalizerImpl.convert(var0);
         var6 = new char[var7.length() * 3];

         while(true) {
            var8 = NormalizerImpl.decompose(var7.toCharArray(), 0, var7.length(), var6, 0, var6.length, var1, var3, var5);
            if (var8 <= var6.length) {
               return new String(var6, 0, var8);
            }

            var6 = new char[var8];
         }
      } else {
         var6 = new char[var0.length() * 3];

         while(true) {
            var8 = NormalizerImpl.decompose(var0.toCharArray(), 0, var0.length(), var6, 0, var6.length, var1, var3, var5);
            if (var8 <= var6.length) {
               return new String(var6, 0, var8);
            }

            var6 = new char[var8];
         }
      }
   }

   public static int normalize(char[] var0, int var1, int var2, char[] var3, int var4, int var5, NormalizerBase.Mode var6, int var7) {
      int var8 = var6.normalize(var0, var1, var2, var3, var4, var5, var7);
      if (var8 <= var5 - var4) {
         return var8;
      } else {
         throw new IndexOutOfBoundsException(Integer.toString(var8));
      }
   }

   public int current() {
      return this.bufferPos >= this.bufferLimit && !this.nextNormalize() ? -1 : this.getCodePointAt(this.bufferPos);
   }

   public int next() {
      if (this.bufferPos >= this.bufferLimit && !this.nextNormalize()) {
         return -1;
      } else {
         int var1 = this.getCodePointAt(this.bufferPos);
         this.bufferPos += var1 > 65535 ? 2 : 1;
         return var1;
      }
   }

   public int previous() {
      if (this.bufferPos <= 0 && !this.previousNormalize()) {
         return -1;
      } else {
         int var1 = this.getCodePointAt(this.bufferPos - 1);
         this.bufferPos -= var1 > 65535 ? 2 : 1;
         return var1;
      }
   }

   public void reset() {
      this.text.setIndex(0);
      this.currentIndex = this.nextIndex = 0;
      this.clearBuffer();
   }

   public void setIndexOnly(int var1) {
      this.text.setIndex(var1);
      this.currentIndex = this.nextIndex = var1;
      this.clearBuffer();
   }

   /** @deprecated */
   @Deprecated
   public int setIndex(int var1) {
      this.setIndexOnly(var1);
      return this.current();
   }

   /** @deprecated */
   @Deprecated
   public int getBeginIndex() {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public int getEndIndex() {
      return this.endIndex();
   }

   public int getIndex() {
      return this.bufferPos < this.bufferLimit ? this.currentIndex : this.nextIndex;
   }

   public int endIndex() {
      return this.text.getLength();
   }

   public void setMode(NormalizerBase.Mode var1) {
      this.mode = var1;
   }

   public NormalizerBase.Mode getMode() {
      return this.mode;
   }

   public void setText(String var1) {
      UCharacterIterator var2 = UCharacterIterator.getInstance(var1);
      if (var2 == null) {
         throw new InternalError("Could not create a new UCharacterIterator");
      } else {
         this.text = var2;
         this.reset();
      }
   }

   public void setText(CharacterIterator var1) {
      UCharacterIterator var2 = UCharacterIterator.getInstance(var1);
      if (var2 == null) {
         throw new InternalError("Could not create a new UCharacterIterator");
      } else {
         this.text = var2;
         this.currentIndex = this.nextIndex = 0;
         this.clearBuffer();
      }
   }

   private static long getPrevNorm32(UCharacterIterator var0, int var1, int var2, char[] var3) {
      boolean var6 = false;
      int var7;
      if ((var7 = var0.previous()) == -1) {
         return 0L;
      } else {
         var3[0] = (char)var7;
         var3[1] = 0;
         if (var3[0] < var1) {
            return 0L;
         } else if (!UTF16.isSurrogate(var3[0])) {
            return NormalizerImpl.getNorm32(var3[0]);
         } else if (!UTF16.isLeadSurrogate(var3[0]) && var0.getIndex() != 0) {
            if (UTF16.isLeadSurrogate(var3[1] = (char)var0.previous())) {
               long var4 = NormalizerImpl.getNorm32(var3[1]);
               return (var4 & (long)var2) == 0L ? 0L : NormalizerImpl.getNorm32FromSurrogatePair(var4, var3[0]);
            } else {
               var0.moveIndex(1);
               return 0L;
            }
         } else {
            var3[1] = (char)var0.current();
            return 0L;
         }
      }
   }

   private static int findPreviousIterationBoundary(UCharacterIterator var0, NormalizerBase.IsPrevBoundary var1, int var2, int var3, char[] var4, int[] var5) {
      char[] var6 = new char[2];
      var5[0] = var4.length;
      var6[0] = 0;

      while(var0.getIndex() > 0 && var6[0] != -1) {
         boolean var7 = var1.isPrevBoundary(var0, var2, var3, var6);
         if (var5[0] < (var6[1] == 0 ? 1 : 2)) {
            char[] var8 = new char[var4.length * 2];
            System.arraycopy(var4, var5[0], var8, var8.length - (var4.length - var5[0]), var4.length - var5[0]);
            var5[0] += var8.length - var4.length;
            var4 = var8;
            Object var9 = null;
         }

         var4[--var5[0]] = var6[0];
         if (var6[1] != 0) {
            var4[--var5[0]] = var6[1];
         }

         if (var7) {
            break;
         }
      }

      return var4.length - var5[0];
   }

   private static int previous(UCharacterIterator var0, char[] var1, int var2, int var3, NormalizerBase.Mode var4, boolean var5, boolean[] var6, int var7) {
      int var15 = var3 - var2;
      int var9 = 0;
      if (var6 != null) {
         var6[0] = false;
      }

      char var14 = (char)var4.getMinC();
      int var11 = var4.getMask();
      NormalizerBase.IsPrevBoundary var8 = var4.getPrevBoundary();
      if (var8 == null) {
         byte var18 = 0;
         int var12;
         if ((var12 = var0.previous()) >= 0) {
            var18 = 1;
            if (UTF16.isTrailSurrogate((char)var12)) {
               int var13 = var0.previous();
               if (var13 != -1) {
                  if (UTF16.isLeadSurrogate((char)var13)) {
                     if (var15 >= 2) {
                        var1[1] = (char)var12;
                        var18 = 2;
                     }

                     var12 = var13;
                  } else {
                     var0.moveIndex(1);
                  }
               }
            }

            if (var15 > 0) {
               var1[0] = (char)var12;
            }
         }

         return var18;
      } else {
         char[] var16 = new char[100];
         int[] var17 = new int[1];
         int var10 = findPreviousIterationBoundary(var0, var8, var14, var11, var16, var17);
         if (var10 > 0) {
            if (var5) {
               var9 = normalize(var16, var17[0], var17[0] + var10, var1, var2, var3, var4, var7);
               if (var6 != null) {
                  var6[0] = var9 != var10 || Utility.arrayRegionMatches(var16, 0, var1, var2, var3);
               }
            } else if (var15 > 0) {
               System.arraycopy(var16, var17[0], var1, 0, var10 < var15 ? var10 : var15);
            }
         }

         return var9;
      }
   }

   private static long getNextNorm32(UCharacterIterator var0, int var1, int var2, int[] var3) {
      var3[0] = var0.next();
      var3[1] = 0;
      if (var3[0] < var1) {
         return 0L;
      } else {
         long var4 = NormalizerImpl.getNorm32((char)var3[0]);
         if (UTF16.isLeadSurrogate((char)var3[0])) {
            if (var0.current() != -1 && UTF16.isTrailSurrogate((char)(var3[1] = var0.current()))) {
               var0.moveIndex(1);
               return (var4 & (long)var2) == 0L ? 0L : NormalizerImpl.getNorm32FromSurrogatePair(var4, (char)var3[1]);
            } else {
               return 0L;
            }
         } else {
            return var4;
         }
      }
   }

   private static int findNextIterationBoundary(UCharacterIterator var0, NormalizerBase.IsNextBoundary var1, int var2, int var3, char[] var4) {
      if (var0.current() == -1) {
         return 0;
      } else {
         int[] var5 = new int[]{var0.next(), 0};
         var4[0] = (char)var5[0];
         int var6 = 1;
         if (UTF16.isLeadSurrogate((char)var5[0]) && var0.current() != -1) {
            if (UTF16.isTrailSurrogate((char)(var5[1] = var0.next()))) {
               var4[var6++] = (char)var5[1];
            } else {
               var0.moveIndex(-1);
            }
         }

         while(var0.current() != -1) {
            if (var1.isNextBoundary(var0, var2, var3, var5)) {
               var0.moveIndex(var5[1] == 0 ? -1 : -2);
               break;
            }

            if (var6 + (var5[1] == 0 ? 1 : 2) <= var4.length) {
               var4[var6++] = (char)var5[0];
               if (var5[1] != 0) {
                  var4[var6++] = (char)var5[1];
               }
            } else {
               char[] var7 = new char[var4.length * 2];
               System.arraycopy(var4, 0, var7, 0, var6);
               var4 = var7;
               var7[var6++] = (char)var5[0];
               if (var5[1] != 0) {
                  var7[var6++] = (char)var5[1];
               }
            }
         }

         return var6;
      }
   }

   private static int next(UCharacterIterator var0, char[] var1, int var2, int var3, NormalizerBase.Mode var4, boolean var5, boolean[] var6, int var7) {
      int var14 = var3 - var2;
      int var15 = 0;
      if (var6 != null) {
         var6[0] = false;
      }

      char var13 = (char)var4.getMinC();
      int var9 = var4.getMask();
      NormalizerBase.IsNextBoundary var8 = var4.getNextBoundary();
      if (var8 == null) {
         byte var18 = 0;
         int var11 = var0.next();
         if (var11 != -1) {
            var18 = 1;
            if (UTF16.isLeadSurrogate((char)var11)) {
               int var12 = var0.next();
               if (var12 != -1) {
                  if (UTF16.isTrailSurrogate((char)var12)) {
                     if (var14 >= 2) {
                        var1[1] = (char)var12;
                        var18 = 2;
                     }
                  } else {
                     var0.moveIndex(-1);
                  }
               }
            }

            if (var14 > 0) {
               var1[0] = (char)var11;
            }
         }

         return var18;
      } else {
         char[] var16 = new char[100];
         int[] var17 = new int[1];
         int var10 = findNextIterationBoundary(var0, var8, var13, var9, var16);
         if (var10 > 0) {
            if (var5) {
               var15 = var4.normalize(var16, var17[0], var10, var1, var2, var3, var7);
               if (var6 != null) {
                  var6[0] = var15 != var10 || Utility.arrayRegionMatches(var16, var17[0], var1, var2, var15);
               }
            } else if (var14 > 0) {
               System.arraycopy(var16, 0, var1, var2, Math.min(var10, var14));
            }
         }

         return var15;
      }
   }

   private void clearBuffer() {
      this.bufferLimit = this.bufferStart = this.bufferPos = 0;
   }

   private boolean nextNormalize() {
      this.clearBuffer();
      this.currentIndex = this.nextIndex;
      this.text.setIndex(this.nextIndex);
      this.bufferLimit = next(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, (boolean[])null, this.options);
      this.nextIndex = this.text.getIndex();
      return this.bufferLimit > 0;
   }

   private boolean previousNormalize() {
      this.clearBuffer();
      this.nextIndex = this.currentIndex;
      this.text.setIndex(this.currentIndex);
      this.bufferLimit = previous(this.text, this.buffer, this.bufferStart, this.buffer.length, this.mode, true, (boolean[])null, this.options);
      this.currentIndex = this.text.getIndex();
      this.bufferPos = this.bufferLimit;
      return this.bufferLimit > 0;
   }

   private int getCodePointAt(int var1) {
      if (UTF16.isSurrogate(this.buffer[var1])) {
         if (UTF16.isLeadSurrogate(this.buffer[var1])) {
            if (var1 + 1 < this.bufferLimit && UTF16.isTrailSurrogate(this.buffer[var1 + 1])) {
               return UCharacterProperty.getRawSupplementary(this.buffer[var1], this.buffer[var1 + 1]);
            }
         } else if (UTF16.isTrailSurrogate(this.buffer[var1]) && var1 > 0 && UTF16.isLeadSurrogate(this.buffer[var1 - 1])) {
            return UCharacterProperty.getRawSupplementary(this.buffer[var1 - 1], this.buffer[var1]);
         }
      }

      return this.buffer[var1];
   }

   public static boolean isNFSkippable(int var0, NormalizerBase.Mode var1) {
      return var1.isNFSkippable(var0);
   }

   public NormalizerBase(String var1, NormalizerBase.Mode var2) {
      this((String)var1, var2, 0);
   }

   public static String normalize(String var0, Normalizer.Form var1) {
      return normalize(var0, var1, 0);
   }

   public static String normalize(String var0, Normalizer.Form var1, int var2) {
      int var3 = var0.length();
      boolean var4 = true;
      if (var3 < 80) {
         for(int var5 = 0; var5 < var3; ++var5) {
            if (var0.charAt(var5) > 127) {
               var4 = false;
               break;
            }
         }
      } else {
         char[] var7 = var0.toCharArray();

         for(int var6 = 0; var6 < var3; ++var6) {
            if (var7[var6] > 127) {
               var4 = false;
               break;
            }
         }
      }

      switch(var1) {
      case NFC:
         return var4 ? var0 : NFC.normalize(var0, var2);
      case NFD:
         return var4 ? var0 : NFD.normalize(var0, var2);
      case NFKC:
         return var4 ? var0 : NFKC.normalize(var0, var2);
      case NFKD:
         return var4 ? var0 : NFKD.normalize(var0, var2);
      default:
         throw new IllegalArgumentException("Unexpected normalization form: " + var1);
      }
   }

   public static boolean isNormalized(String var0, Normalizer.Form var1) {
      return isNormalized(var0, var1, 0);
   }

   public static boolean isNormalized(String var0, Normalizer.Form var1, int var2) {
      switch(var1) {
      case NFC:
         return NFC.quickCheck(var0.toCharArray(), 0, var0.length(), false, NormalizerImpl.getNX(var2)) == YES;
      case NFD:
         return NFD.quickCheck(var0.toCharArray(), 0, var0.length(), false, NormalizerImpl.getNX(var2)) == YES;
      case NFKC:
         return NFKC.quickCheck(var0.toCharArray(), 0, var0.length(), false, NormalizerImpl.getNX(var2)) == YES;
      case NFKD:
         return NFKD.quickCheck(var0.toCharArray(), 0, var0.length(), false, NormalizerImpl.getNX(var2)) == YES;
      default:
         throw new IllegalArgumentException("Unexpected normalization form: " + var1);
      }
   }

   private static final class IsNextTrueStarter implements NormalizerBase.IsNextBoundary {
      private IsNextTrueStarter() {
      }

      public boolean isNextBoundary(UCharacterIterator var1, int var2, int var3, int[] var4) {
         int var7 = var3 << 2 & 15;
         long var5 = NormalizerBase.getNextNorm32(var1, var2, var3 | var7, var4);
         return NormalizerImpl.isTrueStarter(var5, var3, var7);
      }

      // $FF: synthetic method
      IsNextTrueStarter(Object var1) {
         this();
      }
   }

   private static final class IsNextNFDSafe implements NormalizerBase.IsNextBoundary {
      private IsNextNFDSafe() {
      }

      public boolean isNextBoundary(UCharacterIterator var1, int var2, int var3, int[] var4) {
         return NormalizerImpl.isNFDSafe(NormalizerBase.getNextNorm32(var1, var2, var3, var4), var3, var3 & 63);
      }

      // $FF: synthetic method
      IsNextNFDSafe(Object var1) {
         this();
      }
   }

   private interface IsNextBoundary {
      boolean isNextBoundary(UCharacterIterator var1, int var2, int var3, int[] var4);
   }

   private static final class IsPrevTrueStarter implements NormalizerBase.IsPrevBoundary {
      private IsPrevTrueStarter() {
      }

      public boolean isPrevBoundary(UCharacterIterator var1, int var2, int var3, char[] var4) {
         int var7 = var3 << 2 & 15;
         long var5 = NormalizerBase.getPrevNorm32(var1, var2, var3 | var7, var4);
         return NormalizerImpl.isTrueStarter(var5, var3, var7);
      }

      // $FF: synthetic method
      IsPrevTrueStarter(Object var1) {
         this();
      }
   }

   private static final class IsPrevNFDSafe implements NormalizerBase.IsPrevBoundary {
      private IsPrevNFDSafe() {
      }

      public boolean isPrevBoundary(UCharacterIterator var1, int var2, int var3, char[] var4) {
         return NormalizerImpl.isNFDSafe(NormalizerBase.getPrevNorm32(var1, var2, var3, var4), var3, var3 & 63);
      }

      // $FF: synthetic method
      IsPrevNFDSafe(Object var1) {
         this();
      }
   }

   private interface IsPrevBoundary {
      boolean isPrevBoundary(UCharacterIterator var1, int var2, int var3, char[] var4);
   }

   public static final class QuickCheckResult {
      private int resultValue;

      private QuickCheckResult(int var1) {
         this.resultValue = var1;
      }

      // $FF: synthetic method
      QuickCheckResult(int var1, Object var2) {
         this(var1);
      }
   }

   private static final class NFKCMode extends NormalizerBase.Mode {
      private NFKCMode(int var1) {
         super(var1, null);
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, UnicodeSet var7) {
         return NormalizerImpl.compose(var1, var2, var3, var4, var5, var6, 4096, var7);
      }

      protected String normalize(String var1, int var2) {
         return NormalizerBase.compose(var1, true, var2);
      }

      protected int getMinC() {
         return NormalizerImpl.getFromIndexesArr(7);
      }

      protected NormalizerBase.IsPrevBoundary getPrevBoundary() {
         return new NormalizerBase.IsPrevTrueStarter();
      }

      protected NormalizerBase.IsNextBoundary getNextBoundary() {
         return new NormalizerBase.IsNextTrueStarter();
      }

      protected int getMask() {
         return 65314;
      }

      protected NormalizerBase.QuickCheckResult quickCheck(char[] var1, int var2, int var3, boolean var4, UnicodeSet var5) {
         return NormalizerImpl.quickCheck(var1, var2, var3, NormalizerImpl.getFromIndexesArr(7), 34, 4096, var4, var5);
      }

      protected boolean isNFSkippable(int var1) {
         return NormalizerImpl.isNFSkippable(var1, this, 65474L);
      }

      // $FF: synthetic method
      NFKCMode(int var1, Object var2) {
         this(var1);
      }
   }

   private static final class NFCMode extends NormalizerBase.Mode {
      private NFCMode(int var1) {
         super(var1, null);
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, UnicodeSet var7) {
         return NormalizerImpl.compose(var1, var2, var3, var4, var5, var6, 0, var7);
      }

      protected String normalize(String var1, int var2) {
         return NormalizerBase.compose(var1, false, var2);
      }

      protected int getMinC() {
         return NormalizerImpl.getFromIndexesArr(6);
      }

      protected NormalizerBase.IsPrevBoundary getPrevBoundary() {
         return new NormalizerBase.IsPrevTrueStarter();
      }

      protected NormalizerBase.IsNextBoundary getNextBoundary() {
         return new NormalizerBase.IsNextTrueStarter();
      }

      protected int getMask() {
         return 65297;
      }

      protected NormalizerBase.QuickCheckResult quickCheck(char[] var1, int var2, int var3, boolean var4, UnicodeSet var5) {
         return NormalizerImpl.quickCheck(var1, var2, var3, NormalizerImpl.getFromIndexesArr(6), 17, 0, var4, var5);
      }

      protected boolean isNFSkippable(int var1) {
         return NormalizerImpl.isNFSkippable(var1, this, 65473L);
      }

      // $FF: synthetic method
      NFCMode(int var1, Object var2) {
         this(var1);
      }
   }

   private static final class NFKDMode extends NormalizerBase.Mode {
      private NFKDMode(int var1) {
         super(var1, null);
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, UnicodeSet var7) {
         int[] var8 = new int[1];
         return NormalizerImpl.decompose(var1, var2, var3, var4, var5, var6, true, var8, var7);
      }

      protected String normalize(String var1, int var2) {
         return NormalizerBase.decompose(var1, true, var2);
      }

      protected int getMinC() {
         return 768;
      }

      protected NormalizerBase.IsPrevBoundary getPrevBoundary() {
         return new NormalizerBase.IsPrevNFDSafe();
      }

      protected NormalizerBase.IsNextBoundary getNextBoundary() {
         return new NormalizerBase.IsNextNFDSafe();
      }

      protected int getMask() {
         return 65288;
      }

      protected NormalizerBase.QuickCheckResult quickCheck(char[] var1, int var2, int var3, boolean var4, UnicodeSet var5) {
         return NormalizerImpl.quickCheck(var1, var2, var3, NormalizerImpl.getFromIndexesArr(9), 8, 4096, var4, var5);
      }

      protected boolean isNFSkippable(int var1) {
         return NormalizerImpl.isNFSkippable(var1, this, 65288L);
      }

      // $FF: synthetic method
      NFKDMode(int var1, Object var2) {
         this(var1);
      }
   }

   private static final class NFDMode extends NormalizerBase.Mode {
      private NFDMode(int var1) {
         super(var1, null);
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, UnicodeSet var7) {
         int[] var8 = new int[1];
         return NormalizerImpl.decompose(var1, var2, var3, var4, var5, var6, false, var8, var7);
      }

      protected String normalize(String var1, int var2) {
         return NormalizerBase.decompose(var1, false, var2);
      }

      protected int getMinC() {
         return 768;
      }

      protected NormalizerBase.IsPrevBoundary getPrevBoundary() {
         return new NormalizerBase.IsPrevNFDSafe();
      }

      protected NormalizerBase.IsNextBoundary getNextBoundary() {
         return new NormalizerBase.IsNextNFDSafe();
      }

      protected int getMask() {
         return 65284;
      }

      protected NormalizerBase.QuickCheckResult quickCheck(char[] var1, int var2, int var3, boolean var4, UnicodeSet var5) {
         return NormalizerImpl.quickCheck(var1, var2, var3, NormalizerImpl.getFromIndexesArr(8), 4, 0, var4, var5);
      }

      protected boolean isNFSkippable(int var1) {
         return NormalizerImpl.isNFSkippable(var1, this, 65284L);
      }

      // $FF: synthetic method
      NFDMode(int var1, Object var2) {
         this(var1);
      }
   }

   public static class Mode {
      private int modeValue;

      private Mode(int var1) {
         this.modeValue = var1;
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, UnicodeSet var7) {
         int var8 = var3 - var2;
         int var9 = var6 - var5;
         if (var8 > var9) {
            return var8;
         } else {
            System.arraycopy(var1, var2, var4, var5, var8);
            return var8;
         }
      }

      protected int normalize(char[] var1, int var2, int var3, char[] var4, int var5, int var6, int var7) {
         return this.normalize(var1, var2, var3, var4, var5, var6, NormalizerImpl.getNX(var7));
      }

      protected String normalize(String var1, int var2) {
         return var1;
      }

      protected int getMinC() {
         return -1;
      }

      protected int getMask() {
         return -1;
      }

      protected NormalizerBase.IsPrevBoundary getPrevBoundary() {
         return null;
      }

      protected NormalizerBase.IsNextBoundary getNextBoundary() {
         return null;
      }

      protected NormalizerBase.QuickCheckResult quickCheck(char[] var1, int var2, int var3, boolean var4, UnicodeSet var5) {
         return var4 ? NormalizerBase.MAYBE : NormalizerBase.NO;
      }

      protected boolean isNFSkippable(int var1) {
         return true;
      }

      // $FF: synthetic method
      Mode(int var1, Object var2) {
         this(var1);
      }
   }
}
