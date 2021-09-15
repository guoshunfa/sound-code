package java.awt.font;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public final class NumericShaper implements Serializable {
   private int key;
   private int mask;
   private NumericShaper.Range shapingRange;
   private transient Set<NumericShaper.Range> rangeSet;
   private transient NumericShaper.Range[] rangeArray;
   private static final int BSEARCH_THRESHOLD = 3;
   private static final long serialVersionUID = -8022764705923730308L;
   public static final int EUROPEAN = 1;
   public static final int ARABIC = 2;
   public static final int EASTERN_ARABIC = 4;
   public static final int DEVANAGARI = 8;
   public static final int BENGALI = 16;
   public static final int GURMUKHI = 32;
   public static final int GUJARATI = 64;
   public static final int ORIYA = 128;
   public static final int TAMIL = 256;
   public static final int TELUGU = 512;
   public static final int KANNADA = 1024;
   public static final int MALAYALAM = 2048;
   public static final int THAI = 4096;
   public static final int LAO = 8192;
   public static final int TIBETAN = 16384;
   public static final int MYANMAR = 32768;
   public static final int ETHIOPIC = 65536;
   public static final int KHMER = 131072;
   public static final int MONGOLIAN = 262144;
   public static final int ALL_RANGES = 524287;
   private static final int EUROPEAN_KEY = 0;
   private static final int ARABIC_KEY = 1;
   private static final int EASTERN_ARABIC_KEY = 2;
   private static final int DEVANAGARI_KEY = 3;
   private static final int BENGALI_KEY = 4;
   private static final int GURMUKHI_KEY = 5;
   private static final int GUJARATI_KEY = 6;
   private static final int ORIYA_KEY = 7;
   private static final int TAMIL_KEY = 8;
   private static final int TELUGU_KEY = 9;
   private static final int KANNADA_KEY = 10;
   private static final int MALAYALAM_KEY = 11;
   private static final int THAI_KEY = 12;
   private static final int LAO_KEY = 13;
   private static final int TIBETAN_KEY = 14;
   private static final int MYANMAR_KEY = 15;
   private static final int ETHIOPIC_KEY = 16;
   private static final int KHMER_KEY = 17;
   private static final int MONGOLIAN_KEY = 18;
   private static final int NUM_KEYS = 19;
   private static final int CONTEXTUAL_MASK = Integer.MIN_VALUE;
   private static final char[] bases = new char[]{'\u0000', 'ذ', 'ۀ', 'श', 'শ', 'ਸ਼', 'શ', 'ଶ', 'ஶ', 'శ', 'ಶ', 'ശ', 'ภ', '\u0ea0', '\u0ef0', 'တ', 'ጸ', 'ឰ', '០'};
   private static final char[] contexts = new char[]{'\u0000', '̀', '\u0600', 'ހ', '\u0600', 'ހ', 'ऀ', '\u0980', '\u0980', '\u0a00', '\u0a00', '\u0a80', '\u0a80', '\u0b00', '\u0b00', '\u0b80', '\u0b80', '\u0c00', '\u0c00', '\u0c80', '\u0c80', '\u0d00', '\u0d00', '\u0d80', '\u0e00', '\u0e80', '\u0e80', 'ༀ', 'ༀ', 'က', 'က', 'ႀ', 'ሀ', 'ᎀ', 'ក', '᠀', '᠀', 'ᤀ', '\uffff'};
   private static int ctCache = 0;
   private static int ctCacheLimit;
   private transient volatile NumericShaper.Range currentRange;
   private static int[] strongTable;
   private transient volatile int stCache;

   private static int getContextKey(char var0) {
      if (var0 < contexts[ctCache]) {
         while(ctCache > 0 && var0 < contexts[ctCache]) {
            --ctCache;
         }
      } else if (var0 >= contexts[ctCache + 1]) {
         while(ctCache < ctCacheLimit && var0 >= contexts[ctCache + 1]) {
            ++ctCache;
         }
      }

      return (ctCache & 1) == 0 ? ctCache / 2 : 0;
   }

   private NumericShaper.Range rangeForCodePoint(int var1) {
      if (this.currentRange.inRange(var1)) {
         return this.currentRange;
      } else {
         NumericShaper.Range[] var2 = this.rangeArray;
         int var3;
         if (var2.length > 3) {
            var3 = 0;
            int var4 = var2.length - 1;

            while(var3 <= var4) {
               int var5 = (var3 + var4) / 2;
               NumericShaper.Range var6 = var2[var5];
               if (var1 < var6.start) {
                  var4 = var5 - 1;
               } else {
                  if (var1 < var6.end) {
                     this.currentRange = var6;
                     return var6;
                  }

                  var3 = var5 + 1;
               }
            }
         } else {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3].inRange(var1)) {
                  return var2[var3];
               }
            }
         }

         return NumericShaper.Range.EUROPEAN;
      }
   }

   private boolean isStrongDirectional(char var1) {
      int var2 = this.stCache;
      if (var1 < strongTable[var2]) {
         var2 = search(var1, strongTable, 0, var2);
      } else if (var1 >= strongTable[var2 + 1]) {
         var2 = search(var1, strongTable, var2 + 1, strongTable.length - var2 - 1);
      }

      boolean var3 = (var2 & 1) == 1;
      this.stCache = var2;
      return var3;
   }

   private static int getKeyFromMask(int var0) {
      int var1;
      for(var1 = 0; var1 < 19 && (var0 & 1 << var1) == 0; ++var1) {
      }

      if (var1 != 19 && (var0 & ~(1 << var1)) == 0) {
         return var1;
      } else {
         throw new IllegalArgumentException("invalid shaper: " + Integer.toHexString(var0));
      }
   }

   public static NumericShaper getShaper(int var0) {
      int var1 = getKeyFromMask(var0);
      return new NumericShaper(var1, var0);
   }

   public static NumericShaper getShaper(NumericShaper.Range var0) {
      return new NumericShaper(var0, EnumSet.of(var0));
   }

   public static NumericShaper getContextualShaper(int var0) {
      var0 |= Integer.MIN_VALUE;
      return new NumericShaper(0, var0);
   }

   public static NumericShaper getContextualShaper(Set<NumericShaper.Range> var0) {
      NumericShaper var1 = new NumericShaper(NumericShaper.Range.EUROPEAN, var0);
      var1.mask = Integer.MIN_VALUE;
      return var1;
   }

   public static NumericShaper getContextualShaper(int var0, int var1) {
      int var2 = getKeyFromMask(var1);
      var0 |= Integer.MIN_VALUE;
      return new NumericShaper(var2, var0);
   }

   public static NumericShaper getContextualShaper(Set<NumericShaper.Range> var0, NumericShaper.Range var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         NumericShaper var2 = new NumericShaper(var1, var0);
         var2.mask = Integer.MIN_VALUE;
         return var2;
      }
   }

   private NumericShaper(int var1, int var2) {
      this.currentRange = NumericShaper.Range.EUROPEAN;
      this.stCache = 0;
      this.key = var1;
      this.mask = var2;
   }

   private NumericShaper(NumericShaper.Range var1, Set<NumericShaper.Range> var2) {
      this.currentRange = NumericShaper.Range.EUROPEAN;
      this.stCache = 0;
      this.shapingRange = var1;
      this.rangeSet = EnumSet.copyOf((Collection)var2);
      if (this.rangeSet.contains(NumericShaper.Range.EASTERN_ARABIC) && this.rangeSet.contains(NumericShaper.Range.ARABIC)) {
         this.rangeSet.remove(NumericShaper.Range.ARABIC);
      }

      if (this.rangeSet.contains(NumericShaper.Range.TAI_THAM_THAM) && this.rangeSet.contains(NumericShaper.Range.TAI_THAM_HORA)) {
         this.rangeSet.remove(NumericShaper.Range.TAI_THAM_HORA);
      }

      this.rangeArray = (NumericShaper.Range[])this.rangeSet.toArray(new NumericShaper.Range[this.rangeSet.size()]);
      if (this.rangeArray.length > 3) {
         Arrays.sort(this.rangeArray, new Comparator<NumericShaper.Range>() {
            public int compare(NumericShaper.Range var1, NumericShaper.Range var2) {
               return var1.base > var2.base ? 1 : (var1.base == var2.base ? 0 : -1);
            }
         });
      }

   }

   public void shape(char[] var1, int var2, int var3) {
      this.checkParams(var1, var2, var3);
      if (this.isContextual()) {
         if (this.rangeSet == null) {
            this.shapeContextually(var1, var2, var3, this.key);
         } else {
            this.shapeContextually(var1, var2, var3, this.shapingRange);
         }
      } else {
         this.shapeNonContextually(var1, var2, var3);
      }

   }

   public void shape(char[] var1, int var2, int var3, int var4) {
      this.checkParams(var1, var2, var3);
      if (this.isContextual()) {
         int var5 = getKeyFromMask(var4);
         if (this.rangeSet == null) {
            this.shapeContextually(var1, var2, var3, var5);
         } else {
            this.shapeContextually(var1, var2, var3, NumericShaper.Range.values()[var5]);
         }
      } else {
         this.shapeNonContextually(var1, var2, var3);
      }

   }

   public void shape(char[] var1, int var2, int var3, NumericShaper.Range var4) {
      this.checkParams(var1, var2, var3);
      if (var4 == null) {
         throw new NullPointerException("context is null");
      } else {
         if (this.isContextual()) {
            if (this.rangeSet != null) {
               this.shapeContextually(var1, var2, var3, var4);
            } else {
               int var5 = NumericShaper.Range.toRangeIndex(var4);
               if (var5 >= 0) {
                  this.shapeContextually(var1, var2, var3, var5);
               } else {
                  this.shapeContextually(var1, var2, var3, this.shapingRange);
               }
            }
         } else {
            this.shapeNonContextually(var1, var2, var3);
         }

      }
   }

   private void checkParams(char[] var1, int var2, int var3) {
      if (var1 == null) {
         throw new NullPointerException("text is null");
      } else if (var2 < 0 || var2 > var1.length || var2 + var3 < 0 || var2 + var3 > var1.length) {
         throw new IndexOutOfBoundsException("bad start or count for text of length " + var1.length);
      }
   }

   public boolean isContextual() {
      return (this.mask & Integer.MIN_VALUE) != 0;
   }

   public int getRanges() {
      return this.mask & Integer.MAX_VALUE;
   }

   public Set<NumericShaper.Range> getRangeSet() {
      return (Set)(this.rangeSet != null ? EnumSet.copyOf((Collection)this.rangeSet) : NumericShaper.Range.maskToRangeSet(this.mask));
   }

   private void shapeNonContextually(char[] var1, int var2, int var3) {
      char var5 = '0';
      int var4;
      if (this.shapingRange != null) {
         var4 = this.shapingRange.getDigitBase();
         var5 += this.shapingRange.getNumericBase();
      } else {
         var4 = bases[this.key];
         if (this.key == 16) {
            ++var5;
         }
      }

      int var6 = var2;

      for(int var7 = var2 + var3; var6 < var7; ++var6) {
         char var8 = var1[var6];
         if (var8 >= var5 && var8 <= '9') {
            var1[var6] = (char)(var8 + var4);
         }
      }

   }

   private synchronized void shapeContextually(char[] var1, int var2, int var3, int var4) {
      if ((this.mask & 1 << var4) == 0) {
         var4 = 0;
      }

      int var5 = var4;
      char var6 = bases[var4];
      int var7 = var4 == 16 ? 49 : 48;
      Class var8 = NumericShaper.class;
      synchronized(NumericShaper.class) {
         int var9 = var2;

         for(int var10 = var2 + var3; var9 < var10; ++var9) {
            char var11 = var1[var9];
            if (var11 >= var7 && var11 <= '9') {
               var1[var9] = (char)(var11 + var6);
            }

            if (this.isStrongDirectional(var11)) {
               int var12 = getContextKey(var11);
               if (var12 != var5) {
                  var5 = var12;
                  var4 = var12;
                  if ((this.mask & 4) == 0 || var12 != 1 && var12 != 2) {
                     if ((this.mask & 2) == 0 || var12 != 1 && var12 != 2) {
                        if ((this.mask & 1 << var12) == 0) {
                           var4 = 0;
                        }
                     } else {
                        var4 = 1;
                     }
                  } else {
                     var4 = 2;
                  }

                  var6 = bases[var4];
                  var7 = var4 == 16 ? 49 : 48;
               }
            }
         }

      }
   }

   private void shapeContextually(char[] var1, int var2, int var3, NumericShaper.Range var4) {
      if (var4 == null || !this.rangeSet.contains(var4)) {
         var4 = NumericShaper.Range.EUROPEAN;
      }

      NumericShaper.Range var5 = var4;
      int var6 = var4.getDigitBase();
      char var7 = (char)(48 + var4.getNumericBase());
      int var8 = var2 + var3;

      for(int var9 = var2; var9 < var8; ++var9) {
         char var10 = var1[var9];
         if (var10 >= var7 && var10 <= '9') {
            var1[var9] = (char)(var10 + var6);
         } else if (this.isStrongDirectional(var10)) {
            var4 = this.rangeForCodePoint(var10);
            if (var4 != var5) {
               var5 = var4;
               var6 = var4.getDigitBase();
               var7 = (char)(48 + var4.getNumericBase());
            }
         }
      }

   }

   public int hashCode() {
      int var1 = this.mask;
      if (this.rangeSet != null) {
         var1 &= Integer.MIN_VALUE;
         var1 ^= this.rangeSet.hashCode();
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (var1 != null) {
         try {
            NumericShaper var2 = (NumericShaper)var1;
            if (this.rangeSet == null) {
               if (var2.rangeSet == null) {
                  return var2.mask == this.mask && var2.key == this.key;
               }

               Set var3 = NumericShaper.Range.maskToRangeSet(this.mask);
               NumericShaper.Range var4 = NumericShaper.Range.indexToRange(this.key);
               return this.isContextual() == var2.isContextual() && var3.equals(var2.rangeSet) && var4 == var2.shapingRange;
            }

            if (var2.rangeSet == null) {
               return this.isContextual() == var2.isContextual() && this.rangeSet.equals(NumericShaper.Range.maskToRangeSet(var2.mask)) && this.shapingRange == NumericShaper.Range.indexToRange(var2.key);
            }

            return this.isContextual() == var2.isContextual() && this.rangeSet.equals(var2.rangeSet) && this.shapingRange == var2.shapingRange;
         } catch (ClassCastException var5) {
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString());
      var1.append("[contextual:").append(this.isContextual());
      Object var2 = null;
      if (this.isContextual()) {
         var1.append(", context:");
         var1.append((Object)(this.shapingRange == null ? NumericShaper.Range.values()[this.key] : this.shapingRange));
      }

      if (this.rangeSet == null) {
         var1.append(", range(s): ");
         boolean var3 = true;

         for(int var4 = 0; var4 < 19; ++var4) {
            if ((this.mask & 1 << var4) != 0) {
               if (var3) {
                  var3 = false;
               } else {
                  var1.append(", ");
               }

               var1.append((Object)NumericShaper.Range.values()[var4]);
            }
         }
      } else {
         var1.append(", range set: ").append((Object)this.rangeSet);
      }

      var1.append(']');
      return var1.toString();
   }

   private static int getHighBit(int var0) {
      if (var0 <= 0) {
         return -32;
      } else {
         int var1 = 0;
         if (var0 >= 65536) {
            var0 >>= 16;
            var1 += 16;
         }

         if (var0 >= 256) {
            var0 >>= 8;
            var1 += 8;
         }

         if (var0 >= 16) {
            var0 >>= 4;
            var1 += 4;
         }

         if (var0 >= 4) {
            var0 >>= 2;
            var1 += 2;
         }

         if (var0 >= 2) {
            ++var1;
         }

         return var1;
      }
   }

   private static int search(int var0, int[] var1, int var2, int var3) {
      int var4 = 1 << getHighBit(var3);
      int var5 = var3 - var4;
      int var6 = var4;
      int var7 = var2;
      if (var0 >= var1[var2 + var5]) {
         var7 = var2 + var5;
      }

      while(var6 > 1) {
         var6 >>= 1;
         if (var0 >= var1[var7 + var6]) {
            var7 += var6;
         }
      }

      return var7;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      if (this.shapingRange != null) {
         int var2 = NumericShaper.Range.toRangeIndex(this.shapingRange);
         if (var2 >= 0) {
            this.key = var2;
         }
      }

      if (this.rangeSet != null) {
         this.mask |= NumericShaper.Range.toRangeMask(this.rangeSet);
      }

      var1.defaultWriteObject();
   }

   static {
      ctCacheLimit = contexts.length - 2;
      strongTable = new int[]{0, 65, 91, 97, 123, 170, 171, 181, 182, 186, 187, 192, 215, 216, 247, 248, 697, 699, 706, 720, 722, 736, 741, 750, 751, 880, 884, 886, 894, 902, 903, 904, 1014, 1015, 1155, 1162, 1418, 1470, 1471, 1472, 1473, 1475, 1476, 1478, 1479, 1488, 1536, 1544, 1545, 1547, 1548, 1549, 1550, 1563, 1611, 1645, 1648, 1649, 1750, 1765, 1767, 1774, 1776, 1786, 1809, 1810, 1840, 1869, 1958, 1969, 2027, 2036, 2038, 2042, 2070, 2074, 2075, 2084, 2085, 2088, 2089, 2096, 2137, 2142, 2276, 2307, 2362, 2363, 2364, 2365, 2369, 2377, 2381, 2382, 2385, 2392, 2402, 2404, 2433, 2434, 2492, 2493, 2497, 2503, 2509, 2510, 2530, 2534, 2546, 2548, 2555, 2563, 2620, 2622, 2625, 2649, 2672, 2674, 2677, 2691, 2748, 2749, 2753, 2761, 2765, 2768, 2786, 2790, 2801, 2818, 2876, 2877, 2879, 2880, 2881, 2887, 2893, 2903, 2914, 2918, 2946, 2947, 3008, 3009, 3021, 3024, 3059, 3073, 3134, 3137, 3142, 3160, 3170, 3174, 3192, 3199, 3260, 3261, 3276, 3285, 3298, 3302, 3393, 3398, 3405, 3406, 3426, 3430, 3530, 3535, 3538, 3544, 3633, 3634, 3636, 3648, 3655, 3663, 3761, 3762, 3764, 3773, 3784, 3792, 3864, 3866, 3893, 3894, 3895, 3896, 3897, 3902, 3953, 3967, 3968, 3973, 3974, 3976, 3981, 4030, 4038, 4039, 4141, 4145, 4146, 4152, 4153, 4155, 4157, 4159, 4184, 4186, 4190, 4193, 4209, 4213, 4226, 4227, 4229, 4231, 4237, 4238, 4253, 4254, 4957, 4960, 5008, 5024, 5120, 5121, 5760, 5761, 5787, 5792, 5906, 5920, 5938, 5941, 5970, 5984, 6002, 6016, 6068, 6070, 6071, 6078, 6086, 6087, 6089, 6100, 6107, 6108, 6109, 6112, 6128, 6160, 6313, 6314, 6432, 6435, 6439, 6441, 6450, 6451, 6457, 6470, 6622, 6656, 6679, 6681, 6742, 6743, 6744, 6753, 6754, 6755, 6757, 6765, 6771, 6784, 6912, 6916, 6964, 6965, 6966, 6971, 6972, 6973, 6978, 6979, 7019, 7028, 7040, 7042, 7074, 7078, 7080, 7082, 7083, 7084, 7142, 7143, 7144, 7146, 7149, 7150, 7151, 7154, 7212, 7220, 7222, 7227, 7376, 7379, 7380, 7393, 7394, 7401, 7405, 7406, 7412, 7413, 7616, 7680, 8125, 8126, 8127, 8130, 8141, 8144, 8157, 8160, 8173, 8178, 8189, 8206, 8208, 8305, 8308, 8319, 8320, 8336, 8352, 8450, 8451, 8455, 8456, 8458, 8468, 8469, 8470, 8473, 8478, 8484, 8485, 8486, 8487, 8488, 8489, 8490, 8494, 8495, 8506, 8508, 8512, 8517, 8522, 8526, 8528, 8544, 8585, 9014, 9083, 9109, 9110, 9372, 9450, 9900, 9901, 10240, 10496, 11264, 11493, 11499, 11503, 11506, 11513, 11520, 11647, 11648, 11744, 12293, 12296, 12321, 12330, 12337, 12342, 12344, 12349, 12353, 12441, 12445, 12448, 12449, 12539, 12540, 12736, 12784, 12829, 12832, 12880, 12896, 12924, 12927, 12977, 12992, 13004, 13008, 13175, 13179, 13278, 13280, 13311, 13312, 19904, 19968, 42128, 42192, 42509, 42512, 42607, 42624, 42655, 42656, 42736, 42738, 42752, 42786, 42888, 42889, 43010, 43011, 43014, 43015, 43019, 43020, 43045, 43047, 43048, 43056, 43064, 43072, 43124, 43136, 43204, 43214, 43232, 43250, 43302, 43310, 43335, 43346, 43392, 43395, 43443, 43444, 43446, 43450, 43452, 43453, 43561, 43567, 43569, 43571, 43573, 43584, 43587, 43588, 43596, 43597, 43696, 43697, 43698, 43701, 43703, 43705, 43710, 43712, 43713, 43714, 43756, 43758, 43766, 43777, 44005, 44006, 44008, 44009, 44013, 44016, 64286, 64287, 64297, 64298, 64830, 64848, 65021, 65136, 65279, 65313, 65339, 65345, 65371, 65382, 65504, 65536, 65793, 65794, 65856, 66000, 66045, 66176, 67871, 67872, 68097, 68112, 68152, 68160, 68409, 68416, 69216, 69632, 69633, 69634, 69688, 69703, 69714, 69734, 69760, 69762, 69811, 69815, 69817, 69819, 69888, 69891, 69927, 69932, 69933, 69942, 70016, 70018, 70070, 70079, 71339, 71340, 71341, 71342, 71344, 71350, 71351, 71360, 94095, 94099, 119143, 119146, 119155, 119171, 119173, 119180, 119210, 119214, 119296, 119648, 120539, 120540, 120597, 120598, 120655, 120656, 120713, 120714, 120771, 120772, 120782, 126464, 126704, 127248, 127338, 127344, 127744, 128140, 128141, 128292, 128293, 131072, 917505, 983040, 1114110, 1114111};
   }

   public static enum Range {
      EUROPEAN(48, 0, 768),
      ARABIC(1632, 1536, 1920),
      EASTERN_ARABIC(1776, 1536, 1920),
      DEVANAGARI(2406, 2304, 2432),
      BENGALI(2534, 2432, 2560),
      GURMUKHI(2662, 2560, 2688),
      GUJARATI(2790, 2816, 2944),
      ORIYA(2918, 2816, 2944),
      TAMIL(3046, 2944, 3072),
      TELUGU(3174, 3072, 3200),
      KANNADA(3302, 3200, 3328),
      MALAYALAM(3430, 3328, 3456),
      THAI(3664, 3584, 3712),
      LAO(3792, 3712, 3840),
      TIBETAN(3872, 3840, 4096),
      MYANMAR(4160, 4096, 4224),
      ETHIOPIC(4969, 4608, 4992) {
         char getNumericBase() {
            return '\u0001';
         }
      },
      KHMER(6112, 6016, 6144),
      MONGOLIAN(6160, 6144, 6400),
      NKO(1984, 1984, 2048),
      MYANMAR_SHAN(4240, 4096, 4256),
      LIMBU(6470, 6400, 6480),
      NEW_TAI_LUE(6608, 6528, 6624),
      BALINESE(6992, 6912, 7040),
      SUNDANESE(7088, 7040, 7104),
      LEPCHA(7232, 7168, 7248),
      OL_CHIKI(7248, 7248, 7296),
      VAI(42528, 42240, 42560),
      SAURASHTRA(43216, 43136, 43232),
      KAYAH_LI(43264, 43264, 43312),
      CHAM(43600, 43520, 43616),
      TAI_THAM_HORA(6784, 6688, 6832),
      TAI_THAM_THAM(6800, 6688, 6832),
      JAVANESE(43472, 43392, 43488),
      MEETEI_MAYEK(44016, 43968, 44032);

      private final int base;
      private final int start;
      private final int end;

      private static int toRangeIndex(NumericShaper.Range var0) {
         int var1 = var0.ordinal();
         return var1 < 19 ? var1 : -1;
      }

      private static NumericShaper.Range indexToRange(int var0) {
         return var0 < 19 ? values()[var0] : null;
      }

      private static int toRangeMask(Set<NumericShaper.Range> var0) {
         int var1 = 0;
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            NumericShaper.Range var3 = (NumericShaper.Range)var2.next();
            int var4 = var3.ordinal();
            if (var4 < 19) {
               var1 |= 1 << var4;
            }
         }

         return var1;
      }

      private static Set<NumericShaper.Range> maskToRangeSet(int var0) {
         EnumSet var1 = EnumSet.noneOf(NumericShaper.Range.class);
         NumericShaper.Range[] var2 = values();

         for(int var3 = 0; var3 < 19; ++var3) {
            if ((var0 & 1 << var3) != 0) {
               var1.add(var2[var3]);
            }
         }

         return var1;
      }

      private Range(int var3, int var4, int var5) {
         this.base = var3 - (48 + this.getNumericBase());
         this.start = var4;
         this.end = var5;
      }

      private int getDigitBase() {
         return this.base;
      }

      char getNumericBase() {
         return '\u0000';
      }

      private boolean inRange(int var1) {
         return this.start <= var1 && var1 < this.end;
      }

      // $FF: synthetic method
      Range(int var3, int var4, int var5, Object var6) {
         this(var3, var4, var5);
      }
   }
}
