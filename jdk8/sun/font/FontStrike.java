package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class FontStrike {
   protected FontStrikeDisposer disposer;
   protected FontStrikeDesc desc;
   protected StrikeMetrics strikeMetrics;
   protected boolean algoStyle = false;
   protected float boldness = 1.0F;
   protected float italic = 0.0F;

   public abstract int getNumGlyphs();

   abstract StrikeMetrics getFontMetrics();

   abstract void getGlyphImagePtrs(int[] var1, long[] var2, int var3);

   abstract long getGlyphImagePtr(int var1);

   abstract void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3);

   abstract Point2D.Float getGlyphMetrics(int var1);

   abstract Point2D.Float getCharMetrics(char var1);

   abstract float getGlyphAdvance(int var1);

   abstract float getCodePointAdvance(int var1);

   abstract Rectangle2D.Float getGlyphOutlineBounds(int var1);

   abstract GeneralPath getGlyphOutline(int var1, float var2, float var3);

   abstract GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3);
}
