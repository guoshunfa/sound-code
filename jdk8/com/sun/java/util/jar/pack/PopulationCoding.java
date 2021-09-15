package com.sun.java.util.jar.pack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;

class PopulationCoding implements CodingMethod {
   Histogram vHist;
   int[] fValues;
   int fVlen;
   long[] symtab;
   CodingMethod favoredCoding;
   CodingMethod tokenCoding;
   CodingMethod unfavoredCoding;
   int L = -1;
   static final int[] LValuesCoded = new int[]{-1, 4, 8, 16, 32, 64, 128, 192, 224, 240, 248, 252};

   public void setFavoredValues(int[] var1, int var2) {
      assert var1[0] == 0;

      assert this.fValues == null;

      this.fValues = var1;
      this.fVlen = var2;
      if (this.L >= 0) {
         this.setL(this.L);
      }

   }

   public void setFavoredValues(int[] var1) {
      int var2 = var1.length - 1;
      this.setFavoredValues(var1, var2);
   }

   public void setHistogram(Histogram var1) {
      this.vHist = var1;
   }

   public void setL(int var1) {
      this.L = var1;
      if (var1 >= 0 && this.fValues != null && this.tokenCoding == null) {
         this.tokenCoding = fitTokenCoding(this.fVlen, var1);

         assert this.tokenCoding != null;
      }

   }

   public static Coding fitTokenCoding(int var0, int var1) {
      if (var0 < 256) {
         return BandStructure.BYTE1;
      } else {
         Coding var2 = BandStructure.UNSIGNED5.setL(var1);
         if (!var2.canRepresentUnsigned(var0)) {
            return null;
         } else {
            Coding var3 = var2;
            Coding var4 = var2;

            while(true) {
               var4 = var4.setB(var4.B() - 1);
               if (var4.umax() < var0) {
                  return var3;
               }

               var3 = var4;
            }
         }
      }
   }

   public void setFavoredCoding(CodingMethod var1) {
      this.favoredCoding = var1;
   }

   public void setTokenCoding(CodingMethod var1) {
      this.tokenCoding = var1;
      this.L = -1;
      if (var1 instanceof Coding && this.fValues != null) {
         Coding var2 = (Coding)var1;
         if (var2 == fitTokenCoding(this.fVlen, var2.L())) {
            this.L = var2.L();
         }
      }

   }

   public void setUnfavoredCoding(CodingMethod var1) {
      this.unfavoredCoding = var1;
   }

   public int favoredValueMaxLength() {
      return this.L == 0 ? Integer.MAX_VALUE : BandStructure.UNSIGNED5.setL(this.L).umax();
   }

   public void resortFavoredValues() {
      Coding var1 = (Coding)this.tokenCoding;
      this.fValues = BandStructure.realloc(this.fValues, 1 + this.fVlen);
      int var2 = 1;

      for(int var3 = 1; var3 <= var1.B(); ++var3) {
         int var4 = var1.byteMax(var3);
         if (var4 > this.fVlen) {
            var4 = this.fVlen;
         }

         if (var4 < var1.byteMin(var3)) {
            break;
         }

         int var6 = var4 + 1;
         if (var6 != var2) {
            assert var6 > var2 : var6 + "!>" + var2;

            assert var1.getLength(var2) == var3 : var3 + " != len(" + var2 + ") == " + var1.getLength(var2);

            assert var1.getLength(var6 - 1) == var3 : var3 + " != len(" + (var6 - 1) + ") == " + var1.getLength(var6 - 1);

            int var7 = var2 + (var6 - var2) / 2;
            int var8 = var2;
            int var9 = -1;
            int var10 = var2;

            for(int var11 = var2; var11 < var6; ++var11) {
               int var12 = this.fValues[var11];
               int var13 = this.vHist.getFrequency(var12);
               if (var9 != var13) {
                  if (var3 == 1) {
                     Arrays.sort(this.fValues, var10, var11);
                  } else if (Math.abs(var8 - var7) > Math.abs(var11 - var7)) {
                     var8 = var11;
                  }

                  var9 = var13;
                  var10 = var11;
               }
            }

            if (var3 == 1) {
               Arrays.sort(this.fValues, var10, var6);
            } else {
               Arrays.sort(this.fValues, var2, var8);
               Arrays.sort(this.fValues, var8, var6);
            }

            assert var1.getLength(var2) == var1.getLength(var8);

            assert var1.getLength(var2) == var1.getLength(var6 - 1);

            var2 = var4 + 1;
         }
      }

      assert var2 == this.fValues.length;

      this.symtab = null;
   }

