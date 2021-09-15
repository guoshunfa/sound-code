package sun.java2d.loops;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;
import sun.java2d.SurfaceData;

public final class XORComposite implements Composite {
   Color xorColor;
   int xorPixel;
   int alphaMask;

   public XORComposite(Color var1, SurfaceData var2) {
      this.xorColor = var1;
      SurfaceType var3 = var2.getSurfaceType();
      this.xorPixel = var2.pixelFor(var1.getRGB());
      this.alphaMask = var3.getAlphaMask();
   }

   public Color getXorColor() {
      return this.xorColor;
   }

   public int getXorPixel() {
      return this.xorPixel;
   }

   public int getAlphaMask() {
      return this.alphaMask;
   }

   public CompositeContext createContext(ColorModel var1, ColorModel var2, RenderingHints var3) {
      return new SunCompositeContext(this, var1, var2);
   }
}
