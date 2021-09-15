package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public final class ShapeGraphicAttribute extends GraphicAttribute {
   private Shape fShape;
   private boolean fStroke;
   public static final boolean STROKE = true;
   public static final boolean FILL = false;
   private Rectangle2D fShapeBounds;

   public ShapeGraphicAttribute(Shape var1, int var2, boolean var3) {
      super(var2);
      this.fShape = var1;
      this.fStroke = var3;
      this.fShapeBounds = this.fShape.getBounds2D();
   }

   public float getAscent() {
      return (float)Math.max(0.0D, -this.fShapeBounds.getMinY());
   }

   public float getDescent() {
      return (float)Math.max(0.0D, this.fShapeBounds.getMaxY());
   }

   public float getAdvance() {
      return (float)Math.max(0.0D, this.fShapeBounds.getMaxX());
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      var1.translate((int)var2, (int)var3);

      try {
         if (this.fStroke) {
            var1.draw(this.fShape);
         } else {
            var1.fill(this.fShape);
         }
      } finally {
         var1.translate(-((int)var2), -((int)var3));
      }

   }

   public Rectangle2D getBounds() {
      Rectangle2D.Float var1 = new Rectangle2D.Float();
      var1.setRect(this.fShapeBounds);
      if (this.fStroke) {
         ++var1.width;
         ++var1.height;
      }

      return var1;
   }

   public Shape getOutline(AffineTransform var1) {
      return var1 == null ? this.fShape : var1.createTransformedShape(this.fShape);
   }

   public int hashCode() {
      return this.fShape.hashCode();
   }

   public boolean equals(Object var1) {
      try {
         return this.equals((ShapeGraphicAttribute)var1);
      } catch (ClassCastException var3) {
         return false;
      }
   }

   public boolean equals(ShapeGraphicAttribute var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.fStroke != var1.fStroke) {
         return false;
      } else if (this.getAlignment() != var1.getAlignment()) {
         return false;
      } else {
         return this.fShape.equals(var1.fShape);
      }
   }
}