   public int getToken(int var1) {
      if (this.symtab == null) {
         this.symtab = this.makeSymtab();
      }

      int var2 = Arrays.binarySearch(this.symtab, (long)var1 << 32);
      if (var2 < 0) {
         var2 = -var2 - 1;
      }

      return var2 < this.symtab.length && var1 == (int)(this.symtab[var2] >>> 32) ? (int)this.symtab[var2] : 0;
   }

   public int[][] encodeValues(int[] var1, int var2, int var3) {
      int[] var4 = new int[var3 - var2];
      int var5 = 0;

      int var7;
      int var8;
      for(int var6 = 0; var6 < var4.length; ++var6) {
         var7 = var1[var2 + var6];
         var8 = this.getToken(var7);
         if (var8 != 0) {
            var4[var6] = var8;
         } else {
            ++var5;
         }
      }

      int[] var9 = new int[var5];
      var5 = 0;

      for(var7 = 0; var7 < var4.length; ++var7) {
         if (var4[var7] == 0) {
            var8 = var1[var2 + var7];
            var9[var5++] = var8;
         }
      }

      assert var5 == var9.length;

      return new int[][]{var4, var9};
   }

   private long[] makeSymtab() {
      long[] var1 = new long[this.fVlen];

      for(int var2 = 1; var2 <= this.fVlen; ++var2) {
         var1[var2 - 1] = (long)this.fValues[var2] << 32 | (long)var2;
      }

      Arrays.sort(var1);
      return var1;
   }

   private Coding getTailCoding(CodingMethod var1) {
      while(var1 instanceof AdaptiveCoding) {
         var1 = ((AdaptiveCoding)var1).tailCoding;
      }

      return (Coding)var1;
   }

   public void writeArrayTo(OutputStream var1, int[] var2, int var3, int var4) throws IOException {
      int[][] var5 = this.encodeValues(var2, var3, var4);
      this.writeSequencesTo(var1, var5[0], var5[1]);
   }

   void writeSequencesTo(OutputStream var1, int[] var2, int[] var3) throws IOException {
      this.favoredCoding.writeArrayTo(var1, this.fValues, 1, 1 + this.fVlen);
      this.getTailCoding(this.favoredCoding).writeTo(var1, this.computeSentinelValue());
      this.tokenCoding.writeArrayTo(var1, var2, 0, var2.length);
      if (var3.length > 0) {
         this.unfavoredCoding.writeArrayTo(var1, var3, 0, var3.length);
      }

   }

   int computeSentinelValue() {
      Coding var1 = this.getTailCoding(this.favoredCoding);
      if (var1.isDelta()) {
         return 0;
      } else {
         int var2 = this.fValues[1];
         int var3 = var2;

         for(int var4 = 2; var4 <= this.fVlen; ++var4) {
            var3 = this.fValues[var4];
            var2 = moreCentral(var2, var3);
         }

         return var1.getLength(var2) <= var1.getLength(var3) ? var2 : var3;
      }
   }

