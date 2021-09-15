package com.sun.beans.decoder;

final class DoubleElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Double.valueOf(var1);
   }
}
