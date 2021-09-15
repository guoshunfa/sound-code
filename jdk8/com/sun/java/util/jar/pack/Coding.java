package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

class Coding implements Comparable<Coding>, CodingMethod, Histogram.BitMetric {
   public static final int B_MAX = 5;
   public static final int H_MAX = 256;
   public static final int S_MAX = 2;
   private final int B;
   private final int H;
   private final int L;
   private final int S;
   private final int del;
   private final int min;
   private final int max;
   private final int umin;
   private final int umax;
   private final int[] byteMin;
   private final int[] byteMax;
   private static Map<Coding, Coding> codeMap;
   private static final byte[] byteBitWidths = new byte[256];
   static boolean verboseStringForDebug;

   private static int saturate32(long var0) {
      if (var0 > 2147483647L) {
         return Integer.MAX_VALUE;
      } else {
         return var0 < -2147483648L ? Integer.MIN_VALUE : (int)var0;
      }
   }

   private static long codeRangeLong(int var0, int var1) {
      return codeRangeLong(var0, var1, var0);
   }

   private static long codeRangeLong(int var0, int var1, int var2) {
      assert var2 >= 0 && var2 <= var0;

      assert var0 >= 1 && var0 <= 5;

      assert var1 >= 1 && var1 <= 256;

      if (var2 == 0) {
         return 0L;
      } else if (var0 == 1) {
         return (long)var1;
      } else {
         int var3 = 256 - var1;
         long var4 = 0L;
         long var6 = 1L;

         for(int var8 = 1; var8 <= var2; ++var8) {
            var4 += var6;
            var6 *= (long)var1;
         }

         var4 *= (long)var3;
         if (var2 == var0) {
            var4 += var6;
         }

         return var4;
      }
   }

   public static int codeMax(int var0, int var1, int var2, int var3) {
      long var4 = codeRangeLong(var0, var1, var3);
      if (var4 == 0L) {
         return -1;
      } else if (var2 != 0 && var4 < 4294967296L) {
         long var6;
         for(var6 = var4 - 1L; isNegativeCode(var6, var2); --var6) {
         }

         if (var6 < 0L) {
            return -1;
         } else {
            int var8 = decodeSign32(var6, var2);
            return var8 < 0 ? Integer.MAX_VALUE : var8;
         }
      } else {
         return saturate32(var4 - 1L);
      }
   }

   public static int codeMin(int var0, int var1, int var2, int var3) {
      long var4 = codeRangeLong(var0, var1, var3);
      if (var4 >= 4294967296L && var3 == var0) {
         return Integer.MIN_VALUE;
      } else if (var2 == 0) {
         return 0;
      } else {
         long var6;
         for(var6 = var4 - 1L; !isNegativeCode(var6, var2); --var6) {
         }

         return var6 < 0L ? 0 : decodeSign32(var6, var2);
      }
   }

   private static long toUnsigned32(int var0) {
      return (long)var0 << 32 >>> 32;
   }

   private static boolean isNegativeCode(long var0, int var2) {
      assert var2 > 0;

      assert var0 >= -1L;

      int var3 = (1 << var2) - 1;
      return ((int)var0 + 1 & var3) == 0;
   }

   private static boolean hasNegativeCode(int var0, int var1) {
      assert var1 > 0;

      return 0 > var0 && var0 >= ~(-1 >>> var1);
   }

   private static int decodeSign32(long var0, int var2) {
      assert var0 == toUnsigned32((int)var0) : Long.toHexString(var0);

      if (var2 == 0) {
         return (int)var0;
      } else {
         int var3;
         if (isNegativeCode(var0, var2)) {
            var3 = ~((int)var0 >>> var2);
         } else {
            var3 = (int)var0 - ((int)var0 >>> var2);
         }

         assert var2 != 1 || var3 == ((int)var0 >>> 1 ^ -((int)var0 & 1));

         return var3;
      }
   }

   private static long encodeSign32(int var0, int var1) {
      if (var1 == 0) {
         return toUnsigned32(var0);
      } else {
         int var2 = (1 << var1) - 1;
         long var3;
         if (!hasNegativeCode(var0, var1)) {
            var3 = (long)var0 + toUnsigned32(var0) / (long)var2;
         } else {
            var3 = (long)((-var0 << var1) - 1);
         }

         var3 = toUnsigned32((int)var3);

         assert var0 == decodeSign32(var3, var1) : Long.toHexString(var3) + " -> " + Integer.toHexString(var0) + " != " + Integer.toHexString(decodeSign32(var3, var1));

         return var3;
      }
   }

