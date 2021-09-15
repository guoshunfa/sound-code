package javax.imageio.plugins.jpeg;

import java.util.Arrays;

public class JPEGHuffmanTable {
   private static final short[] StdDCLuminanceLengths = new short[]{0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0};
   private static final short[] StdDCLuminanceValues = new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
   private static final short[] StdDCChrominanceLengths = new short[]{0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0};
   private static final short[] StdDCChrominanceValues = new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
   private static final short[] StdACLuminanceLengths = new short[]{0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 125};
   private static final short[] StdACLuminanceValues = new short[]{1, 2, 3, 0, 4, 17, 5, 18, 33, 49, 65, 6, 19, 81, 97, 7, 34, 113, 20, 50, 129, 145, 161, 8, 35, 66, 177, 193, 21, 82, 209, 240, 36, 51, 98, 114, 130, 9, 10, 22, 23, 24, 25, 26, 37, 38, 39, 40, 41, 42, 52, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250};
   private static final short[] StdACChrominanceLengths = new short[]{0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 119};
   private static final short[] StdACChrominanceValues = new short[]{0, 1, 2, 3, 17, 4, 5, 33, 49, 6, 18, 65, 81, 7, 97, 113, 19, 34, 50, 129, 8, 20, 66, 145, 161, 177, 193, 9, 35, 51, 82, 240, 21, 98, 114, 209, 10, 22, 36, 52, 225, 37, 241, 23, 24, 25, 26, 38, 39, 40, 41, 42, 53, 54, 55, 56, 57, 58, 67, 68, 69, 70, 71, 72, 73, 74, 83, 84, 85, 86, 87, 88, 89, 90, 99, 100, 101, 102, 103, 104, 105, 106, 115, 116, 117, 118, 119, 120, 121, 122, 130, 131, 132, 133, 134, 135, 136, 137, 138, 146, 147, 148, 149, 150, 151, 152, 153, 154, 162, 163, 164, 165, 166, 167, 168, 169, 170, 178, 179, 180, 181, 182, 183, 184, 185, 186, 194, 195, 196, 197, 198, 199, 200, 201, 202, 210, 211, 212, 213, 214, 215, 216, 217, 218, 226, 227, 228, 229, 230, 231, 232, 233, 234, 242, 243, 244, 245, 246, 247, 248, 249, 250};
   public static final JPEGHuffmanTable StdDCLuminance;
   public static final JPEGHuffmanTable StdDCChrominance;
   public static final JPEGHuffmanTable StdACLuminance;
   public static final JPEGHuffmanTable StdACChrominance;
   private short[] lengths;
   private short[] values;

   public JPEGHuffmanTable(short[] var1, short[] var2) {
      if (var1 != null && var2 != null && var1.length != 0 && var2.length != 0 && var1.length <= 16 && var2.length <= 256) {
         int var3;
         for(var3 = 0; var3 < var1.length; ++var3) {
            if (var1[var3] < 0) {
               throw new IllegalArgumentException("lengths[" + var3 + "] < 0");
            }
         }

         for(var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3] < 0) {
               throw new IllegalArgumentException("values[" + var3 + "] < 0");
            }
         }

         this.lengths = Arrays.copyOf(var1, var1.length);
         this.values = Arrays.copyOf(var2, var2.length);
         this.validate();
      } else {
         throw new IllegalArgumentException("Illegal lengths or values");
      }
   }

   private void validate() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.lengths.length; ++var2) {
         var1 += this.lengths[var2];
      }

      if (var1 != this.values.length) {
         throw new IllegalArgumentException("lengths do not correspond to length of value table");
      }
   }

   private JPEGHuffmanTable(short[] var1, short[] var2, boolean var3) {
      if (var3) {
         this.lengths = Arrays.copyOf(var1, var1.length);
         this.values = Arrays.copyOf(var2, var2.length);
      } else {
         this.lengths = var1;
         this.values = var2;
      }

   }

   public short[] getLengths() {
      return Arrays.copyOf(this.lengths, this.lengths.length);
   }

   public short[] getValues() {
      return Arrays.copyOf(this.values, this.values.length);
   }

   public String toString() {
      String var1 = System.getProperty("line.separator", "\n");
      StringBuilder var2 = new StringBuilder("JPEGHuffmanTable");
      var2.append(var1).append("lengths:");

      int var3;
      for(var3 = 0; var3 < this.lengths.length; ++var3) {
         var2.append(" ").append((int)this.lengths[var3]);
      }

      var2.append(var1).append("values:");

      for(var3 = 0; var3 < this.values.length; ++var3) {
         var2.append(" ").append((int)this.values[var3]);
      }

      return var2.toString();
   }

   static {
      StdDCLuminance = new JPEGHuffmanTable(StdDCLuminanceLengths, StdDCLuminanceValues, false);
      StdDCChrominance = new JPEGHuffmanTable(StdDCChrominanceLengths, StdDCChrominanceValues, false);
      StdACLuminance = new JPEGHuffmanTable(StdACLuminanceLengths, StdACLuminanceValues, false);
      StdACChrominance = new JPEGHuffmanTable(StdACChrominanceLengths, StdACChrominanceValues, false);
   }
}