   public void readArrayFrom(InputStream var1, int[] var2, int var3, int var4) throws IOException {
      this.setFavoredValues(this.readFavoredValuesFrom(var1, var4 - var3));
      this.tokenCoding.readArrayFrom(var1, var2, var3, var4);
      int var5 = 0;
      int var6 = -1;
      int var7 = 0;

      int var9;
      for(int var8 = var3; var8 < var4; ++var8) {
         var9 = var2[var8];
         if (var9 == 0) {
            if (var6 < 0) {
               var5 = var8;
            } else {
               var2[var6] = var8;
            }

            var6 = var8;
            ++var7;
         } else {
            var2[var8] = this.fValues[var9];
         }
      }

      int[] var11 = new int[var7];
      if (var7 > 0) {
         this.unfavoredCoding.readArrayFrom(var1, var11, 0, var7);
      }

      for(var9 = 0; var9 < var7; ++var9) {
         int var10 = var2[var5];
         var2[var5] = var11[var9];
         var5 = var10;
      }

   }

   int[] readFavoredValuesFrom(InputStream var1, int var2) throws IOException {
      int[] var3 = new int[1000];
      HashSet var4 = null;

      assert (var4 = new HashSet()) != null;

      int var5 = 1;
      var2 += var5;
      int var6 = Integer.MIN_VALUE;
      int var7 = 0;

      CodingMethod var8;
      AdaptiveCoding var9;
      int var10;
      int var12;
      for(var8 = this.favoredCoding; var8 instanceof AdaptiveCoding; var8 = var9.tailCoding) {
         var9 = (AdaptiveCoding)var8;

         for(var10 = var9.headLength; var5 + var10 > var3.length; var3 = BandStructure.realloc(var3)) {
         }

         int var11 = var5 + var10;
         var9.headCoding.readArrayFrom(var1, var3, var5, var11);

         while(var5 < var11) {
            var12 = var3[var5++];

            assert var4.add(var12);

            assert var5 <= var2;

            var7 = var12;
            var6 = moreCentral(var6, var12);
         }
      }

      Coding var13 = (Coding)var8;
      if (var13.isDelta()) {
         long var14 = 0L;

         while(true) {
            var14 += (long)var13.readFrom(var1);
            if (var13.isSubrange()) {
               var12 = var13.reduceToUnsignedRange(var14);
            } else {
               var12 = (int)var14;
            }

            var14 = (long)var12;
            if (var5 > 1 && (var12 == var7 || var12 == var6)) {
               break;
            }

            if (var5 == var3.length) {
               var3 = BandStructure.realloc(var3);
            }

            var3[var5++] = var12;

            assert var4.add(var12);

            assert var5 <= var2;

            var7 = var12;
            var6 = moreCentral(var6, var12);
         }
      } else {
         while(true) {
            var10 = var13.readFrom(var1);
            if (var5 > 1 && (var10 == var7 || var10 == var6)) {
               break;
            }

            if (var5 == var3.length) {
               var3 = BandStructure.realloc(var3);
            }

            var3[var5++] = var10;

            assert var4.add(var10);

            assert var5 <= var2;

            var7 = var10;
            var6 = moreCentral(var6, var10);
         }
      }

      return BandStructure.realloc(var3, var5);
   }

   private static int moreCentral(int var0, int var1) {
      int var2 = var0 >> 31 ^ var0 << 1;
      int var3 = var1 >> 31 ^ var1 << 1;
      var2 -= Integer.MIN_VALUE;
      var3 -= Integer.MIN_VALUE;
      int var4 = var2 < var3 ? var0 : var1;

      assert var4 == moreCentralSlow(var0, var1);

      return var4;
   }

   private static int moreCentralSlow(int var0, int var1) {
      int var2 = var0;
      if (var0 < 0) {
         var2 = -var0;
      }

      if (var2 < 0) {
         return var1;
      } else {
         int var3 = var1;
         if (var1 < 0) {
            var3 = -var1;
         }

         if (var3 < 0) {
            return var0;
         } else if (var2 < var3) {
            return var0;
         } else if (var2 > var3) {
            return var1;
         } else {
            return var0 < var1 ? var0 : var1;
         }
      }
   }

