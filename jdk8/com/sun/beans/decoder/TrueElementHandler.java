package com.sun.beans.decoder;

final class TrueElementHandler extends NullElementHandler {
   public Object getValue() {
      return Boolean.TRUE;
   }
}
