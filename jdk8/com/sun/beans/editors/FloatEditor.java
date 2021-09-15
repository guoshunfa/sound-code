package com.sun.beans.editors;

public class FloatEditor extends NumberEditor {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      return var1 != null ? var1 + "F" : "null";
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Float.valueOf(var1));
   }
}
