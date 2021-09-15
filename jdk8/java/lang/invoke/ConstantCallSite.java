package java.lang.invoke;

public class ConstantCallSite extends CallSite {
   private final boolean isFrozen = true;

   public ConstantCallSite(MethodHandle var1) {
      super(var1);
   }

   protected ConstantCallSite(MethodType var1, MethodHandle var2) throws Throwable {
      super(var1, var2);
   }

   public final MethodHandle getTarget() {
      if (!this.isFrozen) {
         throw new IllegalStateException();
      } else {
         return this.target;
      }
   }

   public final void setTarget(MethodHandle var1) {
      throw new UnsupportedOperationException();
   }

   public final MethodHandle dynamicInvoker() {
      return this.getTarget();
   }
}
