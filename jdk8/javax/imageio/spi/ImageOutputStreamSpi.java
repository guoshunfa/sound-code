package javax.imageio.spi;

import java.io.File;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;

public abstract class ImageOutputStreamSpi extends IIOServiceProvider {
   protected Class<?> outputClass;

   protected ImageOutputStreamSpi() {
   }

   public ImageOutputStreamSpi(String var1, String var2, Class<?> var3) {
      super(var1, var2);
      this.outputClass = var3;
   }

   public Class<?> getOutputClass() {
      return this.outputClass;
   }

   public boolean canUseCacheFile() {
      return false;
   }

   public boolean needsCacheFile() {
      return false;
   }

   public abstract ImageOutputStream createOutputStreamInstance(Object var1, boolean var2, File var3) throws IOException;

   public ImageOutputStream createOutputStreamInstance(Object var1) throws IOException {
      return this.createOutputStreamInstance(var1, true, (File)null);
   }
}
