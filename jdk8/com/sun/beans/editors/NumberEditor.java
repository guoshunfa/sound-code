package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public abstract class NumberEditor extends PropertyEditorSupport {
   public String getJavaInitializationString() {
      Object var1 = this.getValue();
      return var1 != null ? var1.toString() : "null";
   }
}
