package com.sun.xml.internal.txw2;

final class StartDocument extends Content {
   boolean concludesPendingStartTag() {
      return true;
   }

   void accept(ContentVisitor visitor) {
      visitor.onStartDocument();
   }
}
