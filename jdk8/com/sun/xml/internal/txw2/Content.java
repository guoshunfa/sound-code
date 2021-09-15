package com.sun.xml.internal.txw2;

abstract class Content {
   private Content next;

   final Content getNext() {
      return this.next;
   }

   final void setNext(Document doc, Content next) {
      assert next != null;

      assert this.next == null : "next of " + this + " is already set to " + this.next;

      this.next = next;
      doc.run();
   }

   boolean isReadyToCommit() {
      return true;
   }

   abstract boolean concludesPendingStartTag();

   abstract void accept(ContentVisitor var1);

   public void written() {
   }
}
