package sun.java2d.jules;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sun.java2d.pipe.AATileGenerator;
import sun.java2d.pipe.Region;
import sun.java2d.xr.GrowableIntArray;
import sun.java2d.xr.XRUtils;

public class JulesAATileGenerator implements AATileGenerator {
   static final ExecutorService rasterThreadPool = Executors.newCachedThreadPool();
   static final int CPU_CNT = Runtime.getRuntime().availableProcessors();
   static final boolean ENABLE_THREADING = false;
   static final int THREAD_MIN = 16;
   static final int THREAD_BEGIN = 16;
   IdleTileCache tileCache;
   TileWorker worker;
   boolean threaded = false;
   int rasterTileCnt;
   static final int TILE_SIZE = 32;
   static final int TILE_SIZE_FP = 2097152;
   int left;
   int right;
   int top;
   int bottom;
   int width;
   int height;
   int leftFP;
   int topFP;
   int tileCnt;
   int tilesX;
   int tilesY;
   int currTilePos = 0;
   TrapezoidList traps;
   TileTrapContainer[] tiledTrapArray;
   JulesTile mainTile;

   public JulesAATileGenerator(Shape var1, AffineTransform var2, Region var3, BasicStroke var4, boolean var5, boolean var6, int[] var7) {
      JulesPathBuf var8 = new JulesPathBuf();
      if (var4 == null) {
         this.traps = var8.tesselateFill(var1, var2, var3);
      } else {
         this.traps = var8.tesselateStroke(var1, var4, var5, false, true, var2, var3);
      }

      this.calculateArea(var7);
      this.bucketSortTraps();
      this.calculateTypicalAlpha();
      this.threaded = false;
      if (this.threaded) {
         this.tileCache = new IdleTileCache();
         this.worker = new TileWorker(this, 16, this.tileCache);
         rasterThreadPool.execute(this.worker);
      }

      this.mainTile = new JulesTile();
   }

   private static native long rasterizeTrapezoidsNative(long var0, int[] var2, int[] var3, int var4, byte[] var5, int var6, int var7);

   private static native void freePixmanImgPtr(long var0);

   private void calculateArea(int[] var1) {
      this.tilesX = 0;
      this.tilesY = 0;
      this.tileCnt = 0;
      var1[0] = 0;
      var1[1] = 0;
      var1[2] = 0;
      var1[3] = 0;
      if (this.traps.getSize() > 0) {
         this.left = this.traps.getLeft();
         this.right = this.traps.getRight();
         this.top = this.traps.getTop();
         this.bottom = this.traps.getBottom();
         this.leftFP = this.left << 16;
         this.topFP = this.top << 16;
         var1[0] = this.left;
         var1[1] = this.top;
         var1[2] = this.right;
         var1[3] = this.bottom;
         this.width = this.right - this.left;
         this.height = this.bottom - this.top;
         if (this.width > 0 && this.height > 0) {
            this.tilesX = (int)Math.ceil((double)this.width / 32.0D);
            this.tilesY = (int)Math.ceil((double)this.height / 32.0D);
            this.tileCnt = this.tilesY * this.tilesX;
            this.tiledTrapArray = new TileTrapContainer[this.tileCnt];
         } else {
            this.traps.setSize(0);
         }
      }

   }

   private void bucketSortTraps() {
      for(int var1 = 0; var1 < this.traps.getSize(); ++var1) {
         int var2 = this.traps.getTop(var1) - XRUtils.XDoubleToFixed((double)this.top);
         int var3 = this.traps.getBottom(var1) - this.topFP;
         int var4 = this.traps.getP1XLeft(var1) - this.leftFP;
         int var5 = this.traps.getP2XLeft(var1) - this.leftFP;
         int var6 = this.traps.getP1XRight(var1) - this.leftFP;
         int var7 = this.traps.getP2XRight(var1) - this.leftFP;
         int var8 = Math.min(var4, var5);
         int var9 = Math.max(var6, var7);
         var9 = var9 > 0 ? var9 - 1 : var9;
         var3 = var3 > 0 ? var3 - 1 : var3;
         int var10 = var2 / 2097152;
         int var11 = var3 / 2097152;
         int var12 = var8 / 2097152;
         int var13 = var9 / 2097152;

         for(int var14 = var10; var14 <= var11; ++var14) {
            for(int var15 = var12; var15 <= var13; ++var15) {
               int var16 = var14 * this.tilesX + var15;
               TileTrapContainer var17 = this.tiledTrapArray[var16];
               if (var17 == null) {
                  var17 = new TileTrapContainer(new GrowableIntArray(1, 16));
                  this.tiledTrapArray[var16] = var17;
               }

               var17.getTraps().addInt(var1);
            }
         }
      }

   }

