package com.sun.imageio.plugins.bmp;

public class BMPCompressionTypes {
   private static final String[] compressionTypeNames = new String[]{"BI_RGB", "BI_RLE8", "BI_RLE4", "BI_BITFIELDS", "BI_JPEG", "BI_PNG"};

   static int getType(String var0) {
      for(int var1 = 0; var1 < compressionTypeNames.length; ++var1) {
         if (compressionTypeNames[var1].equals(var0)) {
            return var1;
         }
      }

      return 0;
   }

   static String getName(int var0) {
      return compressionTypeNames[var0];
   }

   public static String[] getCompressionTypes() {
      return (String[])compressionTypeNames.clone();
   }
}
