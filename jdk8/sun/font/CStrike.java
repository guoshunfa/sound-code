package sun.font;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

public final class CStrike extends PhysicalStrike {
   private final CFont nativeFont;
   private AffineTransform invDevTx;
   private final CStrike.GlyphInfoCache glyphInfoCache;
   private final CStrike.GlyphAdvanceCache glyphAdvanceCache;
   private long nativeStrikePtr;

   private static native long createNativeStrikePtr(long var0, double[] var2, double[] var3, int var4, int var5);

   private static native void disposeNativeStrikePtr(long var0);

   private static native StrikeMetrics getFontMetrics(long var0);

   private static native void getGlyphImagePtrsNative(long var0, long[] var2, int[] var3, int var4);

   private static native float getNativeGlyphAdvance(long var0, int var2);

   private static native GeneralPath getNativeGlyphOutline(long var0, int var2, double var3, double var5);

   private static native void getNativeGlyphImageBounds(long var0, int var2, Rectangle2D.Float var3, double var4, double var6);

   CStrike(CFont var1, FontStrikeDesc var2) {
      this.nativeFont = var1;
      this.desc = var2;
      this.glyphInfoCache = new CStrike.GlyphInfoCache(var1, this.desc);
      this.glyphAdvanceCache = new CStrike.GlyphAdvanceCache();
      this.disposer = this.glyphInfoCache;
      if (var2.devTx != null && !var2.devTx.isIdentity()) {
         try {
            this.invDevTx = var2.devTx.createInverse();
         } catch (NoninvertibleTransformException var4) {
         }
      }

   }

   public long getNativeStrikePtr() {
      if (this.nativeStrikePtr != 0L) {
         return this.nativeStrikePtr;
      } else {
         double[] var1 = new double[6];
         this.desc.glyphTx.getMatrix(var1);
         double[] var2 = new double[6];
         if (this.invDevTx == null) {
            var2[0] = 1.0D;
            var2[3] = 1.0D;
         } else {
            this.invDevTx.getMatrix(var2);
         }

         int var3 = this.desc.aaHint;
         int var4 = this.desc.fmHint;
         synchronized(this) {
            if (this.nativeStrikePtr != 0L) {
               return this.nativeStrikePtr;
            }

            this.nativeStrikePtr = createNativeStrikePtr(this.nativeFont.getNativeFontPtr(), var1, var2, var3, var4);
         }

         return this.nativeStrikePtr;
      }
   }

   protected synchronized void finalize() throws Throwable {
      if (this.nativeStrikePtr != 0L) {
         disposeNativeStrikePtr(this.nativeStrikePtr);
      }

      this.nativeStrikePtr = 0L;
   }

   public int getNumGlyphs() {
      return this.nativeFont.getNumGlyphs();
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         StrikeMetrics var1 = getFontMetrics(this.getNativeStrikePtr());
         if (this.invDevTx != null) {
            var1.convertToUserSpace(this.invDevTx);
         }

         var1.convertToUserSpace(this.desc.glyphTx);
         this.strikeMetrics = var1;
      }

