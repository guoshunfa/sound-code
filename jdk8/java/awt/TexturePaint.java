package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class TexturePaint implements Paint {
   BufferedImage bufImg;
   double tx;
   double ty;
   double sx;
   double sy;

   public TexturePaint(BufferedImage var1, Rectangle2D var2) {
      this.bufImg = var1;
      this.tx = var2.getX();
      this.ty = var2.getY();
      this.sx = var2.getWidth() / (double)this.bufImg.getWidth();
      this.sy = var2.getHeight() / (double)this.bufImg.getHeight();
   }

   public BufferedImage getImage() {
      return this.bufImg;
   }

   public Rectangle2D getAnchorRect() {
      return new Rectangle2D.Double(this.tx, this.ty, this.sx * (double)this.bufImg.getWidth(), this.sy * (double)this.bufImg.getHeight());
   }

   public PaintContext createContext(ColorModel var1, Rectangle var2, Rectangle2D var3, AffineTransform var4, RenderingHints var5) {
      if (var4 == null) {
         var4 = new AffineTransform();
      } else {
         var4 = (AffineTransform)var4.clone();
      }

      var4.translate(this.tx, this.ty);
      var4.scale(this.sx, this.sy);
      return TexturePaintContext.getContext(this.bufImg, var4, var5, var2);
   }

   public int getTransparency() {
      return this.bufImg.getColorModel().getTransparency();
   }
}
