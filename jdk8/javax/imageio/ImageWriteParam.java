package javax.imageio;

import java.awt.Dimension;
import java.util.Locale;

public class ImageWriteParam extends IIOParam {
   public static final int MODE_DISABLED = 0;
   public static final int MODE_DEFAULT = 1;
   public static final int MODE_EXPLICIT = 2;
   public static final int MODE_COPY_FROM_METADATA = 3;
   private static final int MAX_MODE = 3;
   protected boolean canWriteTiles = false;
   protected int tilingMode = 3;
   protected Dimension[] preferredTileSizes = null;
   protected boolean tilingSet = false;
   protected int tileWidth = 0;
   protected int tileHeight = 0;
   protected boolean canOffsetTiles = false;
   protected int tileGridXOffset = 0;
   protected int tileGridYOffset = 0;
   protected boolean canWriteProgressive = false;
   protected int progressiveMode = 3;
   protected boolean canWriteCompressed = false;
   protected int compressionMode = 3;
   protected String[] compressionTypes = null;
   protected String compressionType = null;
   protected float compressionQuality = 1.0F;
   protected Locale locale = null;

   protected ImageWriteParam() {
   }

   public ImageWriteParam(Locale var1) {
      this.locale = var1;
   }

   private static Dimension[] clonePreferredTileSizes(Dimension[] var0) {
      if (var0 == null) {
         return null;
      } else {
         Dimension[] var1 = new Dimension[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = new Dimension(var0[var2]);
         }

         return var1;
      }
   }

   public Locale getLocale() {
      return this.locale;
   }

   public boolean canWriteTiles() {
      return this.canWriteTiles;
   }

   public boolean canOffsetTiles() {
      return this.canOffsetTiles;
   }

   public void setTilingMode(int var1) {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (var1 >= 0 && var1 <= 3) {
         this.tilingMode = var1;
         if (var1 == 2) {
            this.unsetTiling();
         }

      } else {
         throw new IllegalArgumentException("Illegal value for mode!");
      }
   }

