package com.sun.imageio.plugins.common;

import java.io.PrintStream;

public class LZWStringTable {
   private static final int RES_CODES = 2;
   private static final short HASH_FREE = -1;
   private static final short NEXT_FIRST = -1;
   private static final int MAXBITS = 12;
   private static final int MAXSTR = 4096;
   private static final short HASHSIZE = 9973;
   private static final short HASHSTEP = 2039;
   byte[] strChr = new byte[4096];
   short[] strNxt = new short[4096];
   short[] strHsh = new short[9973];
   short numStrings;
   int[] strLen = new int[4096];

   public int addCharString(short var1, byte var2) {
      if (this.numStrings >= 4096) {
         return 65535;
      } else {
         int var3;
         for(var3 = hash(var1, var2); this.strHsh[var3] != -1; var3 = (var3 + 2039) % 9973) {
         }

         this.strHsh[var3] = this.numStrings;
         this.strChr[this.numStrings] = var2;
         if (var1 == -1) {
            this.strNxt[this.numStrings] = -1;
            this.strLen[this.numStrings] = 1;
         } else {
            this.strNxt[this.numStrings] = var1;
            this.strLen[this.numStrings] = this.strLen[var1] + 1;
         }

         short var10002 = this.numStrings;
         this.numStrings = (short)(var10002 + 1);
         return var10002;
      }
   }

   public short findCharString(short var1, byte var2) {
      if (var1 == -1) {
         return (short)(var2 & 255);
      } else {
         short var4;
         for(int var3 = hash(var1, var2); (var4 = this.strHsh[var3]) != -1; var3 = (var3 + 2039) % 9973) {
            if (this.strNxt[var4] == var1 && this.strChr[var4] == var2) {
               return (short)var4;
            }
         }

         return -1;
      }
   }

   public void clearTable(int var1) {
      this.numStrings = 0;

      int var2;
      for(var2 = 0; var2 < 9973; ++var2) {
         this.strHsh[var2] = -1;
      }

      var2 = (1 << var1) + 2;

      for(int var3 = 0; var3 < var2; ++var3) {
         this.addCharString((short)-1, (byte)var3);
      }

   }

   public static int hash(short var0, byte var1) {
      return (((short)(var1 << 8) ^ var0) & '\uffff') % 9973;
   }

   public int expandCode(byte[] var1, int var2, short var3, int var4) {
      if (var2 == -2 && var4 == 1) {
         var4 = 0;
      }

      if (var3 != -1 && var4 != this.strLen[var3]) {
         int var6 = this.strLen[var3] - var4;
         int var7 = var1.length - var2;
         int var5;
         if (var7 > var6) {
            var5 = var6;
         } else {
            var5 = var7;
         }

         int var8 = var6 - var5;

         for(int var9 = var2 + var5; var9 > var2 && var3 != -1; var3 = this.strNxt[var3]) {
            --var8;
            if (var8 < 0) {
               --var9;
               var1[var9] = this.strChr[var3];
            }
         }

         return var6 > var5 ? -var5 : var5;
      } else {
         return 0;
      }
   }

   public void dump(PrintStream var1) {
      for(int var2 = 258; var2 < this.numStrings; ++var2) {
         var1.println(" strNxt[" + var2 + "] = " + this.strNxt[var2] + " strChr " + Integer.toHexString(this.strChr[var2] & 255) + " strLen " + Integer.toHexString(this.strLen[var2]));
      }

   }
}
