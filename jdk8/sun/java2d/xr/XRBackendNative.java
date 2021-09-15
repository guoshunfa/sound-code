package sun.java2d.xr;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import sun.font.GlyphList;
import sun.font.XRGlyphCacheEntry;
import sun.java2d.jules.TrapezoidList;
import sun.java2d.pipe.Region;

public class XRBackendNative implements XRBackend {
   private static long FMTPTR_A8;
   private static long FMTPTR_ARGB32;
   private static long MASK_XIMG;

   private static native void initIDs();

   public native long createGC(int var1);

   public native void freeGC(long var1);

   public native int createPixmap(int var1, int var2, int var3, int var4);

   private native int createPictureNative(int var1, long var2);

   public native void freePicture(int var1);

   public native void freePixmap(int var1);

   public native void setGCExposures(long var1, boolean var3);

   public native void setGCForeground(long var1, int var3);

   public native void setPictureRepeat(int var1, int var2);

   public native void copyArea(int var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10);

   public native void setGCMode(long var1, boolean var3);

   private static native void GCRectanglesNative(int var0, long var1, int[] var3, int var4);

   public native void renderComposite(byte var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   private native void renderRectangle(int var1, byte var2, short var3, short var4, short var5, short var6, int var7, int var8, int var9, int var10);

   private static native void XRenderRectanglesNative(int var0, byte var1, short var2, short var3, short var4, short var5, int[] var6, int var7);

   private native void XRSetTransformNative(int var1, int var2, int var3, int var4, int var5, int var6, int var7);

   private static native int XRCreateLinearGradientPaintNative(float[] var0, short[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   private static native int XRCreateRadialGradientPaintNative(float[] var0, short[] var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public native void setFilter(int var1, int var2);

   private static native void XRSetClipNative(long var0, int var2, int var3, int var4, int var5, Region var6, boolean var7);

   public void GCRectangles(int var1, long var2, GrowableRectArray var4) {
      GCRectanglesNative(var1, var2, var4.getArray(), var4.getSize());
   }

   public int createPicture(int var1, int var2) {
      return this.createPictureNative(var1, getFormatPtr(var2));
   }

   public void setPictureTransform(int var1, AffineTransform var2) {
      this.XRSetTransformNative(var1, XRUtils.XDoubleToFixed(var2.getScaleX()), XRUtils.XDoubleToFixed(var2.getShearX()), XRUtils.XDoubleToFixed(var2.getTranslateX()), XRUtils.XDoubleToFixed(var2.getShearY()), XRUtils.XDoubleToFixed(var2.getScaleY()), XRUtils.XDoubleToFixed(var2.getTranslateY()));
   }

   public void renderRectangle(int var1, byte var2, XRColor var3, int var4, int var5, int var6, int var7) {
      this.renderRectangle(var1, var2, (short)var3.red, (short)var3.green, (short)var3.blue, (short)var3.alpha, var4, var5, var6, var7);
   }

   private short[] getRenderColors(int[] var1) {
      short[] var2 = new short[var1.length * 4];
      XRColor var3 = new XRColor();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         var3.setColorValues(var1[var4], true);
         var2[var4 * 4 + 0] = (short)var3.alpha;
         var2[var4 * 4 + 1] = (short)var3.red;
         var2[var4 * 4 + 2] = (short)var3.green;
         var2[var4 * 4 + 3] = (short)var3.blue;
      }

      return var2;
   }

   private static long getFormatPtr(int var0) {
      switch(var0) {
      case 0:
         return FMTPTR_ARGB32;
      case 2:
         return FMTPTR_A8;
      default:
         return 0L;
      }
   }

   public int createLinearGradient(Point2D var1, Point2D var2, float[] var3, int[] var4, int var5) {
      short[] var6 = this.getRenderColors(var4);
      int var7 = XRCreateLinearGradientPaintNative(var3, var6, XRUtils.XDoubleToFixed(var1.getX()), XRUtils.XDoubleToFixed(var1.getY()), XRUtils.XDoubleToFixed(var2.getX()), XRUtils.XDoubleToFixed(var2.getY()), var3.length, var5);
      return var7;
   }

   public int createRadialGradient(float var1, float var2, float var3, float var4, float[] var5, int[] var6, int var7) {
      short[] var8 = this.getRenderColors(var6);
      return XRCreateRadialGradientPaintNative(var5, var8, var5.length, XRUtils.XDoubleToFixed((double)var1), XRUtils.XDoubleToFixed((double)var2), XRUtils.XDoubleToFixed((double)var3), XRUtils.XDoubleToFixed((double)var4), var7);
   }

   public void setGCClipRectangles(long var1, Region var3) {
      XRSetClipNative(var1, var3.getLoX(), var3.getLoY(), var3.getHiX(), var3.getHiY(), var3.isRectangular() ? null : var3, true);
   }

   public void setClipRectangles(int var1, Region var2) {
      if (var2 != null) {
         XRSetClipNative((long)var1, var2.getLoX(), var2.getLoY(), var2.getHiX(), var2.getHiY(), var2.isRectangular() ? null : var2, false);
      } else {
         XRSetClipNative((long)var1, 0, 0, 32767, 32767, (Region)null, false);
      }

   }

   public void renderRectangles(int var1, byte var2, XRColor var3, GrowableRectArray var4) {
      XRenderRectanglesNative(var1, var2, (short)var3.red, (short)var3.green, (short)var3.blue, (short)var3.alpha, var4.getArray(), var4.getSize());
   }

   private static long[] getGlyphInfoPtrs(List<XRGlyphCacheEntry> var0) {
      long[] var1 = new long[var0.size()];

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         var1[var2] = ((XRGlyphCacheEntry)var0.get(var2)).getGlyphInfoPtr();
      }

      return var1;
   }

   public void XRenderAddGlyphs(int var1, GlyphList var2, List<XRGlyphCacheEntry> var3, byte[] var4) {
      long[] var5 = getGlyphInfoPtrs(var3);
      XRAddGlyphsNative(var1, var5, var5.length, var4, var4.length);
   }

   public void XRenderFreeGlyphs(int var1, int[] var2) {
      XRFreeGlyphsNative(var1, var2, var2.length);
   }

   private static native void XRAddGlyphsNative(int var0, long[] var1, int var2, byte[] var3, int var4);

   private static native void XRFreeGlyphsNative(int var0, int[] var1, int var2);

   private static native void XRenderCompositeTextNative(int var0, int var1, int var2, int var3, int var4, long var5, int[] var7, int[] var8, int var9, int var10);

   public int XRenderCreateGlyphSet(int var1) {
      return XRenderCreateGlyphSetNative(getFormatPtr(var1));
   }

   private static native int XRenderCreateGlyphSetNative(long var0);

   public void XRenderCompositeText(byte var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, GrowableEltArray var10) {
      GrowableIntArray var11 = var10.getGlyphs();
      XRenderCompositeTextNative(var1, var2, var3, var5, var6, 0L, var10.getArray(), var11.getArray(), var10.getSize(), var11.getSize());
   }

   public void putMaskImage(int var1, long var2, byte[] var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, float var13) {
      putMaskNative(var1, var2, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, MASK_XIMG);
   }

   private static native void putMaskNative(int var0, long var1, byte[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, float var12, long var13);

   public void padBlit(byte var1, int var2, int var3, int var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15) {
      padBlitNative(var1, var2, var3, var4, XRUtils.XDoubleToFixed(var5.getScaleX()), XRUtils.XDoubleToFixed(var5.getShearX()), XRUtils.XDoubleToFixed(var5.getTranslateX()), XRUtils.XDoubleToFixed(var5.getShearY()), XRUtils.XDoubleToFixed(var5.getScaleY()), XRUtils.XDoubleToFixed(var5.getTranslateY()), var6, var7, var8, var9, var10, var11, var12, var13, var14, var15);
   }

   private static native void padBlitNative(byte var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, int var17, int var18, int var19);

   public void renderCompositeTrapezoids(byte var1, int var2, int var3, int var4, int var5, int var6, TrapezoidList var7) {
      renderCompositeTrapezoidsNative(var1, var2, getFormatPtr(var3), var4, var5, var6, var7.getTrapArray());
   }

   private static native void renderCompositeTrapezoidsNative(byte var0, int var1, long var2, int var4, int var5, int var6, int[] var7);

   static {
      initIDs();
   }
}
