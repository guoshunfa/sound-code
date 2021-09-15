package sun.java2d;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.XORComposite;
import sun.java2d.pipe.Region;

public class SunCompositeContext implements CompositeContext {
   ColorModel srcCM;
   ColorModel dstCM;
   Composite composite;
   CompositeType comptype;

   public SunCompositeContext(AlphaComposite var1, ColorModel var2, ColorModel var3) {
      if (var2 == null) {
         throw new NullPointerException("Source color model cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("Destination color model cannot be null");
      } else {
         this.srcCM = var2;
         this.dstCM = var3;
         this.composite = var1;
         this.comptype = CompositeType.forAlphaComposite(var1);
      }
   }

   public SunCompositeContext(XORComposite var1, ColorModel var2, ColorModel var3) {
      if (var2 == null) {
         throw new NullPointerException("Source color model cannot be null");
      } else if (var3 == null) {
         throw new NullPointerException("Destination color model cannot be null");
      } else {
         this.srcCM = var2;
         this.dstCM = var3;
         this.composite = var1;
         this.comptype = CompositeType.Xor;
      }
   }

   public void dispose() {
   }

   public void compose(Raster var1, Raster var2, WritableRaster var3) {
      if (var2 != var3) {
         var3.setDataElements(0, 0, (Raster)var2);
      }

      WritableRaster var4;
      if (var1 instanceof WritableRaster) {
         var4 = (WritableRaster)var1;
      } else {
         var4 = var1.createCompatibleWritableRaster();
         var4.setDataElements(0, 0, (Raster)var1);
      }

      int var5 = Math.min(var4.getWidth(), var2.getWidth());
      int var6 = Math.min(var4.getHeight(), var2.getHeight());
      BufferedImage var7 = new BufferedImage(this.srcCM, var4, this.srcCM.isAlphaPremultiplied(), (Hashtable)null);
      BufferedImage var8 = new BufferedImage(this.dstCM, var3, this.dstCM.isAlphaPremultiplied(), (Hashtable)null);
      SurfaceData var9 = BufImgSurfaceData.createData(var7);
      SurfaceData var10 = BufImgSurfaceData.createData(var8);
      Blit var11 = Blit.getFromCache(var9.getSurfaceType(), this.comptype, var10.getSurfaceType());
      var11.Blit(var9, var10, this.composite, (Region)null, 0, 0, 0, 0, var5, var6);
   }
}
