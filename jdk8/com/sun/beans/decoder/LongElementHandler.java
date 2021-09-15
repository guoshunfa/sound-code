package com.sun.beans.decoder;

final class LongElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Long.decode(var1);
   }
}
