package sun.java2d.pipe;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import sun.java2d.SunGraphics2D;

public interface DrawImagePipe {
   boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, Color var5, ImageObserver var6);

   boolean copyImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, Color var9, ImageObserver var10);

   boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, Color var7, ImageObserver var8);

   boolean scaleImage(SunGraphics2D var1, Image var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, Color var11, ImageObserver var12);

   boolean transformImage(SunGraphics2D var1, Image var2, AffineTransform var3, ImageObserver var4);

   void transformImage(SunGraphics2D var1, BufferedImage var2, BufferedImageOp var3, int var4, int var5);
}
