package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class BooleanEditor extends PropertyEditorSupport {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      return var1 != null ? var1.toString() : "null";
   }

   public String getAsText() {
      Object var1 = this.getValue();
      return var1 instanceof Boolean ? this.getValidName((Boolean)var1) : null;
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         this.setValue((Object)null);
      } else if (this.isValidName(true, var1)) {
         this.setValue(Boolean.TRUE);
      } else {
         if (!this.isValidName(false, var1)) {
            throw new IllegalArgumentException(var1);
         }

         this.setValue(Boolean.FALSE);
      }

   }

   public String[] getTags() {
      return new String[]{this.getValidName(true), this.getValidName(false)};
   }

   private String getValidName(boolean var1) {
      return var1 ? "True" : "False";
   }

   private boolean isValidName(boolean var1, String var2) {
      return this.getValidName(var1).equalsIgnoreCase(var2);
   }
}
