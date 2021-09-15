package sun.misc;

import java.util.Enumeration;

public class Queue<T> {
   int length = 0;
   QueueElement<T> head = null;
   QueueElement<T> tail = null;

   public synchronized void enqueue(T var1) {
      QueueElement var2 = new QueueElement(var1);
      if (this.head == null) {
         this.head = var2;
         this.tail = var2;
         this.length = 1;
      } else {
         var2.next = this.head;
         this.head.prev = var2;
         this.head = var2;
         ++this.length;
      }

      this.notify();
   }

   public T dequeue() throws InterruptedException {
      return this.dequeue(0L);
   }

   public synchronized T dequeue(long var1) throws InterruptedException {
      while(this.tail == null) {
         this.wait(var1);
      }

      QueueElement var3 = this.tail;
      this.tail = var3.prev;
      if (this.tail == null) {
         this.head = null;
      } else {
         this.tail.next = null;
      }

      --this.length;
      return var3.obj;
   }

   public synchronized boolean isEmpty() {
      return this.tail == null;
   }

   public final synchronized Enumeration<T> elements() {
      return new LIFOQueueEnumerator(this);
   }

   public final synchronized Enumeration<T> reverseElements() {
      return new FIFOQueueEnumerator(this);
   }

   public synchronized void dump(String var1) {
      System.err.println(">> " + var1);
      System.err.println("[" + this.length + " elt(s); head = " + (this.head == null ? "null" : this.head.obj + "") + " tail = " + (this.tail == null ? "null" : this.tail.obj + ""));
      QueueElement var2 = this.head;

      QueueElement var3;
      for(var3 = null; var2 != null; var2 = var2.next) {
         System.err.println("  " + var2);
         var3 = var2;
      }

      if (var3 != this.tail) {
         System.err.println("  tail != last: " + this.tail + ", " + var3);
      }

      System.err.println("]");
   }
}
