package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class UnionIterator extends MultiValuedNodeHeapIterator {
   private final DOM _dom;

   public UnionIterator(DOM dom) {
      this._dom = dom;
   }

   public UnionIterator addIterator(DTMAxisIterator iterator) {
      this.addHeapNode(new UnionIterator.LookAheadIterator(iterator));
      return this;
   }

   private final class LookAheadIterator extends MultiValuedNodeHeapIterator.HeapNode {
      public DTMAxisIterator iterator;

      public LookAheadIterator(DTMAxisIterator iterator) {
         super();
         this.iterator = iterator;
      }

      public int step() {
         this._node = this.iterator.next();
         return this._node;
      }

      public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
         UnionIterator.LookAheadIterator clone = (UnionIterator.LookAheadIterator)super.cloneHeapNode();
         clone.iterator = this.iterator.cloneIterator();
         return clone;
      }

      public void setMark() {
         super.setMark();
         this.iterator.setMark();
      }

      public void gotoMark() {
         super.gotoMark();
         this.iterator.gotoMark();
      }

      public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode heapNode) {
         UnionIterator.LookAheadIterator comparand = (UnionIterator.LookAheadIterator)heapNode;
         return UnionIterator.this._dom.lessThan(this._node, heapNode._node);
      }

      public MultiValuedNodeHeapIterator.HeapNode setStartNode(int node) {
         this.iterator.setStartNode(node);
         return this;
      }

      public MultiValuedNodeHeapIterator.HeapNode reset() {
         this.iterator.reset();
         return this;
      }
   }
}
