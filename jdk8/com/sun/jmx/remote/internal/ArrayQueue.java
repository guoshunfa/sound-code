package com.sun.jmx.remote.internal;

import java.util.AbstractList;

public class ArrayQueue<T> extends AbstractList<T> {
   private int capacity;
   private T[] queue;
   private int head;
   private int tail;

   public ArrayQueue(int var1) {
      this.capacity = var1 + 1;
      this.queue = this.newArray(var1 + 1);
      this.head = 0;
      this.tail = 0;
   }

   public void resize(int var1) {
      int var2 = this.size();
      if (var1 < var2) {
         throw new IndexOutOfBoundsException("Resizing would lose data");
      } else {
         ++var1;
         if (var1 != this.capacity) {
            Object[] var3 = this.newArray(var1);

            for(int var4 = 0; var4 < var2; ++var4) {
               var3[var4] = this.get(var4);
            }

            this.capacity = var1;
            this.queue = var3;
            this.head = 0;
            this.tail = var2;
         }
      }
   }

   private T[] newArray(int var1) {
      return (Object[])(new Object[var1]);
   }

   public boolean add(T var1) {
      this.queue[this.tail] = var1;
      int var2 = (this.tail + 1) % this.capacity;
      if (var2 == this.head) {
         throw new IndexOutOfBoundsException("Queue full");
      } else {
         this.tail = var2;
         return true;
      }
   }

   public T remove(int var1) {
      if (var1 != 0) {
         throw new IllegalArgumentException("Can only remove head of queue");
      } else if (this.head == this.tail) {
         throw new IndexOutOfBoundsException("Queue empty");
      } else {
         Object var2 = this.queue[this.head];
         this.queue[this.head] = null;
         this.head = (this.head + 1) % this.capacity;
         return var2;
      }
   }

   public T get(int var1) {
      int var2 = this.size();
      if (var1 >= 0 && var1 < var2) {
         int var4 = (this.head + var1) % this.capacity;
         return this.queue[var4];
      } else {
         String var3 = "Index " + var1 + ", queue size " + var2;
         throw new IndexOutOfBoundsException(var3);
      }
   }

   public int size() {
      int var1 = this.tail - this.head;
      if (var1 < 0) {
         var1 += this.capacity;
      }

      return var1;
   }
}
