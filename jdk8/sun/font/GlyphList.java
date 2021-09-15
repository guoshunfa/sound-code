package sun.font;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;

public final class GlyphList {
   private static final int MINGRAYLENGTH = 1024;
   private static final int MAXGRAYLENGTH = 8192;
   private static final int DEFAULT_LENGTH = 32;
   int glyphindex;
   int[] metrics;
   byte[] graybits;
   Object strikelist;
   int len = 0;
   int maxLen = 0;
   int maxPosLen = 0;
   int[] glyphData;
   char[] chData;
   long[] images;
   float[] positions;
   float x;
   float y;
   float gposx;
   float gposy;
   boolean usePositions;
   boolean lcdRGBOrder;
   boolean lcdSubPixPos;
   private static GlyphList reusableGL = new GlyphList();
   private static boolean inUse;

   void ensureCapacity(int var1) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (this.usePositions && var1 > this.maxPosLen) {
         this.positions = new float[var1 * 2 + 2];
         this.maxPosLen = var1;
      }

      if (this.maxLen == 0 || var1 > this.maxLen) {
         this.glyphData = new int[var1];
         this.chData = new char[var1];
         this.images = new long[var1];
         this.maxLen = var1;
      }

   }

   private GlyphList() {
   }

   public static GlyphList getInstance() {
      if (inUse) {
         return new GlyphList();
      } else {
         Class var0 = GlyphList.class;
         synchronized(GlyphList.class) {
            if (inUse) {
               return new GlyphList();
            } else {
               inUse = true;
               return reusableGL;
            }
         }
      }
   }

   public boolean setFromString(FontInfo var1, String var2, float var3, float var4) {
      this.x = var3;
      this.y = var4;
      this.strikelist = var1.fontStrike;
      this.lcdRGBOrder = var1.lcdRGBOrder;
      this.lcdSubPixPos = var1.lcdSubPixPos;
      this.len = var2.length();
      this.ensureCapacity(this.len);
      var2.getChars(0, this.len, this.chData, 0);
      return this.mapChars(var1, this.len);
   }

   public boolean setFromChars(FontInfo var1, char[] var2, int var3, int var4, float var5, float var6) {
      this.x = var5;
      this.y = var6;
      this.strikelist = var1.fontStrike;
      this.lcdRGBOrder = var1.lcdRGBOrder;
      this.lcdSubPixPos = var1.lcdSubPixPos;
      this.len = var4;
      if (var4 < 0) {
         this.len = 0;
      } else {
         this.len = var4;
      }

      this.ensureCapacity(this.len);
      System.arraycopy(var2, var3, this.chData, 0, this.len);
      return this.mapChars(var1, this.len);
   }

   private final boolean mapChars(FontInfo var1, int var2) {
      if (var1.font2D.getMapper().charsToGlyphsNS(var2, this.chData, this.glyphData)) {
         return false;
      } else {
         var1.fontStrike.getGlyphImagePtrs(this.glyphData, this.images, var2);
         this.glyphindex = -1;
         return true;
      }
   }

   public void setFromGlyphVector(FontInfo var1, GlyphVector var2, float var3, float var4) {
      this.x = var3;
      this.y = var4;
      this.lcdRGBOrder = var1.lcdRGBOrder;
      this.lcdSubPixPos = var1.lcdSubPixPos;
      StandardGlyphVector var5 = StandardGlyphVector.getStandardGV(var2, var1);
      this.usePositions = var5.needsPositions(var1.devTx);
      this.len = var5.getNumGlyphs();
      this.ensureCapacity(this.len);
      this.strikelist = var5.setupGlyphImages(this.images, this.usePositions ? this.positions : null, var1.devTx);
      this.glyphindex = -1;
   }

   public int[] getBounds() {
      if (this.glyphindex >= 0) {
         throw new InternalError("calling getBounds after setGlyphIndex");
      } else {
         if (this.metrics == null) {
            this.metrics = new int[5];
         }

         this.gposx = this.x + 0.5F;
         this.gposy = this.y + 0.5F;
         this.fillBounds(this.metrics);
         return this.metrics;
      }
   }

   public void setGlyphIndex(int var1) {
      this.glyphindex = var1;
      float var2 = StrikeCache.unsafe.getFloat(this.images[var1] + (long)StrikeCache.topLeftXOffset);
      float var3 = StrikeCache.unsafe.getFloat(this.images[var1] + (long)StrikeCache.topLeftYOffset);
      if (this.usePositions) {
         this.metrics[0] = (int)Math.floor((double)(this.positions[var1 << 1] + this.gposx + var2));
         this.metrics[1] = (int)Math.floor((double)(this.positions[(var1 << 1) + 1] + this.gposy + var3));
      } else {
         this.metrics[0] = (int)Math.floor((double)(this.gposx + var2));
         this.metrics[1] = (int)Math.floor((double)(this.gposy + var3));
         this.gposx += StrikeCache.unsafe.getFloat(this.images[var1] + (long)StrikeCache.xAdvanceOffset);
         this.gposy += StrikeCache.unsafe.getFloat(this.images[var1] + (long)StrikeCache.yAdvanceOffset);
      }

      this.metrics[2] = StrikeCache.unsafe.getChar(this.images[var1] + (long)StrikeCache.widthOffset);
      this.metrics[3] = StrikeCache.unsafe.getChar(this.images[var1] + (long)StrikeCache.heightOffset);
      this.metrics[4] = StrikeCache.unsafe.getChar(this.images[var1] + (long)StrikeCache.rowBytesOffset);
   }

   public int[] getMetrics() {
      return this.metrics;
   }

   public byte[] getGrayBits() {
      int var1 = this.metrics[4] * this.metrics[3];
      if (this.graybits == null) {
         this.graybits = new byte[Math.max(var1, 1024)];
      } else if (var1 > this.graybits.length) {
         this.graybits = new byte[var1];
      }

      long var2 = StrikeCache.unsafe.getAddress(this.images[this.glyphindex] + (long)StrikeCache.pixelDataOffset);
      if (var2 == 0L) {
         return this.graybits;
      } else {
         for(int var4 = 0; var4 < var1; ++var4) {
            this.graybits[var4] = StrikeCache.unsafe.getByte(var2 + (long)var4);
         }

         return this.graybits;
      }
   }

   public long[] getImages() {
      return this.images;
   }

   public boolean usePositions() {
      return this.usePositions;
   }

   public float[] getPositions() {
      return this.positions;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public Object getStrike() {
      return this.strikelist;
   }

   public boolean isSubPixPos() {
      return this.lcdSubPixPos;
   }

   public boolean isRGBOrder() {
      return this.lcdRGBOrder;
   }

   public void dispose() {
      if (this == reusableGL) {
         if (this.graybits != null && this.graybits.length > 8192) {
            this.graybits = null;
         }

         this.usePositions = false;
         this.strikelist = null;
         inUse = false;
      }

   }

   public int getNumGlyphs() {
      return this.len;
   }

   private void fillBounds(int[] var1) {
      int var2 = StrikeCache.topLeftXOffset;
      int var3 = StrikeCache.topLeftYOffset;
      int var4 = StrikeCache.widthOffset;
      int var5 = StrikeCache.heightOffset;
      int var6 = StrikeCache.xAdvanceOffset;
      int var7 = StrikeCache.yAdvanceOffset;
      if (this.len == 0) {
         var1[0] = var1[1] = var1[2] = var1[3] = 0;
      } else {
         float var9 = Float.POSITIVE_INFINITY;
         float var8 = Float.POSITIVE_INFINITY;
         float var11 = Float.NEGATIVE_INFINITY;
         float var10 = Float.NEGATIVE_INFINITY;
         int var12 = 0;
         float var13 = this.x + 0.5F;
         float var14 = this.y + 0.5F;

         for(int var23 = 0; var23 < this.len; ++var23) {
            float var17 = StrikeCache.unsafe.getFloat(this.images[var23] + (long)var2);
            float var18 = StrikeCache.unsafe.getFloat(this.images[var23] + (long)var3);
            char var15 = StrikeCache.unsafe.getChar(this.images[var23] + (long)var4);
            char var16 = StrikeCache.unsafe.getChar(this.images[var23] + (long)var5);
            float var19;
            float var20;
            if (this.usePositions) {
               var19 = this.positions[var12++] + var17 + var13;
               var20 = this.positions[var12++] + var18 + var14;
            } else {
               var19 = var13 + var17;
               var20 = var14 + var18;
               var13 += StrikeCache.unsafe.getFloat(this.images[var23] + (long)var6);
               var14 += StrikeCache.unsafe.getFloat(this.images[var23] + (long)var7);
            }

            float var21 = var19 + (float)var15;
            float var22 = var20 + (float)var16;
            if (var8 > var19) {
               var8 = var19;
            }

            if (var9 > var20) {
               var9 = var20;
            }

            if (var10 < var21) {
               var10 = var21;
            }

            if (var11 < var22) {
               var11 = var22;
            }
         }

         var1[0] = (int)Math.floor((double)var8);
         var1[1] = (int)Math.floor((double)var9);
         var1[2] = (int)Math.floor((double)var10);
         var1[3] = (int)Math.floor((double)var11);
      }
   }
}