   public int getTilingMode() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported");
      } else {
         return this.tilingMode;
      }
   }

   public Dimension[] getPreferredTileSizes() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported");
      } else {
         return clonePreferredTileSizes(this.preferredTileSizes);
      }
   }

   public void setTiling(int var1, int var2, int var3, int var4) {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else if (var1 > 0 && var2 > 0) {
         boolean var5 = var3 != 0 || var4 != 0;
         if (!this.canOffsetTiles() && var5) {
            throw new UnsupportedOperationException("Can't offset tiles!");
         } else {
            if (this.preferredTileSizes != null) {
               boolean var6 = true;

               for(int var7 = 0; var7 < this.preferredTileSizes.length; var7 += 2) {
                  Dimension var8 = this.preferredTileSizes[var7];
                  Dimension var9 = this.preferredTileSizes[var7 + 1];
                  if (var1 < var8.width || var1 > var9.width || var2 < var8.height || var2 > var9.height) {
                     var6 = false;
                     break;
                  }
               }

               if (!var6) {
                  throw new IllegalArgumentException("Illegal tile size!");
               }
            }

            this.tilingSet = true;
            this.tileWidth = var1;
            this.tileHeight = var2;
            this.tileGridXOffset = var3;
            this.tileGridYOffset = var4;
         }
      } else {
         throw new IllegalArgumentException("tile dimensions are non-positive!");
      }
   }

   public void unsetTiling() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else {
         this.tilingSet = false;
         this.tileWidth = 0;
         this.tileHeight = 0;
         this.tileGridXOffset = 0;
         this.tileGridYOffset = 0;
      }
   }

   public int getTileWidth() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else if (!this.tilingSet) {
         throw new IllegalStateException("Tiling parameters not set!");
      } else {
         return this.tileWidth;
      }
   }

   public int getTileHeight() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else if (!this.tilingSet) {
         throw new IllegalStateException("Tiling parameters not set!");
      } else {
         return this.tileHeight;
      }
   }

   public int getTileGridXOffset() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else if (!this.tilingSet) {
         throw new IllegalStateException("Tiling parameters not set!");
      } else {
         return this.tileGridXOffset;
      }
   }

   public int getTileGridYOffset() {
      if (!this.canWriteTiles()) {
         throw new UnsupportedOperationException("Tiling not supported!");
      } else if (this.getTilingMode() != 2) {
         throw new IllegalStateException("Tiling mode not MODE_EXPLICIT!");
      } else if (!this.tilingSet) {
         throw new IllegalStateException("Tiling parameters not set!");
      } else {
         return this.tileGridYOffset;
      }
   }

   public boolean canWriteProgressive() {
      return this.canWriteProgressive;
   }

   public void setProgressiveMode(int var1) {
      if (!this.canWriteProgressive()) {
         throw new UnsupportedOperationException("Progressive output not supported");
      } else if (var1 >= 0 && var1 <= 3) {
         if (var1 == 2) {
            throw new IllegalArgumentException("MODE_EXPLICIT not supported for progressive output");
         } else {
            this.progressiveMode = var1;
         }
      } else {
         throw new IllegalArgumentException("Illegal value for mode!");
      }
   }

   public int getProgressiveMode() {
      if (!this.canWriteProgressive()) {
         throw new UnsupportedOperationException("Progressive output not supported");
      } else {
         return this.progressiveMode;
      }
   }

   public boolean canWriteCompressed() {
      return this.canWriteCompressed;
   }

   public void setCompressionMode(int var1) {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (var1 >= 0 && var1 <= 3) {
         this.compressionMode = var1;
         if (var1 == 2) {
            this.unsetCompression();
         }

      } else {
         throw new IllegalArgumentException("Illegal value for mode!");
      }
   }

   public int getCompressionMode() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else {
         return this.compressionMode;
      }
   }

   public String[] getCompressionTypes() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported");
      } else {
         return this.compressionTypes == null ? null : (String[])((String[])this.compressionTypes.clone());
      }
   }

   public void setCompressionType(String var1) {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         String[] var2 = this.getCompressionTypes();
         if (var2 == null) {
            throw new UnsupportedOperationException("No settable compression types");
         } else {
            if (var1 != null) {
               boolean var3 = false;
               if (var2 != null) {
                  for(int var4 = 0; var4 < var2.length; ++var4) {
                     if (var1.equals(var2[var4])) {
                        var3 = true;
                        break;
                     }
                  }
               }

               if (!var3) {
                  throw new IllegalArgumentException("Unknown compression type!");
               }
            }

            this.compressionType = var1;
         }
      }
   }

   public String getCompressionType() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         return this.compressionType;
      }
   }

   public void unsetCompression() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else {
         this.compressionType = null;
         this.compressionQuality = 1.0F;
      }
   }

   public String getLocalizedCompressionTypeName() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return this.getCompressionType();
      }
   }

   public boolean isCompressionLossless() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return true;
      }
   }

   public void setCompressionQuality(float var1) {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else if (var1 >= 0.0F && var1 <= 1.0F) {
         this.compressionQuality = var1;
      } else {
         throw new IllegalArgumentException("Quality out-of-bounds!");
      }
   }

   public float getCompressionQuality() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return this.compressionQuality;
      }
   }

   public float getBitRate(float var1) {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else if (var1 >= 0.0F && var1 <= 1.0F) {
         return -1.0F;
      } else {
         throw new IllegalArgumentException("Quality out-of-bounds!");
      }
   }

   public String[] getCompressionQualityDescriptions() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return null;
      }
   }

   public float[] getCompressionQualityValues() {
      if (!this.canWriteCompressed()) {
         throw new UnsupportedOperationException("Compression not supported.");
      } else if (this.getCompressionMode() != 2) {
         throw new IllegalStateException("Compression mode not MODE_EXPLICIT!");
      } else if (this.getCompressionTypes() != null && this.getCompressionType() == null) {
         throw new IllegalStateException("No compression type set!");
      } else {
         return null;
      }
   }
}
