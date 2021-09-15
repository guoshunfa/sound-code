package com.sun.beans.decoder;

final class ValueObjectImpl implements ValueObject {
   static final ValueObject NULL = new ValueObjectImpl((Object)null);
   static final ValueObject VOID = new ValueObjectImpl();
   private Object value;
   private boolean isVoid;

   static ValueObject create(Object var0) {
      return (ValueObject)(var0 != null ? new ValueObjectImpl(var0) : NULL);
   }

   private ValueObjectImpl() {
      this.isVoid = true;
   }

   private ValueObjectImpl(Object var1) {
      this.value = var1;
   }

   public Object getValue() {
      return this.value;
   }

   public boolean isVoid() {
      return this.isVoid;
   }
}
