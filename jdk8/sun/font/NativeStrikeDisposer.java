package sun.font;

class NativeStrikeDisposer extends FontStrikeDisposer {
   long pNativeScalerContext;

   public NativeStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3, int[] var5) {
      super(var1, var2, 0L, var5);
      this.pNativeScalerContext = var3;
   }

   public NativeStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3, long[] var5) {
      super(var1, var2, 0L, var5);
      this.pNativeScalerContext = var3;
   }

   public NativeStrikeDisposer(Font2D var1, FontStrikeDesc var2, long var3) {
      super(var1, var2, 0L);
      this.pNativeScalerContext = var3;
   }

   public NativeStrikeDisposer(Font2D var1, FontStrikeDesc var2) {
      super(var1, var2);
   }

   public synchronized void dispose() {
      if (!this.disposed) {
         if (this.pNativeScalerContext != 0L) {
            this.freeNativeScalerContext(this.pNativeScalerContext);
         }

         super.dispose();
      }

   }

   private native void freeNativeScalerContext(long var1);
}
