package com.sun.beans.decoder;

import java.lang.reflect.Array;

final class ArrayElementHandler extends NewElementHandler {
   private Integer length;

   public void addAttribute(String var1, String var2) {
      if (var1.equals("length")) {
         this.length = Integer.valueOf(var2);
      } else {
         super.addAttribute(var1, var2);
      }

   }

   public void startElement() {
      if (this.length != null) {
         this.getValueObject();
      }

   }

   protected boolean isArgument() {
      return true;
   }

   protected ValueObject getValueObject(Class<?> var1, Object[] var2) {
      if (var1 == null) {
         var1 = Object.class;
      }

      if (this.length != null) {
         return ValueObjectImpl.create(Array.newInstance(var1, this.length));
      } else {
         Object var3 = Array.newInstance(var1, var2.length);

         for(int var4 = 0; var4 < var2.length; ++var4) {
            Array.set(var3, var4, var2[var4]);
         }

         return ValueObjectImpl.create(var3);
      }
   }
}
