package sun.java2d.xr;

import sun.awt.X11ComponentPeer;
import sun.awt.X11GraphicsConfig;
import sun.awt.X11GraphicsDevice;
import sun.awt.X11GraphicsEnvironment;
import sun.awt.image.SurfaceManager;
import sun.java2d.SurfaceData;

public class XRGraphicsConfig extends X11GraphicsConfig implements SurfaceManager.ProxiedGraphicsConfig {
   private XRGraphicsConfig(X11GraphicsDevice var1, int var2, int var3, int var4, boolean var5) {
      super(var1, var2, var3, var4, var5);
   }

   public SurfaceData createSurfaceData(X11ComponentPeer var1) {
      return XRSurfaceData.createData(var1);
   }

   public static XRGraphicsConfig getConfig(X11GraphicsDevice var0, int var1, int var2, int var3, boolean var4) {
      return !X11GraphicsEnvironment.isXRenderAvailable() ? null : new XRGraphicsConfig(var0, var1, var2, var3, var4);
   }

   public Object getProxyKey() {
      return this;
   }
}
