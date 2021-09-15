package sun.lwawt.macosx;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

public class CPrinterDevice extends GraphicsDevice {
   GraphicsConfiguration gc;

   public CPrinterDevice(CPrinterGraphicsConfig var1) {
      this.gc = var1;
   }

   public int getType() {
      return 1;
   }

   public String getIDstring() {
      return "Printer";
   }

   public GraphicsConfiguration[] getConfigurations() {
      return new GraphicsConfiguration[]{this.gc};
   }

   public GraphicsConfiguration getDefaultConfiguration() {
      return this.gc;
   }
}
