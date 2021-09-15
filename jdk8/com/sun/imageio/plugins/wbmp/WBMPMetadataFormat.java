package com.sun.imageio.plugins.wbmp;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class WBMPMetadataFormat extends IIOMetadataFormatImpl {
   private static IIOMetadataFormat instance = null;

   private WBMPMetadataFormat() {
      super("javax_imageio_wbmp_1.0", 2);
      this.addElement("ImageDescriptor", "javax_imageio_wbmp_1.0", 0);
      this.addAttribute("ImageDescriptor", "WBMPType", 2, true, "0");
      this.addAttribute("ImageDescriptor", "Width", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("ImageDescriptor", "Height", 2, true, (String)null, "1", "65535", true, true);
   }

   public boolean canNodeAppear(String var1, ImageTypeSpecifier var2) {
      return true;
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (instance == null) {
         instance = new WBMPMetadataFormat();
      }

      return instance;
   }
}
