package com.sun.beans.decoder;

final class FloatElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Float.valueOf(var1);
   }
}
