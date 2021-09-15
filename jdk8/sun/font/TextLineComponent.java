package sun.font;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphJustificationInfo;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public interface TextLineComponent {
   int LEFT_TO_RIGHT = 0;
   int RIGHT_TO_LEFT = 1;
   int UNCHANGED = 2;

   CoreMetrics getCoreMetrics();

   void draw(Graphics2D var1, float var2, float var3);

   Rectangle2D getCharVisualBounds(int var1);

   Rectangle2D getVisualBounds();

   float getAdvance();

   Shape getOutline(float var1, float var2);

   int getNumCharacters();

   float getCharX(int var1);

   float getCharY(int var1);

   float getCharAdvance(int var1);

   boolean caretAtOffsetIsValid(int var1);

   int getLineBreakIndex(int var1, float var2);

   float getAdvanceBetween(int var1, int var2);

   Rectangle2D getLogicalBounds();

   Rectangle2D getItalicBounds();

   AffineTransform getBaselineTransform();

   boolean isSimple();

   Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3);

   TextLineComponent getSubset(int var1, int var2, int var3);

   int getNumJustificationInfos();

   void getJustificationInfos(GlyphJustificationInfo[] var1, int var2, int var3, int var4);

   TextLineComponent applyJustificationDeltas(float[] var1, int var2, boolean[] var3);
}
