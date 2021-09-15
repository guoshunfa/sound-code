package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public abstract class MultiValuedNodeHeapIterator extends DTMAxisIteratorBase {
   private static final int InitSize = 8;
   private int _heapSize = 0;
   private int _size = 8;
   private MultiValuedNodeHeapIterator.HeapNode[] _heap = new MultiValuedNodeHeapIterator.HeapNode[8];
   private int _free = 0;
   private int _returnedLast;
   private int _cachedReturnedLast = -1;
   private int _cachedHeapSize;

   public DTMAxisIterator cloneIterator() {
      this._isRestartable = false;
      MultiValuedNodeHeapIterator.HeapNode[] heapCopy = new MultiValuedNodeHeapIterator.HeapNode[this._heap.length];

      try {
         MultiValuedNodeHeapIterator clone = (MultiValuedNodeHeapIterator)super.clone();

         for(int i = 0; i < this._free; ++i) {
            heapCopy[i] = this._heap[i].cloneHeapNode();
         }

         clone.setRestartable(false);
         clone._heap = heapCopy;
         return clone.reset();
      } catch (CloneNotSupportedException var4) {
         BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", (Object)var4.toString());
         return null;
      }
   }

   protected void addHeapNode(MultiValuedNodeHeapIterator.HeapNode node) {
      if (this._free == this._size) {
         MultiValuedNodeHeapIterator.HeapNode[] newArray = new MultiValuedNodeHeapIterator.HeapNode[this._size *= 2];
         System.arraycopy(this._heap, 0, newArray, 0, this._free);
         this._heap = newArray;
      }

      ++this._heapSize;
      this._heap[this._free++] = node;
   }

   public int next() {
      for(; this._heapSize > 0; this.heapify(0)) {
         int smallest = this._heap[0]._node;
         if (smallest == -1) {
            if (this._heapSize <= 1) {
               return -1;
            }

            MultiValuedNodeHeapIterator.HeapNode temp = this._heap[0];
            this._heap[0] = this._heap[--this._heapSize];
            this._heap[this._heapSize] = temp;
         } else {
            if (smallest != this._returnedLast) {
               this._heap[0].step();
               this.heapify(0);
               return this.returnNode(this._returnedLast = smallest);
            }

            this._heap[0].step();
         }
      }

      return -1;
   }

   public DTMAxisIterator setStartNode(int node) {
      if (!this._isRestartable) {
         return this;
      } else {
         this._startNode = node;

         int i;
         for(i = 0; i < this._free; ++i) {
            if (!this._heap[i]._isStartSet) {
               this._heap[i].setStartNode(node);
               this._heap[i].step();
               this._heap[i]._isStartSet = true;
            }
         }

         for(i = (this._heapSize = this._free) / 2; i >= 0; --i) {
            this.heapify(i);
         }

         this._returnedLast = -1;
         return this.resetPosition();
      }
   }

   protected void init() {
      for(int i = 0; i < this._free; ++i) {
         this._heap[i] = null;
      }

      this._heapSize = 0;
      this._free = 0;
   }

   private void heapify(int i) {
      while(true) {
         int r = i + 1 << 1;
         int l = r - 1;
         int smallest = l < this._heapSize && this._heap[l].isLessThan(this._heap[i]) ? l : i;
         if (r < this._heapSize && this._heap[r].isLessThan(this._heap[smallest])) {
            smallest = r;
         }

         if (smallest == i) {
            return;
         }

         MultiValuedNodeHeapIterator.HeapNode temp = this._heap[smallest];
         this._heap[smallest] = this._heap[i];
         this._heap[i] = temp;
         i = smallest;
      }
   }

   public void setMark() {
      for(int i = 0; i < this._free; ++i) {
         this._heap[i].setMark();
      }

      this._cachedReturnedLast = this._returnedLast;
      this._cachedHeapSize = this._heapSize;
   }

   public void gotoMark() {
      int i;
      for(i = 0; i < this._free; ++i) {
         this._heap[i].gotoMark();
      }

      for(i = (this._heapSize = this._cachedHeapSize) / 2; i >= 0; --i) {
         this.heapify(i);
      }

      this._returnedLast = this._cachedReturnedLast;
   }

   public DTMAxisIterator reset() {
      int i;
      for(i = 0; i < this._free; ++i) {
         this._heap[i].reset();
         this._heap[i].step();
      }

      for(i = (this._heapSize = this._free) / 2; i >= 0; --i) {
         this.heapify(i);
      }

      this._returnedLast = -1;
      return this.resetPosition();
   }

   public abstract class HeapNode implements Cloneable {
      protected int _node;
      protected int _markedNode;
      protected boolean _isStartSet = false;

      public abstract int step();

      public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
         MultiValuedNodeHeapIterator.HeapNode clone;
         try {
            clone = (MultiValuedNodeHeapIterator.HeapNode)super.clone();
         } catch (CloneNotSupportedException var3) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", (Object)var3.toString());
            return null;
         }

         clone._node = this._node;
         clone._markedNode = this._node;
         return clone;
      }

      public void setMark() {
         this._markedNode = this._node;
      }

      public void gotoMark() {
         this._node = this._markedNode;
      }

      public abstract boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode var1);

      public abstract MultiValuedNodeHeapIterator.HeapNode setStartNode(int var1);

      public abstract MultiValuedNodeHeapIterator.HeapNode reset();
   }
}
