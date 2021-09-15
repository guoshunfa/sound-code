package com.sun.jmx.snmp.agent;

final class LongList {
   public static int DEFAULT_CAPACITY = 10;
   public static int DEFAULT_INCREMENT = 10;
   private final int DELTA;
   private int size;
   public long[] list;

   LongList() {
      this(DEFAULT_CAPACITY, DEFAULT_INCREMENT);
   }

   LongList(int var1) {
      this(var1, DEFAULT_INCREMENT);
   }

   LongList(int var1, int var2) {
      this.size = 0;
      this.DELTA = var2;
      this.list = this.allocate(var1);
   }

   public final int size() {
      return this.size;
   }

   public final boolean add(long var1) {
      if (this.size >= this.list.length) {
         this.resize();
      }

      this.list[this.size++] = var1;
      return true;
   }

   public final void add(int var1, long var2) {
      if (var1 > this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         if (var1 >= this.list.length) {
            this.resize();
         }

         if (var1 == this.size) {
            this.list[this.size++] = var2;
         } else {
            System.arraycopy(this.list, var1, this.list, var1 + 1, this.size - var1);
            this.list[var1] = var2;
            ++this.size;
         }
      }
   }

   public final void add(int var1, long[] var2, int var3, int var4) {
      if (var4 > 0) {
         if (var1 > this.size) {
            throw new IndexOutOfBoundsException();
         } else {
            this.ensure(this.size + var4);
            if (var1 < this.size) {
               System.arraycopy(this.list, var1, this.list, var1 + var4, this.size - var1);
            }

            System.arraycopy(var2, var3, this.list, var1, var4);
            this.size += var4;
         }
      }
   }

   public final long remove(int var1, int var2) {
      if (var2 >= 1 && var1 >= 0) {
         if (var1 + var2 > this.size) {
            return -1L;
         } else {
            long var3 = this.list[var1];
            int var5 = this.size;
            this.size -= var2;
            if (var1 == this.size) {
               return var3;
            } else {
               System.arraycopy(this.list, var1 + var2, this.list, var1, this.size - var1);
               return var3;
            }
         }
      } else {
         return -1L;
      }
   }

   public final long remove(int var1) {
      if (var1 >= this.size) {
         return -1L;
      } else {
         long var2 = this.list[var1];
         this.list[var1] = 0L;
         if (var1 == --this.size) {
            return var2;
         } else {
            System.arraycopy(this.list, var1 + 1, this.list, var1, this.size - var1);
            return var2;
         }
      }
   }

   public final long[] toArray(long[] var1) {
      System.arraycopy(this.list, 0, var1, 0, this.size);
      return var1;
   }

   public final long[] toArray() {
      return this.toArray(new long[this.size]);
   }

   private final void resize() {
      long[] var1 = this.allocate(this.list.length + this.DELTA);
      System.arraycopy(this.list, 0, var1, 0, this.size);
      this.list = var1;
   }

   private final void ensure(int var1) {
      if (this.list.length < var1) {
         int var2 = this.list.length + this.DELTA;
         var1 = var1 < var2 ? var2 : var1;
         long[] var3 = this.allocate(var1);
         System.arraycopy(this.list, 0, var3, 0, this.size);
         this.list = var3;
      }

   }

   private final long[] allocate(int var1) {
      return new long[var1];
   }
}
