package javax.imageio.spi;

import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageReaderSpi extends ImageReaderWriterSpi {
   /** @deprecated */
   @Deprecated
   public static final Class[] STANDARD_INPUT_TYPE = new Class[]{ImageInputStream.class};
   protected Class[] inputTypes = null;
   protected String[] writerSpiNames = null;
   private Class readerClass = null;

   protected ImageReaderSpi() {
   }

   public ImageReaderSpi(String var1, String var2, String[] var3, String[] var4, String[] var5, String var6, Class[] var7, String[] var8, boolean var9, String var10, String var11, String[] var12, String[] var13, boolean var14, String var15, String var16, String[] var17, String[] var18) {
      super(var1, var2, var3, var4, var5, var6, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18);
      if (var7 == null) {
         throw new IllegalArgumentException("inputTypes == null!");
      } else if (var7.length == 0) {
         throw new IllegalArgumentException("inputTypes.length == 0!");
      } else {
         this.inputTypes = var7 == STANDARD_INPUT_TYPE ? new Class[]{ImageInputStream.class} : (Class[])var7.clone();
         if (var8 != null && var8.length > 0) {
            this.writerSpiNames = (String[])((String[])var8.clone());
         }

      }
   }

   public Class[] getInputTypes() {
      return (Class[])((Class[])this.inputTypes.clone());
   }

   public abstract boolean canDecodeInput(Object var1) throws IOException;

   public ImageReader createReaderInstance() throws IOException {
      return this.createReaderInstance((Object)null);
   }

   public abstract ImageReader createReaderInstance(Object var1) throws IOException;

   public boolean isOwnReader(ImageReader var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("reader == null!");
      } else {
         String var2 = var1.getClass().getName();
         return var2.equals(this.pluginClassName);
      }
   }

   public String[] getImageWriterSpiNames() {
      return this.writerSpiNames == null ? null : (String[])((String[])this.writerSpiNames.clone());
   }
}
