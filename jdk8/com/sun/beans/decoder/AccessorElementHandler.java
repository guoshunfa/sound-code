package com.sun.beans.decoder;

abstract class AccessorElementHandler extends ElementHandler {
   private String name;
   private ValueObject value;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("name")) {
         this.name = var2;
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected final void addArgument(Object var1) {
      if (this.value != null) {
         throw new IllegalStateException("Could not add argument to evaluated element");
      } else {
         this.setValue(this.name, var1);
         this.value = ValueObjectImpl.VOID;
      }
   }

   protected final ValueObject getValueObject() {
      if (this.value == null) {
         this.value = ValueObjectImpl.create(this.getValue(this.name));
      }

      return this.value;
   }

   protected abstract Object getValue(String var1);

   protected abstract void setValue(String var1, Object var2);
}
