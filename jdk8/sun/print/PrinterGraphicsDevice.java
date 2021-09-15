package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Window;

public final class PrinterGraphicsDevice extends GraphicsDevice {
   String printerID;
   GraphicsConfiguration graphicsConf;

   protected PrinterGraphicsDevice(GraphicsConfiguration var1, String var2) {
      this.printerID = var2;
      this.graphicsConf = var1;
   }

   public int getType() {
      return 1;
   }

   public String getIDstring() {
      return this.printerID;
   }

   public GraphicsConfiguration[] getConfigurations() {
      GraphicsConfiguration[] var1 = new GraphicsConfiguration[]{this.graphicsConf};
      return var1;
   }

   public GraphicsConfiguration getDefaultConfiguration() {
      return this.graphicsConf;
   }

   public void setFullScreenWindow(Window var1) {
   }

   public Window getFullScreenWindow() {
      return null;
   }
}
