package com.sun.beans.editors;

public class IntegerEditor extends NumberEditor {
   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Integer.decode(var1));
   }
}
