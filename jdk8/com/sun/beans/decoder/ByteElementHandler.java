package com.sun.beans.decoder;

final class ByteElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return Byte.decode(var1);
   }
}
