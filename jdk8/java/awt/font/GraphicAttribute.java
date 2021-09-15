package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public abstract class GraphicAttribute {
   private int fAlignment;
   public static final int TOP_ALIGNMENT = -1;
   public static final int BOTTOM_ALIGNMENT = -2;
   public static final int ROMAN_BASELINE = 0;
   public static final int CENTER_BASELINE = 1;
   public static final int HANGING_BASELINE = 2;

   protected GraphicAttribute(int var1) {
      if (var1 >= -2 && var1 <= 2) {
         this.fAlignment = var1;
      } else {
         throw new IllegalArgumentException("bad alignment");
      }
   }

   public abstract float getAscent();

   public abstract float getDescent();

   public abstract float getAdvance();

   public Rectangle2D getBounds() {
      float var1 = this.getAscent();
      return new Rectangle2D.Float(0.0F, -var1, this.getAdvance(), var1 + this.getDescent());
   }

   public Shape getOutline(AffineTransform var1) {
      Object var2 = this.getBounds();
      if (var1 != null) {
         var2 = var1.createTransformedShape((Shape)var2);
      }

      return (Shape)var2;
   }

   public abstract void draw(Graphics2D var1, float var2, float var3);

   public final int getAlignment() {
      return this.fAlignment;
   }

   public GlyphJustificationInfo getJustificationInfo() {
      float var1 = this.getAdvance();
      return new GlyphJustificationInfo(var1, false, 2, var1 / 3.0F, var1 / 3.0F, false, 1, 0.0F, 0.0F);
   }
}
