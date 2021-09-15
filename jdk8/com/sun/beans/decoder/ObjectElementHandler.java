package com.sun.beans.decoder;

import java.beans.Expression;
import java.util.Locale;

class ObjectElementHandler extends NewElementHandler {
   private String idref;
   private String field;
   private Integer index;
   private String property;
   private String method;

   public final void addAttribute(String var1, String var2) {
      if (var1.equals("idref")) {
         this.idref = var2;
      } else if (var1.equals("field")) {
         this.field = var2;
      } else if (var1.equals("index")) {
         this.index = Integer.valueOf(var2);
         this.addArgument(this.index);
      } else if (var1.equals("property")) {
         this.property = var2;
      } else if (var1.equals("method")) {
         this.method = var2;
      } else {
         super.addAttribute(var1, var2);
      }

   }

   public final void startElement() {
      if (this.field != null || this.idref != null) {
         this.getValueObject();
      }

   }

   protected boolean isArgument() {
      return true;
   }

   protected final ValueObject getValueObject(Class<?> var1, Object[] var2) throws Exception {
      if (this.field != null) {
         return ValueObjectImpl.create(FieldElementHandler.getFieldValue(this.getContextBean(), this.field));
      } else if (this.idref != null) {
         return ValueObjectImpl.create(this.getVariable(this.idref));
      } else {
         Object var3 = this.getContextBean();
         String var4;
         if (this.index != null) {
            var4 = var2.length == 2 ? "set" : "get";
         } else if (this.property != null) {
            var4 = var2.length == 1 ? "set" : "get";
            if (0 < this.property.length()) {
               var4 = var4 + this.property.substring(0, 1).toUpperCase(Locale.ENGLISH) + this.property.substring(1);
            }
         } else {
            var4 = this.method != null && 0 < this.method.length() ? this.method : "new";
         }

         Expression var5 = new Expression(var3, var4, var2);
         return ValueObjectImpl.create(var5.getValue());
      }
   }
}
