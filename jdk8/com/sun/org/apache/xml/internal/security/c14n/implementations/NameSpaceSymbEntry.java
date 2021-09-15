package com.sun.org.apache.xml.internal.security.c14n.implementations;

import org.w3c.dom.Attr;

class NameSpaceSymbEntry implements Cloneable {
   String prefix;
   String uri;
   String lastrendered = null;
   boolean rendered = false;
   Attr n;

   NameSpaceSymbEntry(String var1, Attr var2, boolean var3, String var4) {
      this.uri = var1;
      this.rendered = var3;
      this.n = var2;
      this.prefix = var4;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         return null;
      }
   }
}
