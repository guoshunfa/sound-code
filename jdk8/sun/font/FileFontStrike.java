package sun.font;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class FileFontStrike extends PhysicalStrike {
   static final int INVISIBLE_GLYPHS = 65534;
   private FileFont fileFont;
   private static final int UNINITIALISED = 0;
   private static final int INTARRAY = 1;
   private static final int LONGARRAY = 2;
   private static final int SEGINTARRAY = 3;
   private static final int SEGLONGARRAY = 4;
   private volatile int glyphCacheFormat = 0;
   private static final int SEGSHIFT = 5;
   private static final int SEGSIZE = 32;
   private boolean segmentedCache;
   private int[][] segIntGlyphImages;
   private long[][] segLongGlyphImages;
   private float[] horizontalAdvances;
   private float[][] segHorizontalAdvances;
   ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
   SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
   AffineTransform invertDevTx;
   boolean useNatives;
   NativeStrike[] nativeStrikes;
   private int intPtSize;
   private static boolean isXPorLater = false;
   private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;

   private static native boolean initNative();

   FileFontStrike(FileFont var1, FontStrikeDesc var2) {
      super(var1, var2);
      this.fileFont = var1;
      if (var2.style != var1.style) {
         if ((var2.style & 2) == 2 && (var1.style & 2) == 0) {
            this.algoStyle = true;
            this.italic = 0.7F;
         }

         if ((var2.style & 1) == 1 && (var1.style & 1) == 0) {
            this.algoStyle = true;
            this.boldness = 1.33F;
         }
      }

      double[] var3 = new double[4];
      AffineTransform var4 = var2.glyphTx;
      var4.getMatrix(var3);
      if (!var2.devTx.isIdentity() && var2.devTx.getType() != 1) {
         try {
            this.invertDevTx = var2.devTx.createInverse();
         } catch (NoninvertibleTransformException var13) {
         }
      }

      boolean var5 = var2.aaHint != 1 && var1.familyName.startsWith("Amble");
      if (!Double.isNaN(var3[0]) && !Double.isNaN(var3[1]) && !Double.isNaN(var3[2]) && !Double.isNaN(var3[3]) && var1.getScaler() != null) {
         this.pScalerContext = var1.getScaler().createScalerContext(var3, var2.aaHint, var2.fmHint, this.boldness, this.italic, var5);
      } else {
         this.pScalerContext = NullFontScaler.getNullScalerContext();
      }

      this.mapper = var1.getMapper();
      int var6 = this.mapper.getNumGlyphs();
      float var7 = (float)var3[3];
      int var8 = this.intPtSize = (int)var7;
      boolean var9 = (var4.getType() & 124) == 0;
      this.segmentedCache = var6 > 256 || var6 > 64 && (!var9 || var7 != (float)var8 || var8 < 6 || var8 > 36);
      if (this.pScalerContext == 0L) {
         this.disposer = new FontStrikeDisposer(var1, var2);
         this.initGlyphCache();
         this.pScalerContext = NullFontScaler.getNullScalerContext();
         SunFontManager.getInstance().deRegisterBadFont(var1);
      } else {
         if (FontUtilities.isWindows && isXPorLater && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless() && !var1.useJavaRasterizer && (var2.aaHint == 4 || var2.aaHint == 5) && var3[1] == 0.0D && var3[2] == 0.0D && var3[0] == var3[3] && var3[0] >= 3.0D && var3[0] <= 100.0D && !((TrueTypeFont)var1).useEmbeddedBitmapsForSize(this.intPtSize)) {
            this.useNatives = true;
         } else if (var1.checkUseNatives() && var2.aaHint == 0 && !this.algoStyle && var3[1] == 0.0D && var3[2] == 0.0D && var3[0] >= 6.0D && var3[0] <= 36.0D && var3[0] == var3[3]) {
            this.useNatives = true;
            int var10 = var1.nativeFonts.length;
            this.nativeStrikes = new NativeStrike[var10];

            for(int var11 = 0; var11 < var10; ++var11) {
               this.nativeStrikes[var11] = new NativeStrike(var1.nativeFonts[var11], var2, false);
            }
         }

         if (FontUtilities.isLogging() && FontUtilities.isWindows) {
            FontUtilities.getLogger().info("Strike for " + var1 + " at size = " + this.intPtSize + " use natives = " + this.useNatives + " useJavaRasteriser = " + var1.useJavaRasterizer + " AAHint = " + var2.aaHint + " Has Embedded bitmaps = " + ((TrueTypeFont)var1).useEmbeddedBitmapsForSize(this.intPtSize));
         }

         this.disposer = new FontStrikeDisposer(var1, var2, this.pScalerContext);
         double var14 = 48.0D;
         this.getImageWithAdvance = Math.abs(var4.getScaleX()) <= var14 && Math.abs(var4.getScaleY()) <= var14 && Math.abs(var4.getShearX()) <= var14 && Math.abs(var4.getShearY()) <= var14;
         if (!this.getImageWithAdvance) {
            int var12;
            if (!this.segmentedCache) {
               this.horizontalAdvances = new float[var6];

               for(var12 = 0; var12 < var6; ++var12) {
                  this.horizontalAdvances[var12] = Float.MAX_VALUE;
               }
            } else {
               var12 = (var6 + 32 - 1) / 32;
               this.segHorizontalAdvances = new float[var12][];
            }
         }

      }
   }

   public int getNumGlyphs() {
      return this.fileFont.getNumGlyphs();
   }

   long getGlyphImageFromNative(int var1) {
      return FontUtilities.isWindows ? this.getGlyphImageFromWindows(var1) : this.getGlyphImageFromX11(var1);
   }

   private native long _getGlyphImageFromWindows(String var1, int var2, int var3, int var4, boolean var5);

   long getGlyphImageFromWindows(int var1) {
      String var2 = this.fileFont.getFamilyName((Locale)null);
      int var3 = this.desc.style & 1 | this.desc.style & 2 | this.fileFont.getStyle();
      int var4 = this.intPtSize;
      long var5 = this._getGlyphImageFromWindows(var2, var3, var4, var1, this.desc.fmHint == 2);
      if (var5 != 0L) {
         float var7 = this.getGlyphAdvance(var1, false);
         StrikeCache.unsafe.putFloat(var5 + (long)StrikeCache.xAdvanceOffset, var7);
         return var5;
      } else {
         return this.fileFont.getGlyphImage(this.pScalerContext, var1);
      }
   }

   long getGlyphImageFromX11(int var1) {
      char var4 = this.fileFont.glyphToCharMap[var1];

      for(int var5 = 0; var5 < this.nativeStrikes.length; ++var5) {
         CharToGlyphMapper var6 = this.fileFont.nativeFonts[var5].getMapper();
         int var7 = var6.charToGlyph(var4) & '\uffff';
         if (var7 != var6.getMissingGlyphCode()) {
            long var2 = this.nativeStrikes[var5].getGlyphImagePtrNoCache(var7);
            if (var2 != 0L) {
               return var2;
            }
         }
      }

      return this.fileFont.getGlyphImage(this.pScalerContext, var1);
   }

   long getGlyphImagePtr(int var1) {
      if (var1 >= 65534) {
         return StrikeCache.invisibleGlyphPtr;
      } else {
         long var2 = 0L;
         if ((var2 = this.getCachedGlyphPtr(var1)) != 0L) {
            return var2;
         } else {
            if (this.useNatives) {
               var2 = this.getGlyphImageFromNative(var1);
               if (var2 == 0L && FontUtilities.isLogging()) {
                  FontUtilities.getLogger().info("Strike for " + this.fileFont + " at size = " + this.intPtSize + " couldn't get native glyph for code = " + var1);
               }
            }

            if (var2 == 0L) {
               var2 = this.fileFont.getGlyphImage(this.pScalerContext, var1);
            }

            return this.setCachedGlyphPtr(var1, var2);
         }
      }
   }

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var1[var4];
         if (var5 >= 65534) {
            var2[var4] = StrikeCache.invisibleGlyphPtr;
         } else if ((var2[var4] = this.getCachedGlyphPtr(var5)) == 0L) {
            long var6 = 0L;
            if (this.useNatives) {
               var6 = this.getGlyphImageFromNative(var5);
            }

            if (var6 == 0L) {
               var6 = this.fileFont.getGlyphImage(this.pScalerContext, var5);
            }

            var2[var4] = this.setCachedGlyphPtr(var5, var6);
         }
      }

   }

   int getSlot0GlyphImagePtrs(int[] var1, long[] var2, int var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = var1[var5];
         if (var6 >>> 24 != 0) {
            return var4;
         }

         ++var4;
         if (var6 >= 65534) {
            var2[var5] = StrikeCache.invisibleGlyphPtr;
         } else if ((var2[var5] = this.getCachedGlyphPtr(var6)) == 0L) {
            long var7 = 0L;
            if (this.useNatives) {
               var7 = this.getGlyphImageFromNative(var6);
            }

            if (var7 == 0L) {
               var7 = this.fileFont.getGlyphImage(this.pScalerContext, var6);
            }

            var2[var5] = this.setCachedGlyphPtr(var6, var7);
         }
      }

      return var4;
   }

   long getCachedGlyphPtr(int var1) {
      try {
         return this.getCachedGlyphPtrInternal(var1);
      } catch (Exception var6) {
         NullFontScaler var3 = (NullFontScaler)FontScaler.getNullScaler();
         long var4 = NullFontScaler.getNullScalerContext();
         return var3.getGlyphImage(var4, var1);
      }
   }

   private long getCachedGlyphPtrInternal(int var1) {
      int var2;
      int var3;
      switch(this.glyphCacheFormat) {
      case 1:
         return (long)this.intGlyphImages[var1] & 4294967295L;
      case 2:
         return this.longGlyphImages[var1];
      case 3:
         var2 = var1 >> 5;
         if (this.segIntGlyphImages[var2] != null) {
            var3 = var1 % 32;
            return (long)this.segIntGlyphImages[var2][var3] & 4294967295L;
         }

         return 0L;
      case 4:
         var2 = var1 >> 5;
         if (this.segLongGlyphImages[var2] != null) {
            var3 = var1 % 32;
            return this.segLongGlyphImages[var2][var3];
         }

         return 0L;
      default:
         return 0L;
      }
   }

   private synchronized long setCachedGlyphPtr(int var1, long var2) {
      try {
         return this.setCachedGlyphPtrInternal(var1, var2);
      } catch (Exception var8) {
         switch(this.glyphCacheFormat) {
         case 1:
         case 3:
            StrikeCache.freeIntPointer((int)var2);
            break;
         case 2:
         case 4:
            StrikeCache.freeLongPointer(var2);
         }

         NullFontScaler var5 = (NullFontScaler)FontScaler.getNullScaler();
         long var6 = NullFontScaler.getNullScalerContext();
         return var5.getGlyphImage(var6, var1);
      }
   }

   private long setCachedGlyphPtrInternal(int var1, long var2) {
      int var4;
      int var5;
      switch(this.glyphCacheFormat) {
      case 1:
         if (this.intGlyphImages[var1] == 0) {
            this.intGlyphImages[var1] = (int)var2;
            return var2;
         }

         StrikeCache.freeIntPointer((int)var2);
         return (long)this.intGlyphImages[var1] & 4294967295L;
      case 2:
         if (this.longGlyphImages[var1] == 0L) {
            this.longGlyphImages[var1] = var2;
            return var2;
         }

         StrikeCache.freeLongPointer(var2);
         return this.longGlyphImages[var1];
      case 3:
         var4 = var1 >> 5;
         var5 = var1 % 32;
         if (this.segIntGlyphImages[var4] == null) {
            this.segIntGlyphImages[var4] = new int[32];
         }

         if (this.segIntGlyphImages[var4][var5] == 0) {
            this.segIntGlyphImages[var4][var5] = (int)var2;
            return var2;
         }

         StrikeCache.freeIntPointer((int)var2);
         return (long)this.segIntGlyphImages[var4][var5] & 4294967295L;
      case 4:
         var4 = var1 >> 5;
         var5 = var1 % 32;
         if (this.segLongGlyphImages[var4] == null) {
            this.segLongGlyphImages[var4] = new long[32];
         }

         if (this.segLongGlyphImages[var4][var5] == 0L) {
            this.segLongGlyphImages[var4][var5] = var2;
            return var2;
         }

         StrikeCache.freeLongPointer(var2);
         return this.segLongGlyphImages[var4][var5];
      default:
         this.initGlyphCache();
         return this.setCachedGlyphPtr(var1, var2);
      }
   }

   private synchronized void initGlyphCache() {
      int var1 = this.mapper.getNumGlyphs();
      boolean var2 = false;
      byte var4;
      if (this.segmentedCache) {
         int var3 = (var1 + 32 - 1) / 32;
         if (longAddresses) {
            var4 = 4;
            this.segLongGlyphImages = new long[var3][];
            this.disposer.segLongGlyphImages = this.segLongGlyphImages;
         } else {
            var4 = 3;
            this.segIntGlyphImages = new int[var3][];
            this.disposer.segIntGlyphImages = this.segIntGlyphImages;
         }
      } else if (longAddresses) {
         var4 = 2;
         this.longGlyphImages = new long[var1];
         this.disposer.longGlyphImages = this.longGlyphImages;
      } else {
         var4 = 1;
         this.intGlyphImages = new int[var1];
         this.disposer.intGlyphImages = this.intGlyphImages;
      }

      this.glyphCacheFormat = var4;
   }

   float getGlyphAdvance(int var1) {
      return this.getGlyphAdvance(var1, true);
   }

   private float getGlyphAdvance(int var1, boolean var2) {
      if (var1 >= 65534) {
         return 0.0F;
      } else {
         float var3;
         Point2D.Float var4;
         int var7;
         if (this.horizontalAdvances != null) {
            var3 = this.horizontalAdvances[var1];
            if (var3 != Float.MAX_VALUE) {
               if (!var2 && this.invertDevTx != null) {
                  var4 = new Point2D.Float(var3, 0.0F);
                  this.desc.devTx.deltaTransform(var4, var4);
                  return var4.x;
               }

               return var3;
            }
         } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
            var7 = var1 >> 5;
            float[] var5 = this.segHorizontalAdvances[var7];
            if (var5 != null) {
               var3 = var5[var1 % 32];
               if (var3 != Float.MAX_VALUE) {
                  if (!var2 && this.invertDevTx != null) {
                     Point2D.Float var10 = new Point2D.Float(var3, 0.0F);
                     this.desc.devTx.deltaTransform(var10, var10);
                     return var10.x;
                  }

                  return var3;
               }
            }
         }

         if (!var2 && this.invertDevTx != null) {
            var4 = new Point2D.Float();
            this.fileFont.getGlyphMetrics(this.pScalerContext, var1, var4);
            return var4.x;
         } else {
            if (this.invertDevTx == null && var2) {
               long var8;
               if (this.getImageWithAdvance) {
                  var8 = this.getGlyphImagePtr(var1);
               } else {
                  var8 = this.getCachedGlyphPtr(var1);
               }

               if (var8 != 0L) {
                  var3 = StrikeCache.unsafe.getFloat(var8 + (long)StrikeCache.xAdvanceOffset);
               } else {
                  var3 = this.fileFont.getGlyphAdvance(this.pScalerContext, var1);
               }
            } else {
               var3 = this.getGlyphMetrics(var1, var2).x;
            }

            if (this.horizontalAdvances != null) {
               this.horizontalAdvances[var1] = var3;
            } else if (this.segmentedCache && this.segHorizontalAdvances != null) {
               var7 = var1 >> 5;
               int var9 = var1 % 32;
               if (this.segHorizontalAdvances[var7] == null) {
                  this.segHorizontalAdvances[var7] = new float[32];

                  for(int var6 = 0; var6 < 32; ++var6) {
                     this.segHorizontalAdvances[var7][var6] = Float.MAX_VALUE;
                  }
               }

               this.segHorizontalAdvances[var7][var9] = var3;
            }

            return var3;
         }
      }
   }

   float getCodePointAdvance(int var1) {
      return this.getGlyphAdvance(this.mapper.charToGlyph(var1));
   }

   void getGlyphImageBounds(int var1, Point2D.Float var2, Rectangle var3) {
      long var4 = this.getGlyphImagePtr(var1);
      if (var4 == 0L) {
         var3.x = (int)Math.floor((double)var2.x);
         var3.y = (int)Math.floor((double)var2.y);
         var3.width = var3.height = 0;
      } else {
         float var6 = StrikeCache.unsafe.getFloat(var4 + (long)StrikeCache.topLeftXOffset);
         float var7 = StrikeCache.unsafe.getFloat(var4 + (long)StrikeCache.topLeftYOffset);
         var3.x = (int)Math.floor((double)(var2.x + var6));
         var3.y = (int)Math.floor((double)(var2.y + var7));
         var3.width = StrikeCache.unsafe.getShort(var4 + (long)StrikeCache.widthOffset) & '\uffff';
         var3.height = StrikeCache.unsafe.getShort(var4 + (long)StrikeCache.heightOffset) & '\uffff';
         if ((this.desc.aaHint == 4 || this.desc.aaHint == 5) && var6 <= -2.0F) {
            int var8 = this.getGlyphImageMinX(var4, var3.x);
            if (var8 > var3.x) {
               ++var3.x;
               --var3.width;
            }
         }

      }
   }

   private int getGlyphImageMinX(long var1, int var3) {
      char var4 = StrikeCache.unsafe.getChar(var1 + (long)StrikeCache.widthOffset);
      char var5 = StrikeCache.unsafe.getChar(var1 + (long)StrikeCache.heightOffset);
      char var6 = StrikeCache.unsafe.getChar(var1 + (long)StrikeCache.rowBytesOffset);
      if (var6 == var4) {
         return var3;
      } else {
         long var7 = StrikeCache.unsafe.getAddress(var1 + (long)StrikeCache.pixelDataOffset);
         if (var7 == 0L) {
            return var3;
         } else {
            for(int var9 = 0; var9 < var5; ++var9) {
               for(int var10 = 0; var10 < 3; ++var10) {
                  if (StrikeCache.unsafe.getByte(var7 + (long)(var9 * var6) + (long)var10) != 0) {
                     return var3;
                  }
               }
            }

            return var3 + 1;
         }
      }
   }

   StrikeMetrics getFontMetrics() {
      if (this.strikeMetrics == null) {
         this.strikeMetrics = this.fileFont.getFontMetrics(this.pScalerContext);
         if (this.invertDevTx != null) {
            this.strikeMetrics.convertToUserSpace(this.invertDevTx);
         }
      }

      return this.strikeMetrics;
   }

   Point2D.Float getGlyphMetrics(int var1) {
      return this.getGlyphMetrics(var1, true);
   }

   private Point2D.Float getGlyphMetrics(int var1, boolean var2) {
      Point2D.Float var3 = new Point2D.Float();
      if (var1 >= 65534) {
         return var3;
      } else {
         long var4;
         if (this.getImageWithAdvance && var2) {
            var4 = this.getGlyphImagePtr(var1);
         } else {
            var4 = this.getCachedGlyphPtr(var1);
         }

         if (var4 != 0L) {
            var3 = new Point2D.Float();
            var3.x = StrikeCache.unsafe.getFloat(var4 + (long)StrikeCache.xAdvanceOffset);
            var3.y = StrikeCache.unsafe.getFloat(var4 + (long)StrikeCache.yAdvanceOffset);
            if (this.invertDevTx != null) {
               this.invertDevTx.deltaTransform(var3, var3);
            }
         } else {
            Integer var6 = var1;
            Point2D.Float var7 = null;
            ConcurrentHashMap var8 = null;
            if (this.glyphMetricsMapRef != null) {
               var8 = (ConcurrentHashMap)this.glyphMetricsMapRef.get();
            }

            if (var8 != null) {
               var7 = (Point2D.Float)var8.get(var6);
               if (var7 != null) {
                  var3.x = var7.x;
                  var3.y = var7.y;
                  return var3;
               }
            }

            if (var7 == null) {
               this.fileFont.getGlyphMetrics(this.pScalerContext, var1, var3);
               if (this.invertDevTx != null) {
                  this.invertDevTx.deltaTransform(var3, var3);
               }

               var7 = new Point2D.Float(var3.x, var3.y);
               if (var8 == null) {
                  var8 = new ConcurrentHashMap();
                  this.glyphMetricsMapRef = new SoftReference(var8);
               }

               var8.put(var6, var7);
            }
         }

         return var3;
      }
   }

   Point2D.Float getCharMetrics(char var1) {
      return this.getGlyphMetrics(this.mapper.charToGlyph(var1));
   }

   Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      if (this.boundsMap == null) {
         this.boundsMap = new ConcurrentHashMap();
      }

      Integer var2 = var1;
      Rectangle2D.Float var3 = (Rectangle2D.Float)this.boundsMap.get(var2);
      if (var3 == null) {
         var3 = this.fileFont.getGlyphOutlineBounds(this.pScalerContext, var1);
         this.boundsMap.put(var2, var3);
      }

      return var3;
   }

   public Rectangle2D getOutlineBounds(int var1) {
      return this.fileFont.getGlyphOutlineBounds(this.pScalerContext, var1);
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      GeneralPath var4 = null;
      ConcurrentHashMap var5 = null;
      if (this.outlineMapRef != null) {
         var5 = (ConcurrentHashMap)this.outlineMapRef.get();
         if (var5 != null) {
            var4 = (GeneralPath)var5.get(var1);
         }
      }

      if (var4 == null) {
         var4 = this.fileFont.getGlyphOutline(this.pScalerContext, var1, 0.0F, 0.0F);
         if (var5 == null) {
            var5 = new ConcurrentHashMap();
            this.outlineMapRef = new WeakReference(var5);
         }

         var5.put(var1, var4);
      }

      var4 = (GeneralPath)var4.clone();
      if (var2 != 0.0F || var3 != 0.0F) {
         var4.transform(AffineTransform.getTranslateInstance((double)var2, (double)var3));
      }

      return var4;
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      return this.fileFont.getGlyphVectorOutline(this.pScalerContext, var1, var1.length, var2, var3);
   }

   protected void adjustPoint(Point2D.Float var1) {
      if (this.invertDevTx != null) {
         this.invertDevTx.deltaTransform(var1, var1);
      }

   }

   static {
      if (FontUtilities.isWindows && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless()) {
         isXPorLater = initNative();
      }

   }
}
