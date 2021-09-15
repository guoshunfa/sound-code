package com.sun.beans.decoder;

final class FalseElementHandler extends NullElementHandler {
   public Object getValue() {
      return Boolean.FALSE;
   }
}
