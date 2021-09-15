package sun.nio.cs;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Comparator;

public class CharsetMapping {
   public static final char UNMAPPABLE_DECODING = '�';
   public static final int UNMAPPABLE_ENCODING = 65533;
   char[] b2cSB;
   char[] b2cDB1;
   char[] b2cDB2;
   int b2Min;
   int b2Max;
   int b1MinDB1;
   int b1MaxDB1;
   int b1MinDB2;
   int b1MaxDB2;
   int dbSegSize;
   char[] c2b;
   char[] c2bIndex;
   char[] b2cSupp;
   char[] c2bSupp;
   CharsetMapping.Entry[] b2cComp;
   CharsetMapping.Entry[] c2bComp;
   static Comparator<CharsetMapping.Entry> comparatorBytes = new Comparator<CharsetMapping.Entry>() {
      public int compare(CharsetMapping.Entry var1, CharsetMapping.Entry var2) {
         return var1.bs - var2.bs;
      }

      public boolean equals(Object var1) {
         return this == var1;
      }
   };
   static Comparator<CharsetMapping.Entry> comparatorCP = new Comparator<CharsetMapping.Entry>() {
      public int compare(CharsetMapping.Entry var1, CharsetMapping.Entry var2) {
         return var1.cp - var2.cp;
      }

      public boolean equals(Object var1) {
         return this == var1;
      }
   };
   static Comparator<CharsetMapping.Entry> comparatorComp = new Comparator<CharsetMapping.Entry>() {
      public int compare(CharsetMapping.Entry var1, CharsetMapping.Entry var2) {
         int var3 = var1.cp - var2.cp;
         if (var3 == 0) {
            var3 = var1.cp2 - var2.cp2;
         }

         return var3;
      }

      public boolean equals(Object var1) {
         return this == var1;
      }
   };
   private static final int MAP_SINGLEBYTE = 1;
   private static final int MAP_DOUBLEBYTE1 = 2;
   private static final int MAP_DOUBLEBYTE2 = 3;
   private static final int MAP_SUPPLEMENT = 5;
   private static final int MAP_SUPPLEMENT_C2B = 6;
   private static final int MAP_COMPOSITE = 7;
   private static final int MAP_INDEXC2B = 8;
   int off = 0;
   byte[] bb;

   public char decodeSingle(int var1) {
      return this.b2cSB[var1];
   }

   public char decodeDouble(int var1, int var2) {
      if (var2 >= this.b2Min && var2 < this.b2Max) {
         var2 -= this.b2Min;
         if (var1 >= this.b1MinDB1 && var1 <= this.b1MaxDB1) {
            var1 -= this.b1MinDB1;
            return this.b2cDB1[var1 * this.dbSegSize + var2];
         }

         if (var1 >= this.b1MinDB2 && var1 <= this.b1MaxDB2) {
            var1 -= this.b1MinDB2;
            return this.b2cDB2[var1 * this.dbSegSize + var2];
         }
      }

      return '�';
   }

   public char[] decodeSurrogate(int var1, char[] var2) {
      int var3 = this.b2cSupp.length / 2;
      int var4 = Arrays.binarySearch((char[])this.b2cSupp, 0, var3, (char)((char)var1));
      if (var4 >= 0) {
         Character.toChars(this.b2cSupp[var3 + var4] + 131072, var2, 0);
         return var2;
      } else {
         return null;
      }
   }

   public char[] decodeComposite(CharsetMapping.Entry var1, char[] var2) {
      int var3 = findBytes(this.b2cComp, var1);
      if (var3 >= 0) {
         var2[0] = (char)this.b2cComp[var3].cp;
         var2[1] = (char)this.b2cComp[var3].cp2;
         return var2;
      } else {
         return null;
      }
   }

   public int encodeChar(char var1) {
      char var2 = this.c2bIndex[var1 >> 8];
      return var2 == '\uffff' ? '�' : this.c2b[var2 + (var1 & 255)];
   }

   public int encodeSurrogate(char var1, char var2) {
      int var3 = Character.toCodePoint(var1, var2);
      if (var3 >= 131072 && var3 < 196608) {
         int var4 = this.c2bSupp.length / 2;
         int var5 = Arrays.binarySearch((char[])this.c2bSupp, 0, var4, (char)((char)var3));
         return var5 >= 0 ? this.c2bSupp[var4 + var5] : '�';
      } else {
         return 65533;
      }
   }

   public boolean isCompositeBase(CharsetMapping.Entry var1) {
      if (var1.cp <= 12791 && var1.cp >= 230) {
         return findCP(this.c2bComp, var1) >= 0;
      } else {
         return false;
      }
   }

   public int encodeComposite(CharsetMapping.Entry var1) {
      int var2 = findComp(this.c2bComp, var1);
      return var2 >= 0 ? this.c2bComp[var2].bs : '�';
   }

   public static CharsetMapping get(final InputStream var0) {
      return (CharsetMapping)AccessController.doPrivileged(new PrivilegedAction<CharsetMapping>() {
         public CharsetMapping run() {
            return (new CharsetMapping()).load(var0);
         }
      });
   }

