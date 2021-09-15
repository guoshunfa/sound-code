package javax.imageio.spi;

import java.io.File;
import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageInputStreamSpi extends IIOServiceProvider {
   protected Class<?> inputClass;

   protected ImageInputStreamSpi() {
   }

   public ImageInputStreamSpi(String var1, String var2, Class<?> var3) {
      super(var1, var2);
      this.inputClass = var3;
   }

   public Class<?> getInputClass() {
      return this.inputClass;
   }

   public boolean canUseCacheFile() {
      return false;
   }

   public boolean needsCacheFile() {
      return false;
   }

   public abstract ImageInputStream createInputStreamInstance(Object var1, boolean var2, File var3) throws IOException;

   public ImageInputStream createInputStreamInstance(Object var1) throws IOException {
      return this.createInputStreamInstance(var1, true, (File)null);
   }
}
