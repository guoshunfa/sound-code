package com.sun.beans.decoder;

final class IntElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Integer.decode(var1);
   }
}
