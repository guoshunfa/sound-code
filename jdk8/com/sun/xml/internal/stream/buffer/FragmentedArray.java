package com.sun.xml.internal.stream.buffer;

final class FragmentedArray<T> {
   private T _item;
   private FragmentedArray<T> _next;
   private FragmentedArray<T> _previous;

   FragmentedArray(T item) {
      this(item, (FragmentedArray)null);
   }

   FragmentedArray(T item, FragmentedArray<T> previous) {
      this.setArray(item);
      if (previous != null) {
         previous._next = this;
         this._previous = previous;
      }

   }

   T getArray() {
      return this._item;
   }

   void setArray(T item) {
      assert item.getClass().isArray();

      this._item = item;
   }

   FragmentedArray<T> getNext() {
      return this._next;
   }

   void setNext(FragmentedArray<T> next) {
      this._next = next;
      if (next != null) {
         next._previous = this;
      }

   }

   FragmentedArray<T> getPrevious() {
      return this._previous;
   }

   void setPrevious(FragmentedArray<T> previous) {
      this._previous = previous;
      if (previous != null) {
         previous._next = this;
      }

   }
}
