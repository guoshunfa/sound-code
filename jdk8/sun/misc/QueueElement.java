package sun.misc;

class QueueElement<T> {
   QueueElement<T> next = null;
   QueueElement<T> prev = null;
   T obj = null;

   QueueElement(T var1) {
      this.obj = var1;
   }

   public String toString() {
      return "QueueElement[obj=" + this.obj + (this.prev == null ? " null" : " prev") + (this.next == null ? " null" : " next") + "]";
   }
}
