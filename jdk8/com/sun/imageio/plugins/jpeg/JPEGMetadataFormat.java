package com.sun.imageio.plugins.jpeg;

import java.util.ArrayList;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;

abstract class JPEGMetadataFormat extends IIOMetadataFormatImpl {
   private static final int MAX_JPEG_DATA_SIZE = 65533;
   String resourceBaseName = this.getClass().getName() + "Resources";

   JPEGMetadataFormat(String var1, int var2) {
      super(var1, var2);
      this.setResourceBaseName(this.resourceBaseName);
   }

   void addStreamElements(String var1) {
      this.addElement("dqt", var1, 1, 4);
      this.addElement("dqtable", "dqt", 0);
      this.addAttribute("dqtable", "elementPrecision", 2, false, "0");
      ArrayList var2 = new ArrayList();
      var2.add("0");
      var2.add("1");
      var2.add("2");
      var2.add("3");
      this.addAttribute("dqtable", "qtableId", 2, true, (String)null, var2);
      this.addObjectValue("dqtable", JPEGQTable.class, true, (Object)null);
      this.addElement("dht", var1, 1, 4);
      this.addElement("dhtable", "dht", 0);
      ArrayList var3 = new ArrayList();
      var3.add("0");
      var3.add("1");
      this.addAttribute("dhtable", "class", 2, true, (String)null, var3);
      this.addAttribute("dhtable", "htableId", 2, true, (String)null, var2);
      this.addObjectValue("dhtable", JPEGHuffmanTable.class, true, (Object)null);
      this.addElement("dri", var1, 0);
      this.addAttribute("dri", "interval", 2, true, (String)null, "0", "65535", true, true);
      this.addElement("com", var1, 0);
      this.addAttribute("com", "comment", 0, false, (String)null);
      this.addObjectValue("com", byte[].class, 1, 65533);
      this.addElement("unknown", var1, 0);
      this.addAttribute("unknown", "MarkerTag", 2, true, (String)null, "0", "255", true, true);
      this.addObjectValue("unknown", byte[].class, 1, 65533);
   }

   public boolean canNodeAppear(String var1, ImageTypeSpecifier var2) {
      return this.isInSubtree(var1, this.getRootName());
   }

   protected boolean isInSubtree(String var1, String var2) {
      if (var1.equals(var2)) {
         return true;
      } else {
         String[] var3 = this.getChildNames(var1);

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (this.isInSubtree(var1, var3[var4])) {
               return true;
            }
         }

         return false;
      }
   }
}
