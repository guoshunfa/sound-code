package com.sun.beans.editors;

public class DoubleEditor extends NumberEditor {
   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Double.valueOf(var1));
   }
}
