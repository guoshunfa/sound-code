package sun.lwawt;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Image;

public interface LWGraphicsConfig {
   int getMaxTextureWidth();

   int getMaxTextureHeight();

   void assertOperationSupported(int var1, BufferCapabilities var2) throws AWTException;

   Image createBackBuffer(LWComponentPeer<?, ?> var1);

   void destroyBackBuffer(Image var1);

   void flip(LWComponentPeer<?, ?> var1, Image var2, int var3, int var4, int var5, int var6, BufferCapabilities.FlipContents var7);

   Image createAcceleratedImage(Component var1, int var2, int var3);
}
