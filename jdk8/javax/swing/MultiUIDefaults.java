package javax.swing;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class MultiUIDefaults extends UIDefaults {
   private UIDefaults[] tables;

   public MultiUIDefaults(UIDefaults[] var1) {
      this.tables = var1;
   }

   public MultiUIDefaults() {
      this.tables = new UIDefaults[0];
   }

   public Object get(Object var1) {
      Object var2 = super.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         UIDefaults[] var3 = this.tables;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            UIDefaults var6 = var3[var5];
            var2 = var6 != null ? var6.get(var1) : null;
            if (var2 != null) {
               return var2;
            }
         }

         return null;
      }
   }

   public Object get(Object var1, Locale var2) {
      Object var3 = super.get(var1, var2);
      if (var3 != null) {
         return var3;
      } else {
         UIDefaults[] var4 = this.tables;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            UIDefaults var7 = var4[var6];
            var3 = var7 != null ? var7.get(var1, var2) : null;
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      }
   }

   public int size() {
      return this.entrySet().size();
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public Enumeration<Object> keys() {
      return new MultiUIDefaults.MultiUIDefaultsEnumerator(MultiUIDefaults.MultiUIDefaultsEnumerator.Type.KEYS, this.entrySet());
   }

   public Enumeration<Object> elements() {
      return new MultiUIDefaults.MultiUIDefaultsEnumerator(MultiUIDefaults.MultiUIDefaultsEnumerator.Type.ELEMENTS, this.entrySet());
   }

   public Set<Map.Entry<Object, Object>> entrySet() {
      HashSet var1 = new HashSet();

      for(int var2 = this.tables.length - 1; var2 >= 0; --var2) {
         if (this.tables[var2] != null) {
            var1.addAll(this.tables[var2].entrySet());
         }
      }

      var1.addAll(super.entrySet());
      return var1;
   }

   protected void getUIError(String var1) {
      if (this.tables.length > 0) {
         this.tables[0].getUIError(var1);
      } else {
         super.getUIError(var1);
      }

   }

   public Object remove(Object var1) {
      Object var2 = null;

      for(int var3 = this.tables.length - 1; var3 >= 0; --var3) {
         if (this.tables[var3] != null) {
            Object var4 = this.tables[var3].remove(var1);
            if (var4 != null) {
               var2 = var4;
            }
         }
      }

      Object var5 = super.remove(var1);
      if (var5 != null) {
         var2 = var5;
      }

      return var2;
   }

   public void clear() {
      super.clear();
      UIDefaults[] var1 = this.tables;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         UIDefaults var4 = var1[var3];
         if (var4 != null) {
            var4.clear();
         }
      }

   }

   public synchronized String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("{");
      Enumeration var2 = this.keys();

      while(var2.hasMoreElements()) {
         Object var3 = var2.nextElement();
         var1.append(var3 + "=" + this.get(var3) + ", ");
      }

      int var4 = var1.length();
      if (var4 > 1) {
         var1.delete(var4 - 2, var4);
      }

      var1.append("}");
      return var1.toString();
   }

   private static class MultiUIDefaultsEnumerator implements Enumeration<Object> {
      private Iterator<Map.Entry<Object, Object>> iterator;
      private MultiUIDefaults.MultiUIDefaultsEnumerator.Type type;

      MultiUIDefaultsEnumerator(MultiUIDefaults.MultiUIDefaultsEnumerator.Type var1, Set<Map.Entry<Object, Object>> var2) {
         this.type = var1;
         this.iterator = var2.iterator();
      }

      public boolean hasMoreElements() {
         return this.iterator.hasNext();
      }

      public Object nextElement() {
         switch(this.type) {
         case KEYS:
            return ((Map.Entry)this.iterator.next()).getKey();
         case ELEMENTS:
            return ((Map.Entry)this.iterator.next()).getValue();
         default:
            return null;
         }
      }

      public static enum Type {
         KEYS,
         ELEMENTS;
      }
   }
}
