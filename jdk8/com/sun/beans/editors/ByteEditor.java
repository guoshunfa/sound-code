package com.sun.beans.editors;

public class ByteEditor extends NumberEditor {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      return var1 != null ? "((byte)" + var1 + ")" : "null";
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      this.setValue(var1 == null ? null : Byte.decode(var1));
   }
}
