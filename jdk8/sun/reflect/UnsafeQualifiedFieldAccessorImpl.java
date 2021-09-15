package sun.reflect;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedFieldAccessorImpl extends UnsafeFieldAccessorImpl {
   protected final boolean isReadOnly;

   UnsafeQualifiedFieldAccessorImpl(Field var1, boolean var2) {
      super(var1);
      this.isReadOnly = var2;
   }
}