   public static void writeInt(byte[] var0, int[] var1, int var2, int var3, int var4, int var5) {
      long var6 = encodeSign32(var2, var5);

      assert var6 == toUnsigned32((int)var6);

      assert var6 < codeRangeLong(var3, var4) : Long.toHexString(var6);

      int var8 = 256 - var4;
      long var9 = var6;
      int var11 = var1[0];

      for(int var12 = 0; var12 < var3 - 1 && var9 >= (long)var8; ++var12) {
         var9 -= (long)var8;
         int var13 = (int)((long)var8 + var9 % (long)var4);
         var9 /= (long)var4;
         var0[var11++] = (byte)var13;
      }

      var0[var11++] = (byte)((int)var9);
      var1[0] = var11;
   }

   public static int readInt(byte[] var0, int[] var1, int var2, int var3, int var4) {
      int var5 = 256 - var3;
      long var6 = 0L;
      long var8 = 1L;
      int var10 = var1[0];

      for(int var11 = 0; var11 < var2; ++var11) {
         int var12 = var0[var10++] & 255;
         var6 += (long)var12 * var8;
         var8 *= (long)var3;
         if (var12 < var5) {
            break;
         }
      }

      var1[0] = var10;
      return decodeSign32(var6, var4);
   }

   public static int readIntFrom(InputStream var0, int var1, int var2, int var3) throws IOException {
      int var4 = 256 - var2;
      long var5 = 0L;
      long var7 = 1L;

      for(int var9 = 0; var9 < var1; ++var9) {
         int var10 = var0.read();
         if (var10 < 0) {
            throw new RuntimeException("unexpected EOF");
         }

         var5 += (long)var10 * var7;
         var7 *= (long)var2;
         if (var10 < var4) {
            break;
         }
      }

      assert var5 >= 0L && var5 < codeRangeLong(var1, var2);

      return decodeSign32(var5, var3);
   }

