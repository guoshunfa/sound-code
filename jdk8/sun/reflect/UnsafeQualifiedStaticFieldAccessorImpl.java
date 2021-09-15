package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl extends UnsafeStaticFieldAccessorImpl {
   protected final boolean isReadOnly;

   UnsafeQualifiedStaticFieldAccessorImpl(Field var1, boolean var2) {
      super(var1);
      this.isReadOnly = var2;
   }
}
