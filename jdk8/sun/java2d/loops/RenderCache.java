package sun.java2d.loops;

public final class RenderCache {
   private RenderCache.Entry[] entries;

   public RenderCache(int var1) {
      this.entries = new RenderCache.Entry[var1];
   }

   public synchronized Object get(SurfaceType var1, CompositeType var2, SurfaceType var3) {
      int var4 = this.entries.length - 1;

      for(int var5 = var4; var5 >= 0; --var5) {
         RenderCache.Entry var6 = this.entries[var5];
         if (var6 == null) {
            break;
         }

         if (var6.matches(var1, var2, var3)) {
            if (var5 < var4 - 4) {
               System.arraycopy(this.entries, var5 + 1, this.entries, var5, var4 - var5);
               this.entries[var4] = var6;
            }

            return var6.getValue();
         }
      }

      return null;
   }

   public synchronized void put(SurfaceType var1, CompositeType var2, SurfaceType var3, Object var4) {
      RenderCache.Entry var5 = new RenderCache.Entry(var1, var2, var3, var4);
      int var6 = this.entries.length;
      System.arraycopy(this.entries, 1, this.entries, 0, var6 - 1);
      this.entries[var6 - 1] = var5;
   }

   final class Entry {
      private SurfaceType src;
      private CompositeType comp;
      private SurfaceType dst;
      private Object value;

      public Entry(SurfaceType var2, CompositeType var3, SurfaceType var4, Object var5) {
         this.src = var2;
         this.comp = var3;
         this.dst = var4;
         this.value = var5;
      }

      public boolean matches(SurfaceType var1, CompositeType var2, SurfaceType var3) {
         return this.src == var1 && this.comp == var2 && this.dst == var3;
      }

      public Object getValue() {
         return this.value;
      }
   }
}
