package com.sun.corba.se.impl.orbutil;

import java.util.EmptyStackException;

public class StackImpl {
   private Object[] data = new Object[3];
   private int top = -1;

   public final boolean empty() {
      return this.top == -1;
   }

   public final Object peek() {
      if (this.empty()) {
         throw new EmptyStackException();
      } else {
         return this.data[this.top];
      }
   }

   public final Object pop() {
      Object var1 = this.peek();
      this.data[this.top] = null;
      --this.top;
      return var1;
   }

   private void ensure() {
      if (this.top == this.data.length - 1) {
         int var1 = 2 * this.data.length;
         Object[] var2 = new Object[var1];
         System.arraycopy(this.data, 0, var2, 0, this.data.length);
         this.data = var2;
      }

   }

   public final Object push(Object var1) {
      this.ensure();
      ++this.top;
      this.data[this.top] = var1;
      return var1;
   }
}
