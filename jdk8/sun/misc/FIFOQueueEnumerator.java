package sun.misc;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class FIFOQueueEnumerator<T> implements Enumeration<T> {
   Queue<T> queue;
   QueueElement<T> cursor;

   FIFOQueueEnumerator(Queue<T> var1) {
      this.queue = var1;
      this.cursor = var1.tail;
   }

   public boolean hasMoreElements() {
      return this.cursor != null;
   }

   public T nextElement() {
      synchronized(this.queue) {
         if (this.cursor != null) {
            QueueElement var2 = this.cursor;
            this.cursor = this.cursor.prev;
            return var2.obj;
         }
      }

      throw new NoSuchElementException("FIFOQueueEnumerator");
   }
}
