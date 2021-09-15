package javax.imageio.spi;

import java.awt.image.RenderedImage;
import java.io.IOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageWriterSpi extends ImageReaderWriterSpi {
   /** @deprecated */
   @Deprecated
   public static final Class[] STANDARD_OUTPUT_TYPE = new Class[]{ImageOutputStream.class};
   protected Class[] outputTypes = null;
   protected String[] readerSpiNames = null;
   private Class writerClass = null;

   protected ImageWriterSpi() {
   }

   public ImageWriterSpi(String var1, String var2, String[] var3, String[] var4, String[] var5, String var6, Class[] var7, String[] var8, boolean var9, String var10, String var11, String[] var12, String[] var13, boolean var14, String var15, String var16, String[] var17, String[] var18) {
      super(var1, var2, var3, var4, var5, var6, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18);
      if (var7 == null) {
         throw new IllegalArgumentException("outputTypes == null!");
      } else if (var7.length == 0) {
         throw new IllegalArgumentException("outputTypes.length == 0!");
      } else {
         this.outputTypes = var7 == STANDARD_OUTPUT_TYPE ? new Class[]{ImageOutputStream.class} : (Class[])var7.clone();
         if (var8 != null && var8.length > 0) {
            this.readerSpiNames = (String[])((String[])var8.clone());
         }

      }
   }

   public boolean isFormatLossless() {
      return true;
   }

   public Class[] getOutputTypes() {
      return (Class[])((Class[])this.outputTypes.clone());
   }

   public abstract boolean canEncodeImage(ImageTypeSpecifier var1);

   public boolean canEncodeImage(RenderedImage var1) {
      return this.canEncodeImage(ImageTypeSpecifier.createFromRenderedImage(var1));
   }

   public ImageWriter createWriterInstance() throws IOException {
      return this.createWriterInstance((Object)null);
   }

   public abstract ImageWriter createWriterInstance(Object var1) throws IOException;

   public boolean isOwnWriter(ImageWriter var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("writer == null!");
      } else {
         String var2 = var1.getClass().getName();
         return var2.equals(this.pluginClassName);
      }
   }

   public String[] getImageReaderSpiNames() {
      return this.readerSpiNames == null ? null : (String[])((String[])this.readerSpiNames.clone());
   }
}
