package java.awt.font;

import java.awt.geom.Rectangle2D;

public final class GlyphMetrics {
   private boolean horizontal;
   private float advanceX;
   private float advanceY;
   private Rectangle2D.Float bounds;
   private byte glyphType;
   public static final byte STANDARD = 0;
   public static final byte LIGATURE = 1;
   public static final byte COMBINING = 2;
   public static final byte COMPONENT = 3;
   public static final byte WHITESPACE = 4;

   public GlyphMetrics(float var1, Rectangle2D var2, byte var3) {
      this.horizontal = true;
      this.advanceX = var1;
      this.advanceY = 0.0F;
      this.bounds = new Rectangle2D.Float();
      this.bounds.setRect(var2);
      this.glyphType = var3;
   }

   public GlyphMetrics(boolean var1, float var2, float var3, Rectangle2D var4, byte var5) {
      this.horizontal = var1;
      this.advanceX = var2;
      this.advanceY = var3;
      this.bounds = new Rectangle2D.Float();
      this.bounds.setRect(var4);
      this.glyphType = var5;
   }

   public float getAdvance() {
      return this.horizontal ? this.advanceX : this.advanceY;
   }

   public float getAdvanceX() {
      return this.advanceX;
   }

   public float getAdvanceY() {
      return this.advanceY;
   }

   public Rectangle2D getBounds2D() {
      return new Rectangle2D.Float(this.bounds.x, this.bounds.y, this.bounds.width, this.bounds.height);
   }

   public float getLSB() {
      return this.horizontal ? this.bounds.x : this.bounds.y;
   }

   public float getRSB() {
      return this.horizontal ? this.advanceX - this.bounds.x - this.bounds.width : this.advanceY - this.bounds.y - this.bounds.height;
   }

   public int getType() {
      return this.glyphType;
   }

   public boolean isStandard() {
      return (this.glyphType & 3) == 0;
   }

   public boolean isLigature() {
      return (this.glyphType & 3) == 1;
   }

   public boolean isCombining() {
      return (this.glyphType & 3) == 2;
   }

   public boolean isComponent() {
      return (this.glyphType & 3) == 3;
   }

   public boolean isWhitespace() {
      return (this.glyphType & 4) == 4;
   }
}
