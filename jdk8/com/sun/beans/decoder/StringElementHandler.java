package com.sun.beans.decoder;

public class StringElementHandler extends ElementHandler {
   private StringBuilder sb = new StringBuilder();
   private ValueObject value;

   public StringElementHandler() {
      this.value = ValueObjectImpl.NULL;
   }

   public final void addCharacter(char var1) {
      if (this.sb == null) {
         throw new IllegalStateException("Could not add chararcter to evaluated string element");
      } else {
         this.sb.append(var1);
      }
   }

   protected final void addArgument(Object var1) {
      if (this.sb == null) {
         throw new IllegalStateException("Could not add argument to evaluated string element");
      } else {
         this.sb.append(var1);
      }
   }

   protected final ValueObject getValueObject() {
      if (this.sb != null) {
         try {
            this.value = ValueObjectImpl.create(this.getValue(this.sb.toString()));
         } catch (RuntimeException var5) {
            this.getOwner().handleException(var5);
         } finally {
            this.sb = null;
         }
      }

      return this.value;
   }

   protected Object getValue(String var1) {
      return var1;
   }
}
