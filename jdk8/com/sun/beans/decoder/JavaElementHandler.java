package com.sun.beans.decoder;

import java.beans.XMLDecoder;

final class JavaElementHandler extends ElementHandler {
   private Class<?> type;
   private ValueObject value;

   public void addAttribute(String var1, String var2) {
      if (!var1.equals("version")) {
         if (var1.equals("class")) {
            this.type = this.getOwner().findClass(var2);
         } else {
            super.addAttribute(var1, var2);
         }
      }

   }

   protected void addArgument(Object var1) {
      this.getOwner().addObject(var1);
   }

   protected boolean isArgument() {
      return false;
   }

   protected ValueObject getValueObject() {
      if (this.value == null) {
         this.value = ValueObjectImpl.create(this.getValue());
      }

      return this.value;
   }

   private Object getValue() {
      Object var1 = this.getOwner().getOwner();
      if (this.type != null && !this.isValid(var1)) {
         if (var1 instanceof XMLDecoder) {
            XMLDecoder var2 = (XMLDecoder)var1;
            var1 = var2.getOwner();
            if (this.isValid(var1)) {
               return var1;
            }
         }

         throw new IllegalStateException("Unexpected owner class: " + var1.getClass().getName());
      } else {
         return var1;
      }
   }

   private boolean isValid(Object var1) {
      return var1 == null || this.type.isInstance(var1);
   }
}
