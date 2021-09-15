package com.sun.imageio.plugins.bmp;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class BMPMetadataFormat extends IIOMetadataFormatImpl {
   private static IIOMetadataFormat instance = null;

   private BMPMetadataFormat() {
      super("javax_imageio_bmp_1.0", 2);
      this.addElement("ImageDescriptor", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("ImageDescriptor", "bmpVersion", 0, true, (String)null);
      this.addAttribute("ImageDescriptor", "width", 2, true, (String)null, "0", "65535", true, true);
      this.addAttribute("ImageDescriptor", "height", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("ImageDescriptor", "bitsPerPixel", 2, true, (String)null, "1", "65535", true, true);
      this.addAttribute("ImageDescriptor", "compression", 2, false, (String)null);
      this.addAttribute("ImageDescriptor", "imageSize", 2, true, (String)null, "1", "65535", true, true);
      this.addElement("PixelsPerMeter", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("PixelsPerMeter", "X", 2, false, (String)null, "1", "65535", true, true);
      this.addAttribute("PixelsPerMeter", "Y", 2, false, (String)null, "1", "65535", true, true);
      this.addElement("ColorsUsed", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("ColorsUsed", "value", 2, true, (String)null, "0", "65535", true, true);
      this.addElement("ColorsImportant", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("ColorsImportant", "value", 2, false, (String)null, "0", "65535", true, true);
      this.addElement("BI_BITFIELDS_Mask", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("BI_BITFIELDS_Mask", "red", 2, false, (String)null, "0", "65535", true, true);
      this.addAttribute("BI_BITFIELDS_Mask", "green", 2, false, (String)null, "0", "65535", true, true);
      this.addAttribute("BI_BITFIELDS_Mask", "blue", 2, false, (String)null, "0", "65535", true, true);
      this.addElement("ColorSpace", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("ColorSpace", "value", 2, false, (String)null, "0", "65535", true, true);
      this.addElement("LCS_CALIBRATED_RGB", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("LCS_CALIBRATED_RGB", "redX", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "redY", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "redZ", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "greenX", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "greenY", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "greenZ", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "blueX", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "blueY", 4, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB", "blueZ", 4, false, (String)null, "0", "65535", true, true);
      this.addElement("LCS_CALIBRATED_RGB_GAMMA", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("LCS_CALIBRATED_RGB_GAMMA", "red", 2, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB_GAMMA", "green", 2, false, (String)null, "0", "65535", true, true);
      this.addAttribute("LCS_CALIBRATED_RGB_GAMMA", "blue", 2, false, (String)null, "0", "65535", true, true);
      this.addElement("Intent", "javax_imageio_bmp_1.0", 0);
      this.addAttribute("Intent", "value", 2, false, (String)null, "0", "65535", true, true);
      this.addElement("Palette", "javax_imageio_bmp_1.0", 2, 256);
      this.addAttribute("Palette", "sizeOfPalette", 2, true, (String)null);
      this.addBooleanAttribute("Palette", "sortFlag", false, false);
      this.addElement("PaletteEntry", "Palette", 0);
      this.addAttribute("PaletteEntry", "index", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("PaletteEntry", "red", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("PaletteEntry", "green", 2, true, (String)null, "0", "255", true, true);
      this.addAttribute("PaletteEntry", "blue", 2, true, (String)null, "0", "255", true, true);
      this.addElement("CommentExtensions", "javax_imageio_bmp_1.0", 1, Integer.MAX_VALUE);
      this.addElement("CommentExtension", "CommentExtensions", 0);
      this.addAttribute("CommentExtension", "value", 0, true, (String)null);
   }

   public boolean canNodeAppear(String var1, ImageTypeSpecifier var2) {
      return true;
   }

   public static synchronized IIOMetadataFormat getInstance() {
      if (instance == null) {
         instance = new BMPMetadataFormat();
      }

      return instance;
   }
}
