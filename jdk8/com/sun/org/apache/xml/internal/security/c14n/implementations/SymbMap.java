package com.sun.org.apache.xml.internal.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

class SymbMap implements Cloneable {
   int free = 23;
   NameSpaceSymbEntry[] entries;
   String[] keys;

   SymbMap() {
      this.entries = new NameSpaceSymbEntry[this.free];
      this.keys = new String[this.free];
   }

   void put(String var1, NameSpaceSymbEntry var2) {
      int var3 = this.index(var1);
      String var4 = this.keys[var3];
      this.keys[var3] = var1;
      this.entries[var3] = var2;
      if ((var4 == null || !var4.equals(var1)) && --this.free == 0) {
         this.free = this.entries.length;
         int var5 = this.free << 2;
         this.rehash(var5);
      }

   }

   List<NameSpaceSymbEntry> entrySet() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.entries.length; ++var2) {
         if (this.entries[var2] != null && !"".equals(this.entries[var2].uri)) {
            var1.add(this.entries[var2]);
         }
      }

      return var1;
   }

   protected int index(Object var1) {
      String[] var2 = this.keys;
      int var3 = var2.length;
      int var4 = (var1.hashCode() & Integer.MAX_VALUE) % var3;
      String var5 = var2[var4];
      if (var5 != null && !var5.equals(var1)) {
         --var3;

         do {
            int var10000;
            if (var4 == var3) {
               var10000 = 0;
            } else {
               ++var4;
               var10000 = var4;
            }

            var4 = var10000;
            var5 = var2[var4];
         } while(var5 != null && !var5.equals(var1));

         return var4;
      } else {
         return var4;
      }
   }

   protected void rehash(int var1) {
      int var2 = this.keys.length;
      String[] var3 = this.keys;
      NameSpaceSymbEntry[] var4 = this.entries;
      this.keys = new String[var1];
      this.entries = new NameSpaceSymbEntry[var1];
      int var5 = var2;

      while(var5-- > 0) {
         if (var3[var5] != null) {
            String var6 = var3[var5];
            int var7 = this.index(var6);
            this.keys[var7] = var6;
            this.entries[var7] = var4[var5];
         }
      }

   }

   NameSpaceSymbEntry get(String var1) {
      return this.entries[this.index(var1)];
   }

   protected Object clone() {
      try {
         SymbMap var1 = (SymbMap)super.clone();
         var1.entries = new NameSpaceSymbEntry[this.entries.length];
         System.arraycopy(this.entries, 0, var1.entries, 0, this.entries.length);
         var1.keys = new String[this.keys.length];
         System.arraycopy(this.keys, 0, var1.keys, 0, this.keys.length);
         return var1;
      } catch (CloneNotSupportedException var2) {
         var2.printStackTrace();
         return null;
      }
   }
}
