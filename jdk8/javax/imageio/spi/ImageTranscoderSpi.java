package javax.imageio.spi;

import javax.imageio.ImageTranscoder;

public abstract class ImageTranscoderSpi extends IIOServiceProvider {
   protected ImageTranscoderSpi() {
   }

   public ImageTranscoderSpi(String var1, String var2) {
      super(var1, var2);
   }

   public abstract String getReaderServiceProviderName();

   public abstract String getWriterServiceProviderName();

   public abstract ImageTranscoder createTranscoderInstance();
}
