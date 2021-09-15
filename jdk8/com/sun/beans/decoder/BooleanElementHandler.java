package com.sun.beans.decoder;

final class BooleanElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      if (Boolean.TRUE.toString().equalsIgnoreCase(var1)) {
         return Boolean.TRUE;
      } else if (Boolean.FALSE.toString().equalsIgnoreCase(var1)) {
         return Boolean.FALSE;
      } else {
         throw new IllegalArgumentException("Unsupported boolean argument: " + var1);
      }
   }
}
