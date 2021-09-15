package sun.java2d.jules;

import java.util.ArrayList;
import java.util.LinkedList;

public class TileWorker implements Runnable {
   static final int RASTERIZED_TILE_SYNC_GRANULARITY = 8;
   final ArrayList<JulesTile> rasterizedTileConsumerCache = new ArrayList();
   final LinkedList<JulesTile> rasterizedBuffers = new LinkedList();
   IdleTileCache tileCache;
   JulesAATileGenerator tileGenerator;
   int workerStartIndex;
   volatile int consumerPos = 0;
   int mainThreadCnt = 0;
   int workerCnt = 0;
   int doubled = 0;

   public TileWorker(JulesAATileGenerator var1, int var2, IdleTileCache var3) {
      this.tileGenerator = var1;
      this.workerStartIndex = var2;
      this.tileCache = var3;
   }

   public void run() {
      ArrayList var1 = new ArrayList(16);

      for(int var2 = this.workerStartIndex; var2 < this.tileGenerator.getTileCount(); ++var2) {
         TileTrapContainer var3 = this.tileGenerator.getTrapContainer(var2);
         if (var3 != null && var3.getTileAlpha() == 127) {
            JulesTile var4 = this.tileGenerator.rasterizeTile(var2, this.tileCache.getIdleTileWorker(this.tileGenerator.getTileCount() - var2 - 1));
            var1.add(var4);
            if (var1.size() > 8) {
               this.addRasterizedTiles(var1);
               var1.clear();
            }
         }

         var2 = Math.max(var2, this.consumerPos + 4);
      }

      this.addRasterizedTiles(var1);
      this.tileCache.disposeRasterizerResources();
   }

   public JulesTile getPreRasterizedTile(int var1) {
      JulesTile var2 = null;
      if (this.rasterizedTileConsumerCache.size() == 0 && var1 >= this.workerStartIndex) {
         synchronized(this.rasterizedBuffers) {
            this.rasterizedTileConsumerCache.addAll(this.rasterizedBuffers);
            this.rasterizedBuffers.clear();
         }
      }

      while(var2 == null && this.rasterizedTileConsumerCache.size() > 0) {
         JulesTile var3 = (JulesTile)this.rasterizedTileConsumerCache.get(0);
         if (var3.getTilePos() > var1) {
            break;
         }

         if (var3.getTilePos() < var1) {
            this.tileCache.releaseTile(var3);
            ++this.doubled;
         }

         if (var3.getTilePos() <= var1) {
            this.rasterizedTileConsumerCache.remove(0);
         }

         if (var3.getTilePos() == var1) {
            var2 = var3;
         }
      }

      if (var2 == null) {
         ++this.mainThreadCnt;
         this.consumerPos = var1;
      } else {
         ++this.workerCnt;
      }

      return var2;
   }

   private void addRasterizedTiles(ArrayList<JulesTile> var1) {
      synchronized(this.rasterizedBuffers) {
         this.rasterizedBuffers.addAll(var1);
      }
   }

   public void disposeConsumerResources() {
      synchronized(this.rasterizedBuffers) {
         this.tileCache.releaseTiles(this.rasterizedBuffers);
      }

      this.tileCache.releaseTiles(this.rasterizedTileConsumerCache);
   }
}
