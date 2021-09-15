package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;

public class HeadTail {
   private int status;
   private Name head;
   private Name tail;

   public HeadTail(Name var1, Name var2) {
      this(var1, var2, 0);
   }

   public HeadTail(Name var1, Name var2, int var3) {
      this.status = var3;
      this.head = var1;
      this.tail = var2;
   }

   public void setStatus(int var1) {
      this.status = var1;
   }

   public Name getHead() {
      return this.head;
   }

   public Name getTail() {
      return this.tail;
   }

   public int getStatus() {
      return this.status;
   }
}
