package java.lang.invoke;

public abstract class CallSite {
   MethodHandle target;
   private static final MethodHandle GET_TARGET;
   private static final MethodHandle THROW_UCS;
   private static final long TARGET_OFFSET;

   CallSite(MethodType var1) {
      this.target = this.makeUninitializedCallSite(var1);
   }

   CallSite(MethodHandle var1) {
      var1.type();
      this.target = var1;
   }

   CallSite(MethodType var1, MethodHandle var2) throws Throwable {
      this(var1);
      ConstantCallSite var3 = (ConstantCallSite)this;
      MethodHandle var4 = (MethodHandle)var2.invokeWithArguments(var3);
      this.checkTargetChange(this.target, var4);
      this.target = var4;
   }

   public MethodType type() {
      return this.target.type();
   }

   public abstract MethodHandle getTarget();

   public abstract void setTarget(MethodHandle var1);

   void checkTargetChange(MethodHandle var1, MethodHandle var2) {
      MethodType var3 = var1.type();
      MethodType var4 = var2.type();
      if (!var4.equals((Object)var3)) {
         throw wrongTargetType(var2, var3);
      }
   }

   private static WrongMethodTypeException wrongTargetType(MethodHandle var0, MethodType var1) {
      return new WrongMethodTypeException(var0 + " should be of type " + var1);
   }

   public abstract MethodHandle dynamicInvoker();

   MethodHandle makeDynamicInvoker() {
      BoundMethodHandle var1 = GET_TARGET.bindArgumentL(0, this);
      MethodHandle var2 = MethodHandles.exactInvoker(this.type());
      return MethodHandles.foldArguments(var2, var1);
   }

   private static Object uninitializedCallSite(Object... var0) {
      throw new IllegalStateException("uninitialized call site");
   }

   private MethodHandle makeUninitializedCallSite(MethodType var1) {
      MethodType var2 = var1.basicType();
      MethodHandle var3 = var2.form().cachedMethodHandle(2);
      if (var3 == null) {
         var3 = THROW_UCS.asType(var2);
         var3 = var2.form().setCachedMethodHandle(2, var3);
      }

      return var3.viewAsType(var1, false);
   }

   void setTargetNormal(MethodHandle var1) {
      MethodHandleNatives.setCallSiteTargetNormal(this, var1);
   }

   MethodHandle getTargetVolatile() {
      return (MethodHandle)MethodHandleStatics.UNSAFE.getObjectVolatile(this, TARGET_OFFSET);
   }

   void setTargetVolatile(MethodHandle var1) {
      MethodHandleNatives.setCallSiteTargetVolatile(this, var1);
   }

   static CallSite makeSite(MethodHandle var0, String var1, MethodType var2, Object var3, Class<?> var4) {
      MethodHandles.Lookup var5 = MethodHandles.Lookup.IMPL_LOOKUP.in(var4);

      try {
         var3 = maybeReBox(var3);
         Object var7;
         if (var3 == null) {
            var7 = var0.invoke(var5, var1, var2);
         } else if (!var3.getClass().isArray()) {
            var7 = var0.invoke(var5, var1, var2, var3);
         } else {
            Object[] var15 = (Object[])((Object[])var3);
            maybeReBoxElements(var15);
            switch(var15.length) {
            case 0:
               var7 = var0.invoke(var5, var1, var2);
               break;
            case 1:
               var7 = var0.invoke(var5, var1, var2, var15[0]);
               break;
            case 2:
               var7 = var0.invoke(var5, var1, var2, var15[0], var15[1]);
               break;
            case 3:
               var7 = var0.invoke(var5, var1, var2, var15[0], var15[1], var15[2]);
               break;
            case 4:
               var7 = var0.invoke(var5, var1, var2, var15[0], var15[1], var15[2], var15[3]);
               break;
            case 5:
               var7 = var0.invoke(var5, var1, var2, var15[0], var15[1], var15[2], var15[3], var15[4]);
               break;
            case 6:
               var7 = var0.invoke(var5, var1, var2, var15[0], var15[1], var15[2], var15[3], var15[4], var15[5]);
               break;
            default:
               if (3 + var15.length > 254) {
                  throw new BootstrapMethodError("too many bootstrap method arguments");
               }

               MethodType var10 = var0.type();
               MethodType var11 = MethodType.genericMethodType(3 + var15.length);
               MethodHandle var12 = var0.asType(var11);
               MethodHandle var13 = var11.invokers().spreadInvoker(3);
               var7 = var13.invokeExact(var12, var5, var1, var2, var15);
            }
         }

         if (var7 instanceof CallSite) {
            CallSite var6 = (CallSite)var7;
            if (!var6.getTarget().type().equals((Object)var2)) {
               throw wrongTargetType(var6.getTarget(), var2);
            } else {
               return var6;
            }
         } else {
            throw new ClassCastException("bootstrap method failed to produce a CallSite");
         }
      } catch (Throwable var14) {
         BootstrapMethodError var8;
         if (var14 instanceof BootstrapMethodError) {
            var8 = (BootstrapMethodError)var14;
         } else {
            var8 = new BootstrapMethodError("call site initialization exception", var14);
         }

         throw var8;
      }
   }

   private static Object maybeReBox(Object var0) {
      if (var0 instanceof Integer) {
         int var1 = (Integer)var0;
         if (var1 == (byte)var1) {
            var0 = var1;
         }
      }

      return var0;
   }

   private static void maybeReBoxElements(Object[] var0) {
      for(int var1 = 0; var1 < var0.length; ++var1) {
         var0[var1] = maybeReBox(var0[var1]);
      }

   }

   static {
      MethodHandleImpl.initStatics();

      try {
         GET_TARGET = MethodHandles.Lookup.IMPL_LOOKUP.findVirtual(CallSite.class, "getTarget", MethodType.methodType(MethodHandle.class));
         THROW_UCS = MethodHandles.Lookup.IMPL_LOOKUP.findStatic(CallSite.class, "uninitializedCallSite", MethodType.methodType(Object.class, Object[].class));
      } catch (ReflectiveOperationException var2) {
         throw MethodHandleStatics.newInternalError((Throwable)var2);
      }

      try {
         TARGET_OFFSET = MethodHandleStatics.UNSAFE.objectFieldOffset(CallSite.class.getDeclaredField("target"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
