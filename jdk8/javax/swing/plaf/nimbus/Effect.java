package javax.swing.plaf.nimbus;

import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import sun.awt.AppContext;

abstract class Effect {
   abstract Effect.EffectType getEffectType();

   abstract float getOpacity();

   abstract BufferedImage applyEffect(BufferedImage var1, BufferedImage var2, int var3, int var4);

   protected static Effect.ArrayCache getArrayCache() {
      Effect.ArrayCache var0 = (Effect.ArrayCache)AppContext.getAppContext().get(Effect.ArrayCache.class);
      if (var0 == null) {
         var0 = new Effect.ArrayCache();
         AppContext.getAppContext().put(Effect.ArrayCache.class, var0);
      }

      return var0;
   }

   protected static class ArrayCache {
      private SoftReference<int[]> tmpIntArray = null;
      private SoftReference<byte[]> tmpByteArray1 = null;
      private SoftReference<byte[]> tmpByteArray2 = null;
      private SoftReference<byte[]> tmpByteArray3 = null;

      protected int[] getTmpIntArray(int var1) {
         int[] var2;
         if (this.tmpIntArray == null || (var2 = (int[])this.tmpIntArray.get()) == null || var2.length < var1) {
            var2 = new int[var1];
            this.tmpIntArray = new SoftReference(var2);
         }

         return var2;
      }

      protected byte[] getTmpByteArray1(int var1) {
         byte[] var2;
         if (this.tmpByteArray1 == null || (var2 = (byte[])this.tmpByteArray1.get()) == null || var2.length < var1) {
            var2 = new byte[var1];
            this.tmpByteArray1 = new SoftReference(var2);
         }

         return var2;
      }

      protected byte[] getTmpByteArray2(int var1) {
         byte[] var2;
         if (this.tmpByteArray2 == null || (var2 = (byte[])this.tmpByteArray2.get()) == null || var2.length < var1) {
            var2 = new byte[var1];
            this.tmpByteArray2 = new SoftReference(var2);
         }

         return var2;
      }

      protected byte[] getTmpByteArray3(int var1) {
         byte[] var2;
         if (this.tmpByteArray3 == null || (var2 = (byte[])this.tmpByteArray3.get()) == null || var2.length < var1) {
            var2 = new byte[var1];
            this.tmpByteArray3 = new SoftReference(var2);
         }

         return var2;
      }
   }

   static enum EffectType {
      UNDER,
      BLENDED,
      OVER;
   }
}
