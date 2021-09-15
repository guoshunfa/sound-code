package com.sun.corba.se.impl.encoding;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BufferQueue {
   private LinkedList list = new LinkedList();

   public void enqueue(ByteBufferWithInfo var1) {
      this.list.addLast(var1);
   }

   public ByteBufferWithInfo dequeue() throws NoSuchElementException {
      return (ByteBufferWithInfo)this.list.removeFirst();
   }

   public int size() {
      return this.list.size();
   }

   public void push(ByteBufferWithInfo var1) {
      this.list.addFirst(var1);
   }
}
