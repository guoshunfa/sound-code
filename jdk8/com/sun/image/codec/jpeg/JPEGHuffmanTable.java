package com.sun.image.codec.jpeg;

public class JPEGHuffmanTable {
   private static final int HUFF_MAX_LEN = 17;
   private static final int HUFF_MAX_SYM = 256;
   private short[] lengths;
   private short[] symbols;
   public static final JPEGHuffmanTable StdDCLuminance = new JPEGHuffmanTable();
   public static final JPEGHuffmanTable StdDCChrominance;
   public static final JPEGHuffmanTable StdACLuminance;
   public static final JPEGHuffmanTable StdACChrominance;

   private JPEGHuffmanTable() {
      this.lengths = null;
      this.symbols = null;
   }

   public JPEGHuffmanTable(short[] var1, short[] var2) {
      if (var1.length > 17) {
         throw new IllegalArgumentException("lengths array is too long");
      } else {
         int var3;
         for(var3 = 1; var3 < var1.length; ++var3) {
            if (var1[var3] < 0) {
               throw new IllegalArgumentException("Values in lengths array must be non-negative.");
            }
         }

         if (var2.length > 256) {
            throw new IllegalArgumentException("symbols array is too long");
         } else {
            for(var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] < 0) {
                  throw new IllegalArgumentException("Values in symbols array must be non-negative.");
               }
            }

            this.lengths = new short[var1.length];
            this.symbols = new short[var2.length];
            System.arraycopy(var1, 0, this.lengths, 0, var1.length);
            System.arraycopy(var2, 0, this.symbols, 0, var2.length);
            this.checkTable();
         }
      }
   }

   private void checkTable() {
      int var1 = 2;
      int var2 = 0;

      for(int var3 = 1; var3 < this.lengths.length; ++var3) {
         var2 += this.lengths[var3];
         var1 -= this.lengths[var3];
         var1 *= 2;
      }

      if (var1 < 0) {
         throw new IllegalArgumentException("Invalid Huffman Table provided, lengths are incorrect.");
      } else if (var2 > this.symbols.length) {
         throw new IllegalArgumentException("Invalid Huffman Table provided, not enough symbols.");
      }
   }

   public short[] getLengths() {
      short[] var1 = new short[this.lengths.length];
      System.arraycopy(this.lengths, 0, var1, 0, this.lengths.length);
      return var1;
   }

   public short[] getSymbols() {
      short[] var1 = new short[this.symbols.length];
      System.arraycopy(this.symbols, 0, var1, 0, this.symbols.length);
      return var1;
   }

   static {
      short[] var0 = new short[]{0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
      short[] var1 = new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
      StdDCLuminance.lengths = var0;
      StdDCLuminance.symbols = var1;
      StdDCLuminance.checkTable();
      StdDCChrominance = new JPEGHuffmanTable();
      var0 = new short[]{0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
      var1 = new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
      StdDCChrominance.lengths = var0;
      StdDCChrominance.symbols = var1;
      StdDCChrominance.checkTable();
      StdACLuminance = new JPEGHuffmanTable();
      var0 = new short[]{0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125};
      var1 = new short[]{1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250};
      StdACLuminance.lengths = var0;
      StdACLuminance.symbols = var1;
      StdACLuminance.checkTable();
      StdACChrominance = new JPEGHuffmanTable();
      var0 = new short[]{0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119};
      var1 = new short[]{0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250};
      StdACChrominance.lengths = var0;
      StdACChrominance.symbols = var1;
      StdACChrominance.checkTable();
   }
}