      return this.strikeMetrics;
   }

   float getGlyphAdvance(int var1) {
      return this.getCachedNativeGlyphAdvance(var1);
   }

   float getCodePointAdvance(int var1) {
      return this.getGlyphAdvance(this.nativeFont.getMapper().charToGlyph(var1));
   }

   Point2D.Float getCharMetrics(char var1) {
      return this.getGlyphMetrics(this.nativeFont.getMapper().charToGlyph(var1));
   }

   Point2D.Float getGlyphMetrics(int var1) {
      return new Point2D.Float(this.getGlyphAdvance(var1), 0.0F);
   }

   Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      GeneralPath var2 = this.getGlyphOutline(var1, 0.0F, 0.0F);
      Rectangle2D var3 = var2.getBounds2D();
      Rectangle2D.Float var4;
      if (var3 instanceof Rectangle2D.Float) {
         var4 = (Rectangle2D.Float)var3;
      } else {
         float var5 = (float)var3.getX();
         float var6 = (float)var3.getY();
         float var7 = (float)var3.getWidth();
         float var8 = (float)var3.getHeight();
         var4 = new Rectangle2D.Float(var5, var6, var7, var8);
      }

      return var4;
   }

   void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3) {
      Rectangle2D.Float var4 = new Rectangle2D.Float();
      if (this.invDevTx != null) {
         this.invDevTx.transform(var2, var2);
      }

      this.getGlyphImageBounds(var1, var2.x, var2.y, var4);
      if (var4.width == 0.0F && var4.height == 0.0F) {
         var3.setRect(0.0D, 0.0D, -1.0D, -1.0D);
      } else {
         var3.setRect((double)(var4.x + var2.x), (double)(var4.y + var2.y), (double)var4.width, (double)var4.height);
      }
   }

   private void getGlyphImageBounds(int var1, float var2, float var3, Rectangle2D.Float var4) {
      getNativeGlyphImageBounds(this.getNativeStrikePtr(), var1, var4, (double)var2, (double)var3);
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      return getNativeGlyphOutline(this.getNativeStrikePtr(), var1, (double)var2, (double)var3);
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      throw new Error("not implemented yet");
   }

   long getGlyphImagePtr(int var1) {
      synchronized(this.glyphInfoCache) {
         long var3 = this.glyphInfoCache.get(var1);
         if (var3 != 0L) {
            return var3;
         } else {
            long[] var5 = new long[1];
            int[] var6 = new int[]{var1};
            this.getGlyphImagePtrs(var6, var5, 1);
            var3 = var5[0];
            this.glyphInfoCache.put(var1, var3);
            return var3;
         }
      }
   }

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
      synchronized(this.glyphInfoCache) {
         int var5 = 0;

         for(int var6 = 0; var6 < var3; ++var6) {
            int var7 = var1[var6];
            long var8 = this.glyphInfoCache.get(var7);
            if (var8 != 0L) {
               var2[var6] = var8;
            } else {
               var2[var6] = 0L;
               ++var5;
            }
         }

         if (var5 != 0) {
            int[] var19 = new int[var5];
            int[] var20 = new int[var5];
            int var21 = 0;
            int var9 = 0;

            int var10;
            int var12;
            for(var10 = 0; var10 < var3; ++var10) {
               if (var2[var10] == 0L) {
                  int var11 = var1[var10];
                  if (this.glyphInfoCache.get(var11) == -1L) {
                     var20[var21] = -1;
                     ++var9;
                     ++var21;
                  } else {
                     var12 = var21 - var9;
                     var19[var12] = var11;
                     this.glyphInfoCache.put(var11, -1L);
                     var20[var21] = var12;
                     ++var21;
                  }
               }
            }

            var10 = var21 - var9;
            long[] var22 = new long[var10];
            this.getFilteredGlyphImagePtrs(var22, var19, var10);
            var21 = 0;

            for(var12 = 0; var12 < var3; ++var12) {
               if (var2[var12] == 0L || var2[var12] == -1L) {
                  int var13 = var20[var21];
                  int var14 = var1[var12];
                  if ((long)var13 == -1L) {
                     var2[var12] = this.glyphInfoCache.get(var14);
                  } else {
                     long var15 = var22[var13];
                     var2[var12] = var15;
                     this.glyphInfoCache.put(var14, var15);
                  }

                  ++var21;
               }
            }

         }
      }
   }

   private void getFilteredGlyphImagePtrs(long[] var1, int[] var2, int var3) {
      getGlyphImagePtrsNative(this.getNativeStrikePtr(), var1, var2, var3);
   }

   private float getCachedNativeGlyphAdvance(int var1) {
      synchronized(this.glyphAdvanceCache) {
         float var3 = this.glyphAdvanceCache.get(var1);
         if (var3 != 0.0F) {
            return var3;
         } else {
            var3 = getNativeGlyphAdvance(this.getNativeStrikePtr(), var1);
            this.glyphAdvanceCache.put(var1, var3);
            return var3;
         }
      }
   }

   private static class GlyphAdvanceCache {
      private static final int FIRST_LAYER_SIZE = 256;
      private static final int SECOND_LAYER_SIZE = 16384;
      private final float[] firstLayerCache = new float[256];
      private CStrike.GlyphAdvanceCache.SparseBitShiftingTwoLayerArray secondLayerCache;
      private HashMap<Integer, Float> generalCache;

      GlyphAdvanceCache() {
      }

      public synchronized float get(int var1) {
         if (var1 < 0) {
            if (-var1 < 16384) {
               if (this.secondLayerCache == null) {
                  return 0.0F;
               }

               return this.secondLayerCache.get(-var1);
            }
         } else if (var1 < 256) {
            return this.firstLayerCache[var1];
         }

         if (this.generalCache == null) {
            return 0.0F;
         } else {
            Float var2 = (Float)this.generalCache.get(new Integer(var1));
            return var2 == null ? 0.0F : var2;
         }
      }

      public synchronized void put(int var1, float var2) {
         if (var1 < 0) {
            if (-var1 < 16384) {
               if (this.secondLayerCache == null) {
                  this.secondLayerCache = new CStrike.GlyphAdvanceCache.SparseBitShiftingTwoLayerArray(16384, 7);
               }

               this.secondLayerCache.put(-var1, var2);
               return;
            }
         } else if (var1 < 256) {
            this.firstLayerCache[var1] = var2;
            return;
         }

         if (this.generalCache == null) {
            this.generalCache = new HashMap();
         }

         this.generalCache.put(new Integer(var1), new Float(var2));
      }

      private static class SparseBitShiftingTwoLayerArray {
         final float[][] cache;
         final int shift;
         final int secondLayerLength;

         SparseBitShiftingTwoLayerArray(int var1, int var2) {
            this.shift = var2;
            this.cache = new float[1 << var2][];
            this.secondLayerLength = var1 >> var2;
         }

         public float get(int var1) {
            int var2 = var1 >> this.shift;
            float[] var3 = this.cache[var2];
            return var3 == null ? 0.0F : var3[var1 - var2 * (1 << this.shift)];
         }

         public void put(int var1, float var2) {
            int var3 = var1 >> this.shift;
            float[] var4 = this.cache[var3];
            if (var4 == null) {
               this.cache[var3] = var4 = new float[this.secondLayerLength];
            }

            var4[var1 - var3 * (1 << this.shift)] = var2;
         }
      }
   }

   private static class GlyphInfoCache extends CStrikeDisposer {
      private static final int FIRST_LAYER_SIZE = 256;
      private static final int SECOND_LAYER_SIZE = 16384;
      private boolean disposed = false;
      private final long[] firstLayerCache = new long[256];
      private CStrike.GlyphInfoCache.SparseBitShiftingTwoLayerArray secondLayerCache;
      private HashMap<Integer, Long> generalCache;

      GlyphInfoCache(Font2D var1, FontStrikeDesc var2) {
         super(var1, var2);
      }

      public synchronized long get(int var1) {
         if (var1 < 0) {
            if (-var1 < 16384) {
               if (this.secondLayerCache == null) {
                  return 0L;
               }

               return this.secondLayerCache.get(-var1);
            }
         } else if (var1 < 256) {
            return this.firstLayerCache[var1];
         }

         if (this.generalCache == null) {
            return 0L;
         } else {
            Long var2 = (Long)this.generalCache.get(new Integer(var1));
            return var2 == null ? 0L : var2;
         }
      }

      public synchronized void put(int var1, long var2) {
         if (var1 < 0) {
            if (-var1 < 16384) {
               if (this.secondLayerCache == null) {
                  this.secondLayerCache = new CStrike.GlyphInfoCache.SparseBitShiftingTwoLayerArray(16384, 7);
               }

               this.secondLayerCache.put(-var1, var2);
               return;
            }
         } else if (var1 < 256) {
            this.firstLayerCache[var1] = var2;
            return;
         }

         if (this.generalCache == null) {
            this.generalCache = new HashMap();
         }

         this.generalCache.put(new Integer(var1), new Long(var2));
      }

      public synchronized void dispose() {
         if (!this.disposed) {
            super.dispose();
            disposeLongArray(this.firstLayerCache);
            if (this.secondLayerCache != null) {
               long[][] var1 = this.secondLayerCache.cache;

               for(int var2 = 0; var2 < var1.length; ++var2) {
                  long[] var3 = var1[var2];
                  if (var3 != null) {
                     disposeLongArray(var3);
                  }
               }
            }

            if (this.generalCache != null) {
               Iterator var4 = this.generalCache.values().iterator();

               while(var4.hasNext()) {
                  long var5 = (Long)var4.next();
                  if (var5 != -1L && var5 != 0L) {
                     removeGlyphInfoFromCache(var5);
                     StrikeCache.freeLongPointer(var5);
                  }
               }
            }

            this.disposed = true;
         }
      }

      private static void disposeLongArray(long[] var0) {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            long var2 = var0[var1];
            if (var2 != 0L && var2 != -1L) {
               removeGlyphInfoFromCache(var2);
               StrikeCache.freeLongPointer(var2);
            }
         }

      }

      private static class SparseBitShiftingTwoLayerArray {
         final long[][] cache;
         final int shift;
         final int secondLayerLength;

         SparseBitShiftingTwoLayerArray(int var1, int var2) {
            this.shift = var2;
            this.cache = new long[1 << var2][];
            this.secondLayerLength = var1 >> var2;
         }

         public long get(int var1) {
            int var2 = var1 >> this.shift;
            long[] var3 = this.cache[var2];
            return var3 == null ? 0L : var3[var1 - var2 * (1 << this.shift)];
         }

         public void put(int var1, long var2) {
            int var4 = var1 >> this.shift;
            long[] var5 = this.cache[var4];
            if (var5 == null) {
               this.cache[var4] = var5 = new long[this.secondLayerLength];
            }

            var5[var1 - var4 * (1 << this.shift)] = var2;
         }
      }
   }
}