   public byte[] getMetaCoding(Coding var1) {
      int var2 = this.fVlen;
      int var3 = 0;
      int var5;
      if (this.tokenCoding instanceof Coding) {
         Coding var4 = (Coding)this.tokenCoding;
         if (var4.B() == 1) {
            var3 = 1;
         } else if (this.L >= 0) {
            assert this.L == var4.L();

            for(var5 = 1; var5 < LValuesCoded.length; ++var5) {
               if (LValuesCoded[var5] == this.L) {
                  var3 = var5;
                  break;
               }
            }
         }
      }

      CodingMethod var12 = null;
      if (var3 != 0 && this.tokenCoding == fitTokenCoding(this.fVlen, this.L)) {
         var12 = this.tokenCoding;
      }

      var5 = this.favoredCoding == var1 ? 1 : 0;
      int var6 = this.unfavoredCoding != var1 && this.unfavoredCoding != null ? 0 : 1;
      boolean var7 = this.tokenCoding == var12;
      int var8 = var7 ? var3 : 0;

      assert var7 == var8 > 0;

      ByteArrayOutputStream var9 = new ByteArrayOutputStream(10);
      var9.write(141 + var5 + 2 * var6 + 4 * var8);

      try {
         if (var5 == 0) {
            var9.write(this.favoredCoding.getMetaCoding(var1));
         }

         if (!var7) {
            var9.write(this.tokenCoding.getMetaCoding(var1));
         }

         if (var6 == 0) {
            var9.write(this.unfavoredCoding.getMetaCoding(var1));
         }
      } catch (IOException var11) {
         throw new RuntimeException(var11);
      }

      return var9.toByteArray();
   }

   public static int parseMetaCoding(byte[] var0, int var1, Coding var2, CodingMethod[] var3) {
      int var4 = var0[var1++] & 255;
      if (var4 >= 141 && var4 < 189) {
         var4 -= 141;
         int var5 = var4 % 2;
         int var6 = var4 / 2 % 2;
         int var7 = var4 / 4;
         boolean var8 = var7 > 0;
         int var9 = LValuesCoded[var7];
         CodingMethod[] var10 = new CodingMethod[]{var2};
         CodingMethod[] var11 = new CodingMethod[]{null};
         CodingMethod[] var12 = new CodingMethod[]{var2};
         if (var5 == 0) {
            var1 = BandStructure.parseMetaCoding(var0, var1, var2, var10);
         }

         if (!var8) {
            var1 = BandStructure.parseMetaCoding(var0, var1, var2, var11);
         }

         if (var6 == 0) {
            var1 = BandStructure.parseMetaCoding(var0, var1, var2, var12);
         }

         PopulationCoding var13 = new PopulationCoding();
         var13.L = var9;
         var13.favoredCoding = var10[0];
         var13.tokenCoding = var11[0];
         var13.unfavoredCoding = var12[0];
         var3[0] = var13;
         return var1;
      } else {
         return var1 - 1;
      }
   }

   private String keyString(CodingMethod var1) {
      if (var1 instanceof Coding) {
         return ((Coding)var1).keyString();
      } else {
         return var1 == null ? "none" : var1.toString();
      }
   }

   public String toString() {
      PropMap var1 = Utils.currentPropMap();
      boolean var2 = var1 != null && var1.getBoolean("com.sun.java.util.jar.pack.verbose.pop");
      StringBuilder var3 = new StringBuilder(100);
      var3.append("pop(").append("fVlen=").append(this.fVlen);
      if (var2 && this.fValues != null) {
         var3.append(" fV=[");

         for(int var4 = 1; var4 <= this.fVlen; ++var4) {
            var3.append(var4 == 1 ? "" : ",").append(this.fValues[var4]);
         }

         var3.append(";").append(this.computeSentinelValue());
         var3.append("]");
      }

      var3.append(" fc=").append(this.keyString(this.favoredCoding));
      var3.append(" tc=").append(this.keyString(this.tokenCoding));
      var3.append(" uc=").append(this.keyString(this.unfavoredCoding));
      var3.append(")");
      return var3.toString();
   }
}
