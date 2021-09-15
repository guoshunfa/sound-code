package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeStaticFieldAccessorImpl extends UnsafeFieldAccessorImpl {
   protected final Object base;

   UnsafeStaticFieldAccessorImpl(Field var1) {
      super(var1);
      this.base = unsafe.staticFieldBase(var1);
   }

   static {
      Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, "base");
   }
}
