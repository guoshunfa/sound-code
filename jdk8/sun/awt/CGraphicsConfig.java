package sun.awt;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import sun.java2d.SurfaceData;
import sun.java2d.opengl.CGLLayer;
import sun.lwawt.LWGraphicsConfig;
import sun.lwawt.macosx.CPlatformView;

public abstract class CGraphicsConfig extends GraphicsConfiguration implements LWGraphicsConfig {
   private final CGraphicsDevice device;
   private ColorModel colorModel;

   protected CGraphicsConfig(CGraphicsDevice var1) {
      this.device = var1;
   }

   public BufferedImage createCompatibleImage(int var1, int var2) {
      throw new UnsupportedOperationException("not implemented");
   }

   private static native Rectangle2D nativeGetBounds(int var0);

   public Rectangle getBounds() {
      Rectangle2D var1 = nativeGetBounds(this.device.getCGDisplayID());
      return var1.getBounds();
   }

   public ColorModel getColorModel() {
      if (this.colorModel == null) {
         this.colorModel = this.getColorModel(1);
      }

      return this.colorModel;
   }

   public ColorModel getColorModel(int var1) {
      throw new UnsupportedOperationException("not implemented");
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   public CGraphicsDevice getDevice() {
      return this.device;
   }

   public AffineTransform getNormalizingTransform() {
      double var1 = this.device.getXResolution() / 72.0D;
      double var3 = this.device.getYResolution() / 72.0D;
      return new AffineTransform(var1, 0.0D, 0.0D, var3, 0.0D, 0.0D);
   }

   public abstract SurfaceData createSurfaceData(CPlatformView var1);

   public abstract SurfaceData createSurfaceData(CGLLayer var1);

   public final boolean isTranslucencyCapable() {
      return true;
   }
}
