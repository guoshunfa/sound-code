package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

/** @deprecated */
public final class ForwardPositionIterator extends DTMAxisIteratorBase {
   private DTMAxisIterator _source;

   public ForwardPositionIterator(DTMAxisIterator source) {
      this._source = source;
   }

   public DTMAxisIterator cloneIterator() {
      try {
         ForwardPositionIterator clone = (ForwardPositionIterator)super.clone();
         clone._source = this._source.cloneIterator();
         clone._isRestartable = false;
         return clone.reset();
      } catch (CloneNotSupportedException var2) {
         BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", (Object)var2.toString());
         return null;
      }
   }

   public int next() {
      return this.returnNode(this._source.next());
   }

   public DTMAxisIterator setStartNode(int node) {
      this._source.setStartNode(node);
      return this;
   }

   public DTMAxisIterator reset() {
      this._source.reset();
      return this.resetPosition();
   }

   public void setMark() {
      this._source.setMark();
   }

   public void gotoMark() {
      this._source.gotoMark();
   }
}
