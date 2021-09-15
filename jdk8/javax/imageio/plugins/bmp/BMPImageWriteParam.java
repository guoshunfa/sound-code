package javax.imageio.plugins.bmp;

import com.sun.imageio.plugins.bmp.BMPCompressionTypes;
import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class BMPImageWriteParam extends ImageWriteParam {
   private boolean topDown;

   public BMPImageWriteParam(Locale var1) {
      super(var1);
      this.topDown = false;
      this.compressionTypes = BMPCompressionTypes.getCompressionTypes();
      this.canWriteCompressed = true;
      this.compressionMode = 3;
      this.compressionType = this.compressionTypes[0];
   }

   public BMPImageWriteParam() {
      this((Locale)null);
   }

   public void setTopDown(boolean var1) {
      this.topDown = var1;
   }

   public boolean isTopDown() {
      return this.topDown;
   }
}
