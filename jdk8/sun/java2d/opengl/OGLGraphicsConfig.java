package sun.java2d.opengl;

import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.hw.AccelGraphicsConfig;

interface OGLGraphicsConfig extends AccelGraphicsConfig, SurfaceManager.ProxiedGraphicsConfig {
   OGLContext getContext();

   long getNativeConfigInfo();

   boolean isCapPresent(int var1);

   SurfaceData createManagedSurface(int var1, int var2, int var3);
}
