package sun.java2d.pipe.hw;

import java.awt.image.VolatileImage;

public interface AccelGraphicsConfig extends BufferedContextProvider {
   VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3, int var4);

   ContextCapabilities getContextCapabilities();

   void addDeviceEventListener(AccelDeviceEventListener var1);

   void removeDeviceEventListener(AccelDeviceEventListener var1);
}
