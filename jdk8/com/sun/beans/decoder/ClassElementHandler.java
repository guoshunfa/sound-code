package com.sun.beans.decoder;

final class ClassElementHandler extends StringElementHandler {
   public Object getValue(String var1) {
      return this.getOwner().findClass(var1);
   }
}
