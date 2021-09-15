package java.util.regex;

final class ASCII {
   static final int UPPER = 256;
   static final int LOWER = 512;
   static final int DIGIT = 1024;
   static final int SPACE = 2048;
   static final int PUNCT = 4096;
   static final int CNTRL = 8192;
   static final int BLANK = 16384;
   static final int HEX = 32768;
   static final int UNDER = 65536;
   static final int ASCII = 65280;
   static final int ALPHA = 768;
   static final int ALNUM = 1792;
   static final int GRAPH = 5888;
   static final int WORD = 67328;
   static final int XDIGIT = 32768;
   private static final int[] ctype = new int[]{8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 26624, 10240, 10240, 10240, 10240, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 8192, 18432, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 33792, 33793, 33794, 33795, 33796, 33797, 33798, 33799, 33800, 33801, 4096, 4096, 4096, 4096, 4096, 4096, 4096, 33034, 33035, 33036, 33037, 33038, 33039, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 4096, 4096, 4096, 4096, 69632, 4096, 33290, 33291, 33292, 33293, 33294, 33295, 528, 529, 530, 531, 532, 533, 534, 535, 536, 537, 538, 539, 540, 541, 542, 543, 544, 545, 546, 547, 4096, 4096, 4096, 4096, 8192};

   static int getType(int var0) {
      return (var0 & -128) == 0 ? ctype[var0] : 0;
   }

   static boolean isType(int var0, int var1) {
      return (getType(var0) & var1) != 0;
   }

   static boolean isAscii(int var0) {
      return (var0 & -128) == 0;
   }

   static boolean isAlpha(int var0) {
      return isType(var0, 768);
   }

   static boolean isDigit(int var0) {
      return (var0 - 48 | 57 - var0) >= 0;
   }

   static boolean isAlnum(int var0) {
      return isType(var0, 1792);
   }

   static boolean isGraph(int var0) {
      return isType(var0, 5888);
   }

   static boolean isPrint(int var0) {
      return (var0 - 32 | 126 - var0) >= 0;
   }

   static boolean isPunct(int var0) {
      return isType(var0, 4096);
   }

   static boolean isSpace(int var0) {
      return isType(var0, 2048);
   }

   static boolean isHexDigit(int var0) {
      return isType(var0, 32768);
   }

   static boolean isOctDigit(int var0) {
      return (var0 - 48 | 55 - var0) >= 0;
   }

   static boolean isCntrl(int var0) {
      return isType(var0, 8192);
   }

   static boolean isLower(int var0) {
      return (var0 - 97 | 122 - var0) >= 0;
   }

   static boolean isUpper(int var0) {
      return (var0 - 65 | 90 - var0) >= 0;
   }

   static boolean isWord(int var0) {
      return isType(var0, 67328);
   }

   static int toDigit(int var0) {
      return ctype[var0 & 127] & 63;
   }

   static int toLower(int var0) {
      return isUpper(var0) ? var0 + 32 : var0;
   }

   static int toUpper(int var0) {
      return isLower(var0) ? var0 - 32 : var0;
   }
}
