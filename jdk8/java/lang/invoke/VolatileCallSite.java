package java.lang.invoke;

public class VolatileCallSite extends CallSite {
   public VolatileCallSite(MethodType var1) {
      super(var1);
   }

   public VolatileCallSite(MethodHandle var1) {
      super(var1);
   }

   public final MethodHandle getTarget() {
      return this.getTargetVolatile();
   }

   public void setTarget(MethodHandle var1) {
      this.checkTargetChange(this.getTargetVolatile(), var1);
      this.setTargetVolatile(var1);
   }

   public final MethodHandle dynamicInvoker() {
      return this.makeDynamicInvoker();
   }
}
