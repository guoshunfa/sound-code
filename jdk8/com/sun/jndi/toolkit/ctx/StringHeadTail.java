package com.sun.jndi.toolkit.ctx;

public class StringHeadTail {
   private int status;
   private String head;
   private String tail;

   public StringHeadTail(String var1, String var2) {
      this(var1, var2, 0);
   }

   public StringHeadTail(String var1, String var2, int var3) {
      this.status = var3;
      this.head = var1;
      this.tail = var2;
   }

   public void setStatus(int var1) {
      this.status = var1;
   }

   public String getHead() {
      return this.head;
   }

   public String getTail() {
      return this.tail;
   }

   public int getStatus() {
      return this.status;
   }
}
