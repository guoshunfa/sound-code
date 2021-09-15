package com.sun.beans.decoder;

final class ShortElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Short.decode(var1);
   }
}
