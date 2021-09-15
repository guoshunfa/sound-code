package sun.java2d.x11;

import java.awt.Color;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import sun.awt.X11GraphicsConfig;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public abstract class X11SurfaceDataProxy extends SurfaceDataProxy implements Transparency {
   X11GraphicsConfig x11gc;

   public static SurfaceDataProxy createProxy(SurfaceData var0, X11GraphicsConfig var1) {
      if (var0 instanceof X11SurfaceData) {
         return UNCACHED;
      } else {
         ColorModel var2 = var0.getColorModel();
         int var3 = var2.getTransparency();
         if (var3 == 1) {
            return new X11SurfaceDataProxy.Opaque(var1);
         } else {
            if (var3 == 2) {
               if (var2 instanceof IndexColorModel && var2.getPixelSize() == 8) {
                  return new X11SurfaceDataProxy.Bitmask(var1);
               }

               if (var2 instanceof DirectColorModel) {
                  DirectColorModel var4 = (DirectColorModel)var2;
                  int var5 = var4.getRedMask() | var4.getGreenMask() | var4.getBlueMask();
                  int var6 = var4.getAlphaMask();
                  if ((var5 & -16777216) == 0 && (var6 & -16777216) != 0) {
                     return new X11SurfaceDataProxy.Bitmask(var1);
                  }
               }
            }

            return UNCACHED;
         }
      }
   }

   public X11SurfaceDataProxy(X11GraphicsConfig var1) {
      this.x11gc = var1;
   }

   public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      if (var2 == null) {
         try {
            var2 = X11SurfaceData.createData(this.x11gc, var3, var4, this.x11gc.getColorModel(), (Image)null, 0L, this.getTransparency());
         } catch (OutOfMemoryError var6) {
         }
      }

      return (SurfaceData)var2;
   }

   public static class Bitmask extends X11SurfaceDataProxy {
      public Bitmask(X11GraphicsConfig var1) {
         super(var1);
      }

      public int getTransparency() {
         return 2;
      }

      public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
         if (var2 >= 3) {
            return false;
         } else if (var4 != null && var4.getTransparency() != 1) {
            return false;
         } else {
            return CompositeType.SrcOverNoEa.equals(var3) || CompositeType.SrcNoEa.equals(var3) && var4 != null;
         }
      }
   }

   public static class Opaque extends X11SurfaceDataProxy {
      public Opaque(X11GraphicsConfig var1) {
         super(var1);
      }

      public int getTransparency() {
         return 1;
      }

      public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
         return var2 < 3 && (CompositeType.SrcOverNoEa.equals(var3) || CompositeType.SrcNoEa.equals(var3));
      }
   }
}
