package sun.font;

import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.Rectangle2D;

public abstract class ExtendedTextLabel extends TextLabel implements TextLineComponent {
   public abstract int getNumCharacters();

   public abstract CoreMetrics getCoreMetrics();

   public abstract float getCharX(int var1);

   public abstract float getCharY(int var1);

   public abstract float getCharAdvance(int var1);

   public abstract Rectangle2D getCharVisualBounds(int var1, float var2, float var3);

   public abstract int logicalToVisual(int var1);

   public abstract int visualToLogical(int var1);

   public abstract int getLineBreakIndex(int var1, float var2);

   public abstract float getAdvanceBetween(int var1, int var2);

   public abstract boolean caretAtOffsetIsValid(int var1);

   public Rectangle2D getCharVisualBounds(int var1) {
      return this.getCharVisualBounds(var1, 0.0F, 0.0F);
   }

   public abstract TextLineComponent getSubset(int var1, int var2, int var3);

   public abstract int getNumJustificationInfos();

   public abstract void getJustificationInfos(GlyphJustificationInfo[] var1, int var2, int var3, int var4);

   public abstract TextLineComponent applyJustificationDeltas(float[] var1, int var2, boolean[] var3);
}
