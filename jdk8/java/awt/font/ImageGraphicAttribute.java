package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public final class ImageGraphicAttribute extends GraphicAttribute {
   private Image fImage;
   private float fImageWidth;
   private float fImageHeight;
   private float fOriginX;
   private float fOriginY;

   public ImageGraphicAttribute(Image var1, int var2) {
      this(var1, var2, 0.0F, 0.0F);
   }

   public ImageGraphicAttribute(Image var1, int var2, float var3, float var4) {
      super(var2);
      this.fImage = var1;
      this.fImageWidth = (float)var1.getWidth((ImageObserver)null);
      this.fImageHeight = (float)var1.getHeight((ImageObserver)null);
      this.fOriginX = var3;
      this.fOriginY = var4;
   }

   public float getAscent() {
      return Math.max(0.0F, this.fOriginY);
   }

   public float getDescent() {
      return Math.max(0.0F, this.fImageHeight - this.fOriginY);
   }

   public float getAdvance() {
      return Math.max(0.0F, this.fImageWidth - this.fOriginX);
   }

   public Rectangle2D getBounds() {
      return new Rectangle2D.Float(-this.fOriginX, -this.fOriginY, this.fImageWidth, this.fImageHeight);
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      var1.drawImage(this.fImage, (int)(var2 - this.fOriginX), (int)(var3 - this.fOriginY), (ImageObserver)null);
   }

   public int hashCode() {
      return this.fImage.hashCode();
   }

   public boolean equals(Object var1) {
      try {
         return this.equals((ImageGraphicAttribute)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean equals(ImageGraphicAttribute var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.fOriginX == var1.fOriginX && this.fOriginY == var1.fOriginY) {
         if (this.getAlignment() != var1.getAlignment()) {
            return false;
         } else {
            return this.fImage.equals(var1.fImage);
         }
      } else {
         return false;
      }
   }
}
