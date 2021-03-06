package java.lang;

class CharacterData0E extends CharacterData {
   static final CharacterData instance = new CharacterData0E();
   static final char[] X = "\u0000\u0010\u0010\u0010    0000000@                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ".toCharArray();
   static final char[] Y = "\u0000\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0004\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0006\u0002\u0002\u0002\u0002\u0002\u0002\u0002\u0002".toCharArray();
   static final int[] A = new int[8];
   static final String A_DATA = "???\u0000?????????\u0000???\u0000????????????????????????";
   static final char[] B = "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000".toCharArray();

   int getProperties(int var1) {
      char var2 = (char)var1;
      int var3 = A[Y[X[var2 >> 5] | var2 >> 1 & 15] | var2 & 1];
      return var3;
   }

   int getPropertiesEx(int var1) {
      char var2 = (char)var1;
      char var3 = B[Y[X[var2 >> 5] | var2 >> 1 & 15] | var2 & 1];
      return var3;
   }

   boolean isOtherLowercase(int var1) {
      int var2 = this.getPropertiesEx(var1);
      return (var2 & 1) != 0;
   }

   boolean isOtherUppercase(int var1) {
      int var2 = this.getPropertiesEx(var1);
      return (var2 & 2) != 0;
   }

   boolean isOtherAlphabetic(int var1) {
      int var2 = this.getPropertiesEx(var1);
      return (var2 & 4) != 0;
   }

   boolean isIdeographic(int var1) {
      int var2 = this.getPropertiesEx(var1);
      return (var2 & 16) != 0;
   }

   int getType(int var1) {
      int var2 = this.getProperties(var1);
      return var2 & 31;
   }

   boolean isJavaIdentifierStart(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 28672) >= 20480;
   }

   boolean isJavaIdentifierPart(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 12288) != 0;
   }

   boolean isUnicodeIdentifierStart(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 28672) == 28672;
   }

   boolean isUnicodeIdentifierPart(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 4096) != 0;
   }

   boolean isIdentifierIgnorable(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 28672) == 4096;
   }

   int toLowerCase(int var1) {
      int var2 = var1;
      int var3 = this.getProperties(var1);
      if ((var3 & 131072) != 0) {
         int var4 = var3 << 5 >> 23;
         var2 = var1 + var4;
      }

      return var2;
   }

   int toUpperCase(int var1) {
      int var2 = var1;
      int var3 = this.getProperties(var1);
      if ((var3 & 65536) != 0) {
         int var4 = var3 << 5 >> 23;
         var2 = var1 - var4;
      }

      return var2;
   }

   int toTitleCase(int var1) {
      int var2 = var1;
      int var3 = this.getProperties(var1);
      if ((var3 & '???') != 0) {
         if ((var3 & 65536) == 0) {
            var2 = var1 + 1;
         } else if ((var3 & 131072) == 0) {
            var2 = var1 - 1;
         }
      } else if ((var3 & 65536) != 0) {
         var2 = this.toUpperCase(var1);
      }

      return var2;
   }

   int digit(int var1, int var2) {
      int var3 = -1;
      if (var2 >= 2 && var2 <= 36) {
         int var4 = this.getProperties(var1);
         int var5 = var4 & 31;
         if (var5 == 9) {
            var3 = var1 + ((var4 & 992) >> 5) & 31;
         } else if ((var4 & 3072) == 3072) {
            var3 = (var1 + ((var4 & 992) >> 5) & 31) + 10;
         }
      }

      return var3 < var2 ? var3 : -1;
   }

   int getNumericValue(int var1) {
      int var2 = this.getProperties(var1);
      boolean var3 = true;
      int var4;
      switch(var2 & 3072) {
      case 0:
      default:
         var4 = -1;
         break;
      case 1024:
         var4 = var1 + ((var2 & 992) >> 5) & 31;
         break;
      case 2048:
         var4 = -2;
         break;
      case 3072:
         var4 = (var1 + ((var2 & 992) >> 5) & 31) + 10;
      }

      return var4;
   }

   boolean isWhitespace(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & 28672) == 16384;
   }

   byte getDirectionality(int var1) {
      int var2 = this.getProperties(var1);
      byte var3 = (byte)((var2 & 2013265920) >> 27);
      if (var3 == 15) {
         var3 = -1;
      }

      return var3;
   }

   boolean isMirrored(int var1) {
      int var2 = this.getProperties(var1);
      return (var2 & Integer.MIN_VALUE) != 0;
   }

   private CharacterData0E() {
   }

   static {
      char[] var0 = "???\u0000?????????\u0000???\u0000????????????????????????".toCharArray();

      assert var0.length == 16;

      int var1 = 0;

      int var3;
      for(int var2 = 0; var1 < 16; A[var2++] = var3 | var0[var1++]) {
         var3 = var0[var1++] << 16;
      }

   }
}
