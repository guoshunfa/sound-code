package com.sun.imageio.plugins.jpeg;

public class JPEGStreamMetadataFormatResources extends JPEGMetadataFormatResources {
   protected Object[][] getContents() {
      Object[][] var1 = new Object[commonContents.length][2];

      for(int var2 = 0; var2 < commonContents.length; ++var2) {
         var1[var2][0] = commonContents[var2][0];
         var1[var2][1] = commonContents[var2][1];
      }

      return var1;
   }
}
