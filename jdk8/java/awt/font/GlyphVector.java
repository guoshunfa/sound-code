package java.awt.font;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class GlyphVector implements Cloneable {
   public static final int FLAG_HAS_TRANSFORMS = 1;
   public static final int FLAG_HAS_POSITION_ADJUSTMENTS = 2;
   public static final int FLAG_RUN_RTL = 4;
   public static final int FLAG_COMPLEX_GLYPHS = 8;
   public static final int FLAG_MASK = 15;

   public abstract Font getFont();

   public abstract FontRenderContext getFontRenderContext();

   public abstract void performDefaultLayout();

   public abstract int getNumGlyphs();

   public abstract int getGlyphCode(int var1);

   public abstract int[] getGlyphCodes(int var1, int var2, int[] var3);

   public int getGlyphCharIndex(int var1) {
      return var1;
   }

   public int[] getGlyphCharIndices(int var1, int var2, int[] var3) {
      if (var3 == null) {
         var3 = new int[var2];
      }

      int var4 = 0;

      for(int var5 = var1; var4 < var2; ++var5) {
         var3[var4] = this.getGlyphCharIndex(var5);
         ++var4;
      }

      return var3;
   }

   public abstract Rectangle2D getLogicalBounds();

   public abstract Rectangle2D getVisualBounds();

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      Rectangle2D var4 = this.getVisualBounds();
      int var5 = (int)Math.floor(var4.getX() + (double)var2);
      int var6 = (int)Math.floor(var4.getY() + (double)var3);
      int var7 = (int)Math.ceil(var4.getMaxX() + (double)var2);
      int var8 = (int)Math.ceil(var4.getMaxY() + (double)var3);
      return new Rectangle(var5, var6, var7 - var5, var8 - var6);
   }

   public abstract Shape getOutline();

   public abstract Shape getOutline(float var1, float var2);

   public abstract Shape getGlyphOutline(int var1);

   public Shape getGlyphOutline(int var1, float var2, float var3) {
      Shape var4 = this.getGlyphOutline(var1);
      AffineTransform var5 = AffineTransform.getTranslateInstance((double)var2, (double)var3);
      return var5.createTransformedShape(var4);
   }

   public abstract Point2D getGlyphPosition(int var1);

   public abstract void setGlyphPosition(int var1, Point2D var2);

   public abstract AffineTransform getGlyphTransform(int var1);

   public abstract void setGlyphTransform(int var1, AffineTransform var2);

   public int getLayoutFlags() {
      return 0;
   }

   public abstract float[] getGlyphPositions(int var1, int var2, float[] var3);

   public abstract Shape getGlyphLogicalBounds(int var1);

   public abstract Shape getGlyphVisualBounds(int var1);

   public Rectangle getGlyphPixelBounds(int var1, FontRenderContext var2, float var3, float var4) {
      Rectangle2D var5 = this.getGlyphVisualBounds(var1).getBounds2D();
      int var6 = (int)Math.floor(var5.getX() + (double)var3);
      int var7 = (int)Math.floor(var5.getY() + (double)var4);
      int var8 = (int)Math.ceil(var5.getMaxX() + (double)var3);
      int var9 = (int)Math.ceil(var5.getMaxY() + (double)var4);
      return new Rectangle(var6, var7, var8 - var6, var9 - var7);
   }

   public abstract GlyphMetrics getGlyphMetrics(int var1);

   public abstract GlyphJustificationInfo getGlyphJustificationInfo(int var1);

   public abstract boolean equals(GlyphVector var1);
}