   public void getAlpha(byte[] var1, int var2, int var3) {
      JulesTile var4 = null;
      if (this.threaded) {
         var4 = this.worker.getPreRasterizedTile(this.currTilePos);
      }

      if (var4 != null) {
         System.arraycopy(var4.getImgBuffer(), 0, var1, 0, var1.length);
         this.tileCache.releaseTile(var4);
      } else {
         this.mainTile.setImgBuffer(var1);
         this.rasterizeTile(this.currTilePos, this.mainTile);
      }

      this.nextTile();
   }

   public void calculateTypicalAlpha() {
      this.rasterTileCnt = 0;

      for(int var1 = 0; var1 < this.tileCnt; ++var1) {
         TileTrapContainer var2 = this.tiledTrapArray[var1];
         if (var2 != null) {
            GrowableIntArray var3 = var2.getTraps();
            short var4 = 127;
            if (var3 != null && var3.getSize() != 0) {
               if (this.doTrapsCoverTile(var3, var1)) {
                  var4 = 255;
               }
            } else {
               var4 = 0;
            }

            if (var4 == 127 || var4 == 255) {
               ++this.rasterTileCnt;
            }

            var2.setTileAlpha(var4);
         }
      }

   }

   protected boolean doTrapsCoverTile(GrowableIntArray var1, int var2) {
      if (var1.getSize() > 32) {
         return false;
      } else {
         int var3 = this.getXPos(var2) * 2097152 + this.leftFP;
         int var4 = this.getYPos(var2) * 2097152 + this.topFP;
         int var5 = var3 + 2097152;
         int var6 = var4 + 2097152;
         int var7 = this.traps.getTop(var1.getInt(0));
         int var8 = this.traps.getBottom(var1.getInt(0));
         if (var7 <= var4 && var8 >= var4) {
            int var9 = var7;

            for(int var10 = 0; var10 < var1.getSize(); ++var10) {
               int var11 = var1.getInt(var10);
               if (this.traps.getP1XLeft(var11) > var3 || this.traps.getP2XLeft(var11) > var3 || this.traps.getP1XRight(var11) < var5 || this.traps.getP2XRight(var11) < var5 || this.traps.getTop(var11) != var9) {
                  return false;
               }

               var9 = this.traps.getBottom(var11);
            }

            return var9 >= var6;
         } else {
            return false;
         }
      }
   }

   public int getTypicalAlpha() {
      return this.tiledTrapArray[this.currTilePos] == null ? 0 : this.tiledTrapArray[this.currTilePos].getTileAlpha();
   }

   public void dispose() {
      freePixmanImgPtr(this.mainTile.getPixmanImgPtr());
      if (this.threaded) {
         this.tileCache.disposeConsumerResources();
         this.worker.disposeConsumerResources();
      }

   }

   protected JulesTile rasterizeTile(int var1, JulesTile var2) {
      int var3 = this.left + this.getXPos(var1) * 32;
      int var4 = this.top + this.getYPos(var1) * 32;
      TileTrapContainer var5 = this.tiledTrapArray[var1];
      GrowableIntArray var6 = var5.getTraps();
      if (var5.getTileAlpha() == 127) {
         long var7 = rasterizeTrapezoidsNative(var2.getPixmanImgPtr(), this.traps.getTrapArray(), var6.getArray(), var6.getSize(), var2.getImgBuffer(), var3, var4);
         var2.setPixmanImgPtr(var7);
      }

      var2.setTilePos(var1);
      return var2;
   }

   protected int getXPos(int var1) {
      return var1 % this.tilesX;
   }

   protected int getYPos(int var1) {
      return var1 / this.tilesX;
   }

   public void nextTile() {
      ++this.currTilePos;
   }

   public int getTileHeight() {
      return 32;
   }

   public int getTileWidth() {
      return 32;
   }

   public int getTileCount() {
      return this.tileCnt;
   }

   public TileTrapContainer getTrapContainer(int var1) {
      return this.tiledTrapArray[var1];
   }
}