   static int findBytes(CharsetMapping.Entry[] var0, CharsetMapping.Entry var1) {
      return Arrays.binarySearch(var0, 0, var0.length, var1, comparatorBytes);
   }

   static int findCP(CharsetMapping.Entry[] var0, CharsetMapping.Entry var1) {
      return Arrays.binarySearch(var0, 0, var0.length, var1, comparatorCP);
   }

   static int findComp(CharsetMapping.Entry[] var0, CharsetMapping.Entry var1) {
      return Arrays.binarySearch(var0, 0, var0.length, var1, comparatorComp);
   }

   private static final boolean readNBytes(InputStream var0, byte[] var1, int var2) throws IOException {
      int var4;
      for(int var3 = 0; var2 > 0; var3 += var4) {
         var4 = var0.read(var1, var3, var2);
         if (var4 == -1) {
            return false;
         }

         var2 -= var4;
      }

      return true;
   }

   private char[] readCharArray() {
      int var1 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      char[] var2 = new char[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = (char)((this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255);
      }

      return var2;
   }

   void readSINGLEBYTE() {
      char[] var1 = this.readCharArray();

      for(int var2 = 0; var2 < var1.length; ++var2) {
         char var3 = var1[var2];
         if (var3 != '�') {
            this.c2b[this.c2bIndex[var3 >> 8] + (var3 & 255)] = (char)var2;
         }
      }

      this.b2cSB = var1;
   }

   void readINDEXC2B() {
      char[] var1 = this.readCharArray();

      for(int var2 = var1.length - 1; var2 >= 0; --var2) {
         if (this.c2b == null && var1[var2] != -1) {
            this.c2b = new char[var1[var2] + 256];
            Arrays.fill(this.c2b, '�');
            break;
         }
      }

      this.c2bIndex = var1;
   }

   char[] readDB(int var1, int var2, int var3) {
      char[] var4 = this.readCharArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         char var6 = var4[var5];
         if (var6 != '�') {
            int var7 = var5 / var3;
            int var8 = var5 % var3;
            int var9 = (var7 + var1) * 256 + var8 + var2;
            this.c2b[this.c2bIndex[var6 >> 8] + (var6 & 255)] = (char)var9;
         }
      }

      return var4;
   }

   void readDOUBLEBYTE1() {
      this.b1MinDB1 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b1MaxDB1 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b2Min = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b2Max = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.dbSegSize = this.b2Max - this.b2Min + 1;
      this.b2cDB1 = this.readDB(this.b1MinDB1, this.b2Min, this.dbSegSize);
   }

   void readDOUBLEBYTE2() {
      this.b1MinDB2 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b1MaxDB2 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b2Min = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.b2Max = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
      this.dbSegSize = this.b2Max - this.b2Min + 1;
      this.b2cDB2 = this.readDB(this.b1MinDB2, this.b2Min, this.dbSegSize);
   }

   void readCOMPOSITE() {
      char[] var1 = this.readCharArray();
      int var2 = var1.length / 3;
      this.b2cComp = new CharsetMapping.Entry[var2];
      this.c2bComp = new CharsetMapping.Entry[var2];
      int var3 = 0;

      for(int var4 = 0; var3 < var2; ++var3) {
         CharsetMapping.Entry var5 = new CharsetMapping.Entry();
         var5.bs = var1[var4++];
         var5.cp = var1[var4++];
         var5.cp2 = var1[var4++];
         this.b2cComp[var3] = var5;
         this.c2bComp[var3] = var5;
      }

      Arrays.sort(this.c2bComp, 0, this.c2bComp.length, comparatorComp);
   }

   CharsetMapping load(InputStream var1) {
      try {
         int var2 = (var1.read() & 255) << 24 | (var1.read() & 255) << 16 | (var1.read() & 255) << 8 | var1.read() & 255;
         this.bb = new byte[var2];
         this.off = 0;
         if (!readNBytes(var1, this.bb, var2)) {
            throw new RuntimeException("Corrupted data file");
         } else {
            var1.close();

            while(this.off < var2) {
               int var3 = (this.bb[this.off++] & 255) << 8 | this.bb[this.off++] & 255;
               switch(var3) {
               case 1:
                  this.readSINGLEBYTE();
                  break;
               case 2:
                  this.readDOUBLEBYTE1();
                  break;
               case 3:
                  this.readDOUBLEBYTE2();
                  break;
               case 4:
               default:
                  throw new RuntimeException("Corrupted data file");
               case 5:
                  this.b2cSupp = this.readCharArray();
                  break;
               case 6:
                  this.c2bSupp = this.readCharArray();
                  break;
               case 7:
                  this.readCOMPOSITE();
                  break;
               case 8:
                  this.readINDEXC2B();
               }
            }

            this.bb = null;
            return this;
         }
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static class Entry {
      public int bs;
      public int cp;
      public int cp2;
   }
}
