package com.sun.beans.decoder;

final class VarElementHandler extends ElementHandler {
   private ValueObject value;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("idref")) {
         this.value = ValueObjectImpl.create(this.getVariable(var2));
      } else {
         super.addAttribute(var1, var2);
      }

   }

   protected ValueObject getValueObject() {
      if (this.value == null) {
         throw new IllegalArgumentException("Variable name is not set");
      } else {
         return this.value;
      }
   }
}
