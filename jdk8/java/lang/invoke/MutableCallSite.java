package java.lang.invoke;

import java.util.concurrent.atomic.AtomicInteger;

public class MutableCallSite extends CallSite {
   private static final AtomicInteger STORE_BARRIER = new AtomicInteger();

   public MutableCallSite(MethodType var1) {
      super(var1);
   }

   public MutableCallSite(MethodHandle var1) {
      super(var1);
   }

   public final MethodHandle getTarget() {
      return this.target;
   }

   public void setTarget(MethodHandle var1) {
      this.checkTargetChange(this.target, var1);
      this.setTargetNormal(var1);
   }

   public final MethodHandle dynamicInvoker() {
      return this.makeDynamicInvoker();
   }

   public static void syncAll(MutableCallSite[] var0) {
      if (var0.length != 0) {
         STORE_BARRIER.lazySet(0);

         for(int var1 = 0; var1 < var0.length; ++var1) {
            var0[var1].getClass();
         }

      }
   }
}
