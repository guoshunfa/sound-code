package com.sun.corba.se.impl.util;

import java.util.Dictionary;
import java.util.Enumeration;

public final class IdentityHashtable extends Dictionary {
   private transient IdentityHashtableEntry[] table;
   private transient int count;
   private int threshold;
   private float loadFactor;

   public IdentityHashtable(int var1, float var2) {
      if (var1 > 0 && (double)var2 > 0.0D) {
         this.loadFactor = var2;
         this.table = new IdentityHashtableEntry[var1];
         this.threshold = (int)((float)var1 * var2);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public IdentityHashtable(int var1) {
      this(var1, 0.75F);
   }

   public IdentityHashtable() {
      this(101, 0.75F);
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public Enumeration keys() {
      return new IdentityHashtableEnumerator(this.table, true);
   }

   public Enumeration elements() {
      return new IdentityHashtableEnumerator(this.table, false);
   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         IdentityHashtableEntry[] var2 = this.table;
         int var3 = var2.length;

         while(var3-- > 0) {
            for(IdentityHashtableEntry var4 = var2[var3]; var4 != null; var4 = var4.next) {
               if (var4.value == var1) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean containsKey(Object var1) {
      IdentityHashtableEntry[] var2 = this.table;
      int var3 = System.identityHashCode(var1);
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(IdentityHashtableEntry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key == var1) {
            return true;
         }
      }

      return false;
   }

   public Object get(Object var1) {
      IdentityHashtableEntry[] var2 = this.table;
      int var3 = System.identityHashCode(var1);
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;

      for(IdentityHashtableEntry var5 = var2[var4]; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key == var1) {
            return var5.value;
         }
      }

      return null;
   }

   protected void rehash() {
      int var1 = this.table.length;
      IdentityHashtableEntry[] var2 = this.table;
      int var3 = var1 * 2 + 1;
      IdentityHashtableEntry[] var4 = new IdentityHashtableEntry[var3];
      this.threshold = (int)((float)var3 * this.loadFactor);
      this.table = var4;
      int var5 = var1;

      IdentityHashtableEntry var7;
      int var8;
      while(var5-- > 0) {
         for(IdentityHashtableEntry var6 = var2[var5]; var6 != null; var4[var8] = var7) {
            var7 = var6;
            var6 = var6.next;
            var8 = (var7.hash & Integer.MAX_VALUE) % var3;
            var7.next = var4[var8];
         }
      }

   }

   public Object put(Object var1, Object var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         IdentityHashtableEntry[] var3 = this.table;
         int var4 = System.identityHashCode(var1);
         int var5 = (var4 & Integer.MAX_VALUE) % var3.length;

         IdentityHashtableEntry var6;
         for(var6 = var3[var5]; var6 != null; var6 = var6.next) {
            if (var6.hash == var4 && var6.key == var1) {
               Object var7 = var6.value;
               var6.value = var2;
               return var7;
            }
         }

         if (this.count >= this.threshold) {
            this.rehash();
            return this.put(var1, var2);
         } else {
            var6 = new IdentityHashtableEntry();
            var6.hash = var4;
            var6.key = var1;
            var6.value = var2;
            var6.next = var3[var5];
            var3[var5] = var6;
            ++this.count;
            return null;
         }
      }
   }

   public Object remove(Object var1) {
      IdentityHashtableEntry[] var2 = this.table;
      int var3 = System.identityHashCode(var1);
      int var4 = (var3 & Integer.MAX_VALUE) % var2.length;
      IdentityHashtableEntry var5 = var2[var4];

      for(IdentityHashtableEntry var6 = null; var5 != null; var5 = var5.next) {
         if (var5.hash == var3 && var5.key == var1) {
            if (var6 != null) {
               var6.next = var5.next;
            } else {
               var2[var4] = var5.next;
            }

            --this.count;
            return var5.value;
         }

         var6 = var5;
      }

      return null;
   }

   public void clear() {
      IdentityHashtableEntry[] var1 = this.table;
      int var2 = var1.length;

      while(true) {
         --var2;
         if (var2 < 0) {
            this.count = 0;
            return;
         }

         var1[var2] = null;
      }
   }

   public String toString() {
      int var1 = this.size() - 1;
      StringBuffer var2 = new StringBuffer();
      Enumeration var3 = this.keys();
      Enumeration var4 = this.elements();
      var2.append("{");

      for(int var5 = 0; var5 <= var1; ++var5) {
         String var6 = var3.nextElement().toString();
         String var7 = var4.nextElement().toString();
         var2.append(var6 + "=" + var7);
         if (var5 < var1) {
            var2.append(", ");
         }
      }

      var2.append("}");
      return var2.toString();
   }
}
