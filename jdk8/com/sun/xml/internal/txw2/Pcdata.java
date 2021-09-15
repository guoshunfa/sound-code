package com.sun.xml.internal.txw2;

final class Pcdata extends Text {
   Pcdata(Document document, NamespaceResolver nsResolver, Object obj) {
      super(document, nsResolver, obj);
   }

   void accept(ContentVisitor visitor) {
      visitor.onPcdata(this.buffer);
   }
}
