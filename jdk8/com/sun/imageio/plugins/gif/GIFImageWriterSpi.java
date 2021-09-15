package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.util.Locale;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class GIFImageWriterSpi extends ImageWriterSpi {
   private static final String vendorName = "Oracle Corporation";
   private static final String version = "1.0";
   private static final String[] names = new String[]{"gif", "GIF"};
   private static final String[] suffixes = new String[]{"gif"};
   private static final String[] MIMETypes = new String[]{"image/gif"};
   private static final String writerClassName = "com.sun.imageio.plugins.gif.GIFImageWriter";
   private static final String[] readerSpiNames = new String[]{"com.sun.imageio.plugins.gif.GIFImageReaderSpi"};

   public GIFImageWriterSpi() {
      super("Oracle Corporation", "1.0", names, suffixes, MIMETypes, "com.sun.imageio.plugins.gif.GIFImageWriter", new Class[]{ImageOutputStream.class}, readerSpiNames, true, "javax_imageio_gif_stream_1.0", "com.sun.imageio.plugins.gif.GIFStreamMetadataFormat", (String[])null, (String[])null, true, "javax_imageio_gif_image_1.0", "com.sun.imageio.plugins.gif.GIFImageMetadataFormat", (String[])null, (String[])null);
   }

   public boolean canEncodeImage(ImageTypeSpecifier var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("type == null!");
      } else {
         SampleModel var2 = var1.getSampleModel();
         ColorModel var3 = var1.getColorModel();
         boolean var4 = var2.getNumBands() == 1 && var2.getSampleSize(0) <= 8 && var2.getWidth() <= 65535 && var2.getHeight() <= 65535 && (var3 == null || var3.getComponentSize()[0] <= 8);
         return var4 ? true : PaletteBuilder.canCreatePalette(var1);
      }
   }

   public String getDescription(Locale var1) {
      return "Standard GIF image writer";
   }

   public ImageWriter createWriterInstance(Object var1) {
      return new GIFImageWriter(this);
   }
}
