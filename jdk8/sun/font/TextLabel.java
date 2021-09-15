package sun.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public abstract class TextLabel {
   public abstract Rectangle2D getVisualBounds(float var1, float var2);

   public abstract Rectangle2D getLogicalBounds(float var1, float var2);

   public abstract Rectangle2D getAlignBounds(float var1, float var2);

   public abstract Rectangle2D getItalicBounds(float var1, float var2);

   public abstract Shape getOutline(float var1, float var2);

   public abstract void draw(Graphics2D var1, float var2, float var3);

   public Rectangle2D getVisualBounds() {
      return this.getVisualBounds(0.0F, 0.0F);
   }

   public Rectangle2D getLogicalBounds() {
      return this.getLogicalBounds(0.0F, 0.0F);
   }

   public Rectangle2D getAlignBounds() {
      return this.getAlignBounds(0.0F, 0.0F);
   }

   public Rectangle2D getItalicBounds() {
      return this.getItalicBounds(0.0F, 0.0F);
   }

   public Shape getOutline() {
      return this.getOutline(0.0F, 0.0F);
   }

   public void draw(Graphics2D var1) {
      this.draw(var1, 0.0F, 0.0F);
   }
}
