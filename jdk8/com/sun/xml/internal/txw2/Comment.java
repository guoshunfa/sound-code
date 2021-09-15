package com.sun.xml.internal.txw2;

final class Comment extends Content {
   private final StringBuilder buffer = new StringBuilder();

   public Comment(Document document, NamespaceResolver nsResolver, Object obj) {
      document.writeValue(obj, nsResolver, this.buffer);
   }

   boolean concludesPendingStartTag() {
      return false;
   }

   void accept(ContentVisitor visitor) {
      visitor.onComment(this.buffer);
   }
}
