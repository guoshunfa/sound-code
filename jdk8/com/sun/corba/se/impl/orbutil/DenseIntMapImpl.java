package com.sun.corba.se.impl.orbutil;

import java.util.ArrayList;

public class DenseIntMapImpl {
   private ArrayList list = new ArrayList();

   private void checkKey(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Key must be >= 0.");
      }
   }

   public Object get(int var1) {
      this.checkKey(var1);
      Object var2 = null;
      if (var1 < this.list.size()) {
         var2 = this.list.get(var1);
      }

      return var2;
   }

   public void set(int var1, Object var2) {
      this.checkKey(var1);
      this.extend(var1);
      this.list.set(var1, var2);
   }

   private void extend(int var1) {
      if (var1 >= this.list.size()) {
         this.list.ensureCapacity(var1 + 1);
         int var2 = this.list.size();

         while(var2++ <= var1) {
            this.list.add((Object)null);
         }
      }

   }
}
