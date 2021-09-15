package sun.java2d.xr;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import sun.font.GlyphList;
import sun.font.XRGlyphCacheEntry;
import sun.java2d.jules.TrapezoidList;
import sun.java2d.pipe.Region;

public interface XRBackend {
   void freePicture(int var1);

   void freePixmap(int var1);

   int createPixmap(int var1, int var2, int var3, int var4);

   int createPicture(int var1, int var2);

   long createGC(int var1);

   void freeGC(long var1);

   void copyArea(int var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   void putMaskImage(int var1, long var2, byte[] var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, float var13);

   void setGCClipRectangles(long var1, Region var3);

   void GCRectangles(int var1, long var2, GrowableRectArray var4);

   void setClipRectangles(int var1, Region var2);

   void setGCExposures(long var1, boolean var3);

   void setGCForeground(long var1, int var3);

   void setPictureTransform(int var1, AffineTransform var2);

   void setPictureRepeat(int var1, int var2);

   void setFilter(int var1, int var2);

   void renderRectangle(int var1, byte var2, XRColor var3, int var4, int var5, int var6, int var7);

   void renderRectangles(int var1, byte var2, XRColor var3, GrowableRectArray var4);

   void renderComposite(byte var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   int XRenderCreateGlyphSet(int var1);

   void XRenderAddGlyphs(int var1, GlyphList var2, List<XRGlyphCacheEntry> var3, byte[] var4);

   void XRenderFreeGlyphs(int var1, int[] var2);

   void XRenderCompositeText(byte var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, GrowableEltArray var10);

   int createRadialGradient(float var1, float var2, float var3, float var4, float[] var5, int[] var6, int var7);

   int createLinearGradient(Point2D var1, Point2D var2, float[] var3, int[] var4, int var5);

   void setGCMode(long var1, boolean var3);

   void renderCompositeTrapezoids(byte var1, int var2, int var3, int var4, int var5, int var6, TrapezoidList var7);
}
