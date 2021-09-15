package com.sun.xml.internal.txw2;

abstract class Text extends Content {
   protected final StringBuilder buffer = new StringBuilder();

   protected Text(Document document, NamespaceResolver nsResolver, Object obj) {
      document.writeValue(obj, nsResolver, this.buffer);
   }

   boolean concludesPendingStartTag() {
      return false;
   }
}
