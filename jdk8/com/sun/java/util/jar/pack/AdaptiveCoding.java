package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class AdaptiveCoding implements CodingMethod {
   CodingMethod headCoding;
   int headLength;
   CodingMethod tailCoding;
   public static final int KX_MIN = 0;
   public static final int KX_MAX = 3;
   public static final int KX_LG2BASE = 4;
   public static final int KX_BASE = 16;
   public static final int KB_MIN = 0;
   public static final int KB_MAX = 255;
   public static final int KB_OFFSET = 1;
   public static final int KB_DEFAULT = 3;

   public AdaptiveCoding(int var1, CodingMethod var2, CodingMethod var3) {
      assert isCodableLength(var1);

      this.headLength = var1;
      this.headCoding = var2;
      this.tailCoding = var3;
   }

   public void setHeadCoding(CodingMethod var1) {
      this.headCoding = var1;
   }

   public void setHeadLength(int var1) {
      assert isCodableLength(var1);

      this.headLength = var1;
   }

   public void setTailCoding(CodingMethod var1) {
      this.tailCoding = var1;
   }

   public boolean isTrivial() {
      return this.headCoding == this.tailCoding;
   }

   public void writeArrayTo(OutputStream var1, int[] var2, int var3, int var4) throws IOException {
      writeArray(this, var1, var2, var3, var4);
   }

   private static void writeArray(AdaptiveCoding var0, OutputStream var1, int[] var2, int var3, int var4) throws IOException {
      while(true) {
         int var5 = var3 + var0.headLength;

         assert var5 <= var4;

         var0.headCoding.writeArrayTo(var1, var2, var3, var5);
         var3 = var5;
         if (!(var0.tailCoding instanceof AdaptiveCoding)) {
            var0.tailCoding.writeArrayTo(var1, var2, var5, var4);
            return;
         }

         var0 = (AdaptiveCoding)var0.tailCoding;
      }
   }

   public void readArrayFrom(InputStream var1, int[] var2, int var3, int var4) throws IOException {
      readArray(this, var1, var2, var3, var4);
   }

   private static void readArray(AdaptiveCoding var0, InputStream var1, int[] var2, int var3, int var4) throws IOException {
      while(true) {
         int var5 = var3 + var0.headLength;

         assert var5 <= var4;

         var0.headCoding.readArrayFrom(var1, var2, var3, var5);
         var3 = var5;
         if (!(var0.tailCoding instanceof AdaptiveCoding)) {
            var0.tailCoding.readArrayFrom(var1, var2, var5, var4);
            return;
         }

         var0 = (AdaptiveCoding)var0.tailCoding;
      }
   }

   static int getKXOf(int var0) {
      for(int var1 = 0; var1 <= 3; ++var1) {
         if ((var0 - 1 & -256) == 0) {
            return var1;
         }

         var0 >>>= 4;
      }

      return -1;
   }

   static int getKBOf(int var0) {
      int var1 = getKXOf(var0);
      if (var1 < 0) {
         return -1;
      } else {
         var0 >>>= var1 * 4;
         return var0 - 1;
      }
   }

   static int decodeK(int var0, int var1) {
      assert 0 <= var0 && var0 <= 3;

      assert 0 <= var1 && var1 <= 255;

      return var1 + 1 << var0 * 4;
   }

   static int getNextK(int var0) {
      if (var0 <= 0) {
         return 1;
      } else {
         int var1 = getKXOf(var0);
         if (var1 < 0) {
            return Integer.MAX_VALUE;
         } else {
            int var2 = 1 << var1 * 4;
            int var3 = 255 << var1 * 4;
            int var4 = var0 + var2;
            var4 &= ~(var2 - 1);
            if ((var4 - var2 & ~var3) == 0) {
               assert getKXOf(var4) == var1;

               return var4;
            } else if (var1 == 3) {
               return Integer.MAX_VALUE;
            } else {
               ++var1;
               int var5 = 255 << var1 * 4;
               var4 |= var3 & ~var5;
               var4 += var2;

               assert getKXOf(var4) == var1;

               return var4;
            }
         }
      }
   }

   public static boolean isCodableLength(int var0) {
      int var1 = getKXOf(var0);
      if (var1 < 0) {
         return false;
      } else {
         int var2 = 1 << var1 * 4;
         int var3 = 255 << var1 * 4;
         return (var0 - var2 & ~var3) == 0;
      }
   }

   public byte[] getMetaCoding(Coding var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(10);

      try {
         makeMetaCoding(this, var1, var2);
      } catch (IOException var4) {
         throw new RuntimeException(var4);
      }

      return var2.toByteArray();
   }

   private static void makeMetaCoding(AdaptiveCoding var0, Coding var1, ByteArrayOutputStream var2) throws IOException {
      while(true) {
         CodingMethod var3 = var0.headCoding;
         int var4 = var0.headLength;
         CodingMethod var5 = var0.tailCoding;

         assert isCodableLength(var4);

         int var7 = var3 == var1 ? 1 : 0;
         int var8 = var5 == var1 ? 1 : 0;
         if (var7 + var8 > 1) {
            var8 = 0;
         }

         int var9 = 1 * var7 + 2 * var8;

         assert var9 < 3;

         int var10 = getKXOf(var4);
         int var11 = getKBOf(var4);

         assert decodeK(var10, var11) == var4;

         int var12 = var11 != 3 ? 1 : 0;
         var2.write(117 + var10 + 4 * var12 + 8 * var9);
         if (var12 != 0) {
            var2.write(var11);
         }

         if (var7 == 0) {
            var2.write(var3.getMetaCoding(var1));
         }

         if (!(var5 instanceof AdaptiveCoding)) {
            if (var8 == 0) {
               var2.write(var5.getMetaCoding(var1));
            }

            return;
         }

         var0 = (AdaptiveCoding)var5;
      }
   }

   public static int parseMetaCoding(byte[] var0, int var1, Coding var2, CodingMethod[] var3) {
      int var4 = var0[var1++] & 255;
      if (var4 >= 117 && var4 < 141) {
         AdaptiveCoding var5 = null;

         AdaptiveCoding var15;
         for(boolean var6 = true; var6; var5 = var15) {
            var6 = false;

            assert var4 >= 117;

            var4 -= 117;
            int var7 = var4 % 4;
            int var8 = var4 / 4 % 2;
            int var9 = var4 / 8;

            assert var9 < 3;

            int var10 = var9 & 1;
            int var11 = var9 & 2;
            CodingMethod[] var12 = new CodingMethod[]{var2};
            CodingMethod[] var13 = new CodingMethod[]{var2};
            int var14 = 3;
            if (var8 != 0) {
               var14 = var0[var1++] & 255;
            }

            if (var10 == 0) {
               var1 = BandStructure.parseMetaCoding(var0, var1, var2, var12);
            }

            if (var11 == 0 && (var4 = var0[var1] & 255) >= 117 && var4 < 141) {
               ++var1;
               var6 = true;
            } else if (var11 == 0) {
               var1 = BandStructure.parseMetaCoding(var0, var1, var2, var13);
            }

            var15 = new AdaptiveCoding(decodeK(var7, var14), var12[0], var13[0]);
            if (var5 == null) {
               var3[0] = var15;
            } else {
               var5.tailCoding = var15;
            }
         }

         return var1;
      } else {
         return var1 - 1;
      }
   }

   private String keyString(CodingMethod var1) {
      return var1 instanceof Coding ? ((Coding)var1).keyString() : var1.toString();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(20);
      AdaptiveCoding var2 = this;
      var1.append("run(");

      while(true) {
         var1.append(var2.headLength).append("*");
         var1.append(this.keyString(var2.headCoding));
         if (!(var2.tailCoding instanceof AdaptiveCoding)) {
            var1.append(" **").append(this.keyString(var2.tailCoding));
            var1.append(")");
            return var1.toString();
         }

         var2 = (AdaptiveCoding)var2.tailCoding;
         var1.append(" ");
      }
   }
}
