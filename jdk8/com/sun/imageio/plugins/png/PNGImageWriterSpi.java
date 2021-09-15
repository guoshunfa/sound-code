package com.sun.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class PNGImageWriterSpi extends ImageWriterSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final String[] names = new String[]{"png", "PNG"};
   private static final String[] suffixes = new String[]{"png"};
   private static final String[] MIMETypes = new String[]{"image/png", "image/x-png"};
   private static final String writerClassName = "com.sun.imageio.plugins.png.PNGImageWriter";
   private static final String[] readerSpiNames = new String[]{"com.sun.imageio.plugins.png.PNGImageReaderSpi"};

   public PNGImageWriterSpi() {
      super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.png.PNGImageWriter", new Class[]{ImageOutputStream.class}, readerSpiNames, false, (String)null, (String)null, (String[])null, (String[])null, true, "javax_imageio_png_1.0", "com.sun.imageio.plugins.png.PNGMetadataFormat", (String[])null, (String[])null);
   }

   public boolean canEncodeImage(ImageTypeSpecifier var1) {
      SampleModel var2 = var1.getSampleModel();
      ColorModel var3 = var1.getColorModel();
      int[] var4 = var2.getSampleSize();
      int var5 = var4[0];

      int var6;
      for(var6 = 1; var6 < var4.length; ++var6) {
         if (var4[var6] > var5) {
            var5 = var4[var6];
         }
      }

      if (var5 >= 1 && var5 <= 16) {
         var6 = var2.getNumBands();
         if (var6 >= 1 && var6 <= 4) {
            boolean var7 = var3.hasAlpha();
            if (var3 instanceof IndexColorModel) {
               return true;
            } else if ((var6 == 1 || var6 == 3) && var7) {
               return false;
            } else {
               return var6 != 2 && var6 != 4 || var7;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public String getDescription(Locale var1) {
      return "Standard PNG image writer";
   }

   public ImageWriter createWriterInstance(Object var1) {
      return new PNGImageWriter(this);
   }
}