   private Coding(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   private Coding(int var1, int var2, int var3, int var4) {
      this.B = var1;
      this.H = var2;
      this.L = 256 - var2;
      this.S = var3;
      this.del = var4;
      this.min = codeMin(var1, var2, var3, var1);
      this.max = codeMax(var1, var2, var3, var1);
      this.umin = codeMin(var1, var2, 0, var1);
      this.umax = codeMax(var1, var2, 0, var1);
      this.byteMin = new int[var1];
      this.byteMax = new int[var1];

      for(int var5 = 1; var5 <= var1; ++var5) {
         this.byteMin[var5 - 1] = codeMin(var1, var2, var3, var5);
         this.byteMax[var5 - 1] = codeMax(var1, var2, var3, var5);
      }

   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Coding)) {
         return false;
      } else {
         Coding var2 = (Coding)var1;
         if (this.B != var2.B) {
            return false;
         } else if (this.H != var2.H) {
            return false;
         } else if (this.S != var2.S) {
            return false;
         } else {
            return this.del == var2.del;
         }
      }
   }

   public int hashCode() {
      return (this.del << 14) + (this.S << 11) + (this.B << 8) + (this.H << 0);
   }

   private static synchronized Coding of(int var0, int var1, int var2, int var3) {
      if (codeMap == null) {
         codeMap = new HashMap();
      }

      Coding var4 = new Coding(var0, var1, var2, var3);
      Coding var5 = (Coding)codeMap.get(var4);
      if (var5 == null) {
         var5 = var4;
         codeMap.put(var4, var4);
      }

      return var5;
   }

   public static Coding of(int var0, int var1) {
      return of(var0, var1, 0, 0);
   }

   public static Coding of(int var0, int var1, int var2) {
      return of(var0, var1, var2, 0);
   }

   public boolean canRepresentValue(int var1) {
      return this.isSubrange() ? this.canRepresentUnsigned(var1) : this.canRepresentSigned(var1);
   }

   public boolean canRepresentSigned(int var1) {
      return var1 >= this.min && var1 <= this.max;
   }

   public boolean canRepresentUnsigned(int var1) {
      return var1 >= this.umin && var1 <= this.umax;
   }

   public int readFrom(byte[] var1, int[] var2) {
      return readInt(var1, var2, this.B, this.H, this.S);
   }

   public void writeTo(byte[] var1, int[] var2, int var3) {
      writeInt(var1, var2, var3, this.B, this.H, this.S);
   }

   public int readFrom(InputStream var1) throws IOException {
      return readIntFrom(var1, this.B, this.H, this.S);
   }

   public void writeTo(OutputStream var1, int var2) throws IOException {
      byte[] var3 = new byte[this.B];
      int[] var4 = new int[1];
      writeInt(var3, var4, var2, this.B, this.H, this.S);
      var1.write(var3, 0, var4[0]);
   }

   public void readArrayFrom(InputStream var1, int[] var2, int var3, int var4) throws IOException {
      int var5;
      for(var5 = var3; var5 < var4; ++var5) {
         var2[var5] = this.readFrom(var1);
      }

      for(var5 = 0; var5 < this.del; ++var5) {
         long var6 = 0L;

         for(int var8 = var3; var8 < var4; ++var8) {
            var6 += (long)var2[var8];
            if (this.isSubrange()) {
               var6 = (long)this.reduceToUnsignedRange(var6);
            }

            var2[var8] = (int)var6;
         }
      }

   }

   public void writeArrayTo(OutputStream var1, int[] var2, int var3, int var4) throws IOException {
      if (var4 > var3) {
         for(int var5 = 0; var5 < this.del; ++var5) {
            int[] var6;
            if (!this.isSubrange()) {
               var6 = makeDeltas(var2, var3, var4, 0, 0);
            } else {
               var6 = makeDeltas(var2, var3, var4, this.min, this.max);
            }

            var2 = var6;
            var3 = 0;
            var4 = var6.length;
         }

         byte[] var9 = new byte[256];
         int var10 = var9.length - this.B;
         int[] var7 = new int[]{0};

         for(int var8 = var3; var8 < var4; var7[0] = 0) {
            while(var7[0] <= var10) {
               this.writeTo(var9, var7, var2[var8++]);
               if (var8 >= var4) {
                  break;
               }
            }

            var1.write(var9, 0, var7[0]);
         }

      }
   }

   boolean isSubrange() {
      return this.max < Integer.MAX_VALUE && (long)this.max - (long)this.min + 1L <= 2147483647L;
   }

   boolean isFullRange() {
      return this.max == Integer.MAX_VALUE && this.min == Integer.MIN_VALUE;
   }

   int getRange() {
      assert this.isSubrange();

      return this.max - this.min + 1;
   }

   Coding setB(int var1) {
      return of(var1, this.H, this.S, this.del);
   }

   Coding setH(int var1) {
      return of(this.B, var1, this.S, this.del);
   }

   Coding setS(int var1) {
      return of(this.B, this.H, var1, this.del);
   }

   Coding setL(int var1) {
      return this.setH(256 - var1);
   }

   Coding setD(int var1) {
      return of(this.B, this.H, this.S, var1);
   }

   Coding getDeltaCoding() {
      return this.setD(this.del + 1);
   }

   Coding getValueCoding() {
      return this.isDelta() ? of(this.B, this.H, 0, this.del - 1) : this;
   }

   int reduceToUnsignedRange(long var1) {
      if (var1 == (long)((int)var1) && this.canRepresentUnsigned((int)var1)) {
         return (int)var1;
      } else {
         int var3 = this.getRange();

         assert var3 > 0;

         var1 %= (long)var3;
         if (var1 < 0L) {
            var1 += (long)var3;
         }

         assert this.canRepresentUnsigned((int)var1);

         return (int)var1;
      }
   }

   int reduceToSignedRange(int var1) {
      return this.canRepresentSigned(var1) ? var1 : reduceToSignedRange(var1, this.min, this.max);
   }

   static int reduceToSignedRange(int var0, int var1, int var2) {
      int var3 = var2 - var1 + 1;

      assert var3 > 0;

      int var4 = var0;
      var0 -= var1;
      if (var0 < 0 && var4 >= 0) {
         var0 -= var3;

         assert var0 >= 0;
      }

      var0 %= var3;
      if (var0 < 0) {
         var0 += var3;
      }

      var0 += var1;

      assert var1 <= var0 && var0 <= var2;

      return var0;
   }

   boolean isSigned() {
      return this.min < 0;
   }

   boolean isDelta() {
      return this.del != 0;
   }

   public int B() {
      return this.B;
   }

   public int H() {
      return this.H;
   }

   public int L() {
      return this.L;
   }

   public int S() {
      return this.S;
   }

   public int del() {
      return this.del;
   }

   public int min() {
      return this.min;
   }

   public int max() {
      return this.max;
   }

   public int umin() {
      return this.umin;
   }

   public int umax() {
      return this.umax;
   }

   public int byteMin(int var1) {
      return this.byteMin[var1 - 1];
   }

   public int byteMax(int var1) {
      return this.byteMax[var1 - 1];
   }

   public int compareTo(Coding var1) {
      int var2 = this.del - var1.del;
      if (var2 == 0) {
         var2 = this.B - var1.B;
      }

      if (var2 == 0) {
         var2 = this.H - var1.H;
      }

      if (var2 == 0) {
         var2 = this.S - var1.S;
      }

      return var2;
   }

   public int distanceFrom(Coding var1) {
      int var2 = this.del - var1.del;
      if (var2 < 0) {
         var2 = -var2;
      }

      int var3 = this.S - var1.S;
      if (var3 < 0) {
         var3 = -var3;
      }

      int var4 = this.B - var1.B;
      if (var4 < 0) {
         var4 = -var4;
      }

      int var5;
      int var6;
      if (this.H == var1.H) {
         var5 = 0;
      } else {
         var6 = this.getHL();
         int var7 = var1.getHL();
         var6 *= var6;
         var7 *= var7;
         if (var6 > var7) {
            var5 = ceil_lg2(1 + (var6 - 1) / var7);
         } else {
            var5 = ceil_lg2(1 + (var7 - 1) / var6);
         }
      }

      var6 = 5 * (var2 + var3 + var4) + var5;

      assert var6 != 0 || this.compareTo(var1) == 0;

      return var6;
   }

   private int getHL() {
      if (this.H <= 128) {
         return this.H;
      } else {
         return this.L >= 1 ? 16384 / this.L : '耀';
      }
   }

   static int ceil_lg2(int var0) {
      assert var0 - 1 >= 0;

      --var0;

      int var1;
      for(var1 = 0; var0 != 0; var0 >>= 1) {
         ++var1;
      }

      return var1;
   }

   static int bitWidth(int var0) {
      if (var0 < 0) {
         var0 = ~var0;
      }

      int var1 = 0;
      int var2 = var0;
      if (var0 < byteBitWidths.length) {
         return byteBitWidths[var0];
      } else {
         int var3 = var0 >>> 16;
         if (var3 != 0) {
            var2 = var3;
            var1 += 16;
         }

         var3 = var2 >>> 8;
         if (var3 != 0) {
            var2 = var3;
            var1 += 8;
         }

         var1 += byteBitWidths[var2];
         return var1;
      }
   }

   static int[] makeDeltas(int[] var0, int var1, int var2, int var3, int var4) {
      assert var4 >= var3;

      int var5 = var2 - var1;
      int[] var6 = new int[var5];
      int var7 = 0;
      int var8;
      int var9;
      if (var3 == var4) {
         for(var8 = 0; var8 < var5; ++var8) {
            var9 = var0[var1 + var8];
            var6[var8] = var9 - var7;
            var7 = var9;
         }
      } else {
         for(var8 = 0; var8 < var5; ++var8) {
            var9 = var0[var1 + var8];

            assert var9 >= 0 && var9 + var3 <= var4;

            int var10 = var9 - var7;

            assert (long)var10 == (long)var9 - (long)var7;

            var7 = var9;
            var10 = reduceToSignedRange(var10, var3, var4);
            var6[var8] = var10;
         }
      }

      return var6;
   }

   boolean canRepresent(int var1, int var2) {
      assert var1 <= var2;

      if (this.del > 0) {
         if (!this.isSubrange()) {
            return this.isFullRange();
         } else {
            return this.canRepresentUnsigned(var2) && this.canRepresentUnsigned(var1);
         }
      } else {
         return this.canRepresentSigned(var2) && this.canRepresentSigned(var1);
      }
   }

   boolean canRepresent(int[] var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (var4 == 0) {
         return true;
      } else if (this.isFullRange()) {
         return true;
      } else {
         int var5 = var1[var2];
         int var6 = var5;

         for(int var7 = 1; var7 < var4; ++var7) {
            int var8 = var1[var2 + var7];
            if (var5 < var8) {
               var5 = var8;
            }

            if (var6 > var8) {
               var6 = var8;
            }
         }

         return this.canRepresent(var6, var5);
      }
   }

   public double getBitLength(int var1) {
      return (double)this.getLength(var1) * 8.0D;
   }

   public int getLength(int var1) {
      if (this.isDelta() && this.isSubrange()) {
         if (!this.canRepresentUnsigned(var1)) {
            return Integer.MAX_VALUE;
         }

         var1 = this.reduceToSignedRange(var1);
      }

      int var2;
      if (var1 >= 0) {
         for(var2 = 0; var2 < this.B; ++var2) {
            if (var1 <= this.byteMax[var2]) {
               return var2 + 1;
            }
         }
      } else {
         for(var2 = 0; var2 < this.B; ++var2) {
            if (var1 >= this.byteMin[var2]) {
               return var2 + 1;
            }
         }
      }

      return Integer.MAX_VALUE;
   }

   public int getLength(int[] var1, int var2, int var3) {
      int var4 = var3 - var2;
      if (this.B == 1) {
         return var4;
      } else if (this.L == 0) {
         return var4 * this.B;
      } else {
         if (this.isDelta()) {
            int[] var5;
            if (!this.isSubrange()) {
               var5 = makeDeltas(var1, var2, var3, 0, 0);
            } else {
               var5 = makeDeltas(var1, var2, var3, this.min, this.max);
            }

            var1 = var5;
            var2 = 0;
         }

         int var12 = var4;

         for(int var6 = 1; var6 <= this.B; ++var6) {
            int var7 = this.byteMax[var6 - 1];
            int var8 = this.byteMin[var6 - 1];
            int var9 = 0;

            for(int var10 = 0; var10 < var4; ++var10) {
               int var11 = var1[var2 + var10];
               if (var11 >= 0) {
                  if (var11 > var7) {
                     ++var9;
                  }
               } else if (var11 < var8) {
                  ++var9;
               }
            }

            if (var9 == 0) {
               break;
            }

            if (var6 == this.B) {
               return Integer.MAX_VALUE;
            }

            var12 += var9;
         }

         return var12;
      }
   }

   public byte[] getMetaCoding(Coding var1) {
      if (var1 == this) {
         return new byte[]{0};
      } else {
         int var2 = BandStructure.indexOf(this);
         return var2 > 0 ? new byte[]{(byte)var2} : new byte[]{116, (byte)(this.del + 2 * this.S + 8 * (this.B - 1)), (byte)(this.H - 1)};
      }
   }

   public static int parseMetaCoding(byte[] var0, int var1, Coding var2, CodingMethod[] var3) {
      int var4 = var0[var1++] & 255;
      if (1 <= var4 && var4 <= 115) {
         Coding var11 = BandStructure.codingForIndex(var4);

         assert var11 != null;

         var3[0] = var11;
         return var1;
      } else if (var4 != 116) {
         return var1 - 1;
      } else {
         int var5 = var0[var1++] & 255;
         int var6 = var0[var1++] & 255;
         int var7 = var5 % 2;
         int var8 = var5 / 2 % 4;
         int var9 = var5 / 8 + 1;
         int var10 = var6 + 1;
         if (1 <= var9 && var9 <= 5 && 0 <= var8 && var8 <= 2 && 1 <= var10 && var10 <= 256 && 0 <= var7 && var7 <= 1 && (var9 != 1 || var10 == 256) && (var9 != 5 || var10 != 256)) {
            var3[0] = of(var9, var10, var8, var7);
            return var1;
         } else {
            throw new RuntimeException("Bad arb. coding: (" + var9 + "," + var10 + "," + var8 + "," + var7);
         }
      }
   }

   public String keyString() {
      return "(" + this.B + "," + this.H + "," + this.S + "," + this.del + ")";
   }

   public String toString() {
      String var1 = "Coding" + this.keyString();
      return var1;
   }

   String stringForDebug() {
      String var1 = this.min == Integer.MIN_VALUE ? "min" : "" + this.min;
      String var2 = this.max == Integer.MAX_VALUE ? "max" : "" + this.max;
      String var3 = this.keyString() + " L=" + this.L + " r=[" + var1 + "," + var2 + "]";
      if (this.isSubrange()) {
         var3 = var3 + " subrange";
      } else if (!this.isFullRange()) {
         var3 = var3 + " MIDRANGE";
      }

      if (verboseStringForDebug) {
         var3 = var3 + " {";
         int var4 = 0;

         for(int var5 = 1; var5 <= this.B; ++var5) {
            int var6 = saturate32((long)this.byteMax[var5 - 1] - (long)this.byteMin[var5 - 1] + 1L);

            assert var6 == saturate32(codeRangeLong(this.B, this.H, var5));

            var6 -= var4;
            var4 = var6;
            String var7 = var6 == Integer.MAX_VALUE ? "max" : "" + var6;
            var3 = var3 + " #" + var5 + "=" + var7;
         }

         var3 = var3 + " }";
      }

      return var3;
   }

   static {
      int var0;
      for(var0 = 0; var0 < byteBitWidths.length; ++var0) {
         byteBitWidths[var0] = (byte)ceil_lg2(var0 + 1);
      }

      for(var0 = 10; var0 >= 0; var0 = (var0 << 1) - (var0 >> 3)) {
         assert bitWidth(var0) == ceil_lg2(var0 + 1);
      }

      verboseStringForDebug = false;
   }
}
