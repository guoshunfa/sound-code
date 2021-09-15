package sun.misc;

public abstract class LRUCache<N, V> {
   private V[] oa = null;
   private final int size;

   public LRUCache(int var1) {
      this.size = var1;
   }

   protected abstract V create(N var1);

   protected abstract boolean hasName(V var1, N var2);

   public static void moveToFront(Object[] var0, int var1) {
      Object var2 = var0[var1];

      for(int var3 = var1; var3 > 0; --var3) {
         var0[var3] = var0[var3 - 1];
      }

      var0[0] = var2;
   }

   public V forName(N var1) {
      if (this.oa == null) {
         Object[] var2 = (Object[])(new Object[this.size]);
         this.oa = var2;
      } else {
         for(int var4 = 0; var4 < this.oa.length; ++var4) {
            Object var3 = this.oa[var4];
            if (var3 != null && this.hasName(var3, var1)) {
               if (var4 > 0) {
                  moveToFront(this.oa, var4);
               }

               return var3;
            }
         }
      }

      Object var5 = this.create(var1);
      this.oa[this.oa.length - 1] = var5;
      moveToFront(this.oa, this.oa.length - 1);
      return var5;
   }
}
