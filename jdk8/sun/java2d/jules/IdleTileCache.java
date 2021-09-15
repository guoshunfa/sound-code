package sun.java2d.jules;

import java.util.ArrayList;
import java.util.List;

public class IdleTileCache {
   static final int IDLE_TILE_SYNC_GRANULARITY = 16;
   static final ArrayList<JulesTile> idleBuffers = new ArrayList();
   ArrayList<JulesTile> idleTileWorkerCacheList = new ArrayList();
   ArrayList<JulesTile> idleTileConsumerCacheList = new ArrayList(16);

   public JulesTile getIdleTileWorker(int var1) {
      if (this.idleTileWorkerCacheList.size() == 0) {
         this.idleTileWorkerCacheList.ensureCapacity(var1);
         synchronized(idleBuffers) {
            for(int var3 = 0; var3 < var1 && idleBuffers.size() > 0; ++var3) {
               this.idleTileWorkerCacheList.add(idleBuffers.remove(idleBuffers.size() - 1));
            }
         }
      }

      return this.idleTileWorkerCacheList.size() > 0 ? (JulesTile)this.idleTileWorkerCacheList.remove(this.idleTileWorkerCacheList.size() - 1) : new JulesTile();
   }

   public void releaseTile(JulesTile var1) {
      if (var1 != null && var1.hasBuffer()) {
         this.idleTileConsumerCacheList.add(var1);
         if (this.idleTileConsumerCacheList.size() > 16) {
            synchronized(idleBuffers) {
               idleBuffers.addAll(this.idleTileConsumerCacheList);
            }

            this.idleTileConsumerCacheList.clear();
         }
      }

   }

   public void disposeRasterizerResources() {
      this.releaseTiles(this.idleTileWorkerCacheList);
   }

   public void disposeConsumerResources() {
      this.releaseTiles(this.idleTileConsumerCacheList);
   }

   public void releaseTiles(List<JulesTile> var1) {
      if (var1.size() > 0) {
         synchronized(idleBuffers) {
            idleBuffers.addAll(var1);
         }

         var1.clear();
      }

   }
}
