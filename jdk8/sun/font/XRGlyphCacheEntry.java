package sun.font;

import java.io.ByteArrayOutputStream;

public class XRGlyphCacheEntry {
   long glyphInfoPtr;
   int lastUsed;
   boolean pinned;
   int xOff;
   int yOff;
   int glyphSet;

   public XRGlyphCacheEntry(long var1, GlyphList var3) {
      this.glyphInfoPtr = var1;
      this.xOff = Math.round(this.getXAdvance());
      this.yOff = Math.round(this.getYAdvance());
   }

   public int getXOff() {
      return this.xOff;
   }

   public int getYOff() {
      return this.yOff;
   }

   public void setGlyphSet(int var1) {
      this.glyphSet = var1;
   }

   public int getGlyphSet() {
      return this.glyphSet;
   }

   public static int getGlyphID(long var0) {
      return (int)StrikeCache.unsafe.getAddress(var0 + (long)StrikeCache.cacheCellOffset);
   }

   public static void setGlyphID(long var0, int var2) {
      StrikeCache.unsafe.putAddress(var0 + (long)StrikeCache.cacheCellOffset, (long)var2);
   }

   public int getGlyphID() {
      return getGlyphID(this.glyphInfoPtr);
   }

   public void setGlyphID(int var1) {
      setGlyphID(this.glyphInfoPtr, var1);
   }

   public float getXAdvance() {
      return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + (long)StrikeCache.xAdvanceOffset);
   }

   public float getYAdvance() {
      return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + (long)StrikeCache.yAdvanceOffset);
   }

   public int getSourceRowBytes() {
      return StrikeCache.unsafe.getShort(this.glyphInfoPtr + (long)StrikeCache.rowBytesOffset);
   }

   public int getWidth() {
      return StrikeCache.unsafe.getShort(this.glyphInfoPtr + (long)StrikeCache.widthOffset);
   }

   public int getHeight() {
      return StrikeCache.unsafe.getShort(this.glyphInfoPtr + (long)StrikeCache.heightOffset);
   }

   public void writePixelData(ByteArrayOutputStream var1, boolean var2) {
      long var3 = StrikeCache.unsafe.getAddress(this.glyphInfoPtr + (long)StrikeCache.pixelDataOffset);
      if (var3 != 0L) {
         int var5 = this.getWidth();
         int var6 = this.getHeight();
         int var7 = this.getSourceRowBytes();
         int var8 = this.getPaddedWidth(var2);
         int var9;
         int var10;
         if (!var2) {
            for(var9 = 0; var9 < var6; ++var9) {
               for(var10 = 0; var10 < var8; ++var10) {
                  if (var10 < var5) {
                     var1.write(StrikeCache.unsafe.getByte(var3 + (long)(var9 * var7 + var10)));
                  } else {
                     var1.write(0);
                  }
               }
            }
         } else {
            for(var9 = 0; var9 < var6; ++var9) {
               var10 = var9 * var7;
               int var11 = var5 * 3;

               for(int var12 = 0; var12 < var11; var12 += 3) {
                  var1.write(StrikeCache.unsafe.getByte(var3 + (long)(var10 + var12 + 2)));
                  var1.write(StrikeCache.unsafe.getByte(var3 + (long)(var10 + var12 + 1)));
                  var1.write(StrikeCache.unsafe.getByte(var3 + (long)(var10 + var12 + 0)));
                  var1.write(255);
               }
            }
         }

      }
   }

   public float getTopLeftXOffset() {
      return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + (long)StrikeCache.topLeftXOffset);
   }

   public float getTopLeftYOffset() {
      return StrikeCache.unsafe.getFloat(this.glyphInfoPtr + (long)StrikeCache.topLeftYOffset);
   }

   public long getGlyphInfoPtr() {
      return this.glyphInfoPtr;
   }

   public boolean isGrayscale(boolean var1) {
      return this.getSourceRowBytes() == this.getWidth() && (this.getWidth() != 0 || this.getHeight() != 0 || !var1);
   }

   public int getPaddedWidth(boolean var1) {
      int var2 = this.getWidth();
      return this.isGrayscale(var1) ? (int)Math.ceil((double)var2 / 4.0D) * 4 : var2;
   }

   public int getDestinationRowBytes(boolean var1) {
      boolean var2 = this.isGrayscale(var1);
      return var2 ? this.getPaddedWidth(var2) : this.getWidth() * 4;
   }

   public int getGlyphDataLenth(boolean var1) {
      return this.getDestinationRowBytes(var1) * this.getHeight();
   }

   public void setPinned() {
      this.pinned = true;
   }

   public void setUnpinned() {
      this.pinned = false;
   }

   public int getLastUsed() {
      return this.lastUsed;
   }

   public void setLastUsed(int var1) {
      this.lastUsed = var1;
   }

   public int getPixelCnt() {
      return this.getWidth() * this.getHeight();
   }

   public boolean isPinned() {
      return this.pinned;
   }
}
