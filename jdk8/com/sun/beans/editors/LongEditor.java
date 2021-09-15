package com.sun.beans.editors;

public class LongEditor extends NumberEditor {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      return var1 != null ? var1 + "L" : "null";
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Long.decode(var1));
   }
}
