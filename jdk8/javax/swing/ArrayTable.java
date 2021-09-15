package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

class ArrayTable implements Cloneable {
   private Object table = null;
   private static final int ARRAY_BOUNDARY = 8;

   static void writeArrayTable(ObjectOutputStream var0, ArrayTable var1) throws IOException {
      Object[] var2;
      if (var1 != null && (var2 = var1.getKeys((Object[])null)) != null) {
         int var3 = 0;

         for(int var4 = 0; var4 < var2.length; ++var4) {
            Object var5 = var2[var4];
            if ((!(var5 instanceof Serializable) || !(var1.get(var5) instanceof Serializable)) && (!(var5 instanceof ClientPropertyKey) || !((ClientPropertyKey)var5).getReportValueNotSerializable())) {
               var2[var4] = null;
            } else {
               ++var3;
            }
         }

         var0.writeInt(var3);
         if (var3 > 0) {
            Object[] var8 = var2;
            int var9 = var2.length;

            for(int var6 = 0; var6 < var9; ++var6) {
               Object var7 = var8[var6];
               if (var7 != null) {
                  var0.writeObject(var7);
                  var0.writeObject(var1.get(var7));
                  --var3;
                  if (var3 == 0) {
                     break;
                  }
               }
            }
         }
      } else {
         var0.writeInt(0);
      }

   }

   public void put(Object var1, Object var2) {
      if (this.table == null) {
         this.table = new Object[]{var1, var2};
      } else {
         int var3 = this.size();
         if (var3 < 8) {
            Object[] var4;
            int var5;
            if (this.containsKey(var1)) {
               var4 = (Object[])((Object[])this.table);

               for(var5 = 0; var5 < var4.length - 1; var5 += 2) {
                  if (var4[var5].equals(var1)) {
                     var4[var5 + 1] = var2;
                     break;
                  }
               }
            } else {
               var4 = (Object[])((Object[])this.table);
               var5 = var4.length;
               Object[] var6 = new Object[var5 + 2];
               System.arraycopy(var4, 0, var6, 0, var5);
               var6[var5] = var1;
               var6[var5 + 1] = var2;
               this.table = var6;
            }
         } else {
            if (var3 == 8 && this.isArray()) {
               this.grow();
            }

            ((Hashtable)this.table).put(var1, var2);
         }
      }

   }

   public Object get(Object var1) {
      Object var2 = null;
      if (this.table != null) {
         if (this.isArray()) {
            Object[] var3 = (Object[])((Object[])this.table);

            for(int var4 = 0; var4 < var3.length - 1; var4 += 2) {
               if (var3[var4].equals(var1)) {
                  var2 = var3[var4 + 1];
                  break;
               }
            }
         } else {
            var2 = ((Hashtable)this.table).get(var1);
         }
      }

      return var2;
   }

   public int size() {
      if (this.table == null) {
         return 0;
      } else {
         int var1;
         if (this.isArray()) {
            var1 = ((Object[])((Object[])this.table)).length / 2;
         } else {
            var1 = ((Hashtable)this.table).size();
         }

         return var1;
      }
   }

   public boolean containsKey(Object var1) {
      boolean var2 = false;
      if (this.table != null) {
         if (this.isArray()) {
            Object[] var3 = (Object[])((Object[])this.table);

            for(int var4 = 0; var4 < var3.length - 1; var4 += 2) {
               if (var3[var4].equals(var1)) {
                  var2 = true;
                  break;
               }
            }
         } else {
            var2 = ((Hashtable)this.table).containsKey(var1);
         }
      }

      return var2;
   }

   public Object remove(Object var1) {
      Object var2 = null;
      if (var1 == null) {
         return null;
      } else {
         if (this.table != null) {
            if (!this.isArray()) {
               var2 = ((Hashtable)this.table).remove(var1);
            } else {
               int var3 = -1;
               Object[] var4 = (Object[])((Object[])this.table);

               for(int var5 = var4.length - 2; var5 >= 0; var5 -= 2) {
                  if (var4[var5].equals(var1)) {
                     var3 = var5;
                     var2 = var4[var5 + 1];
                     break;
                  }
               }

               if (var3 != -1) {
                  Object[] var6 = new Object[var4.length - 2];
                  System.arraycopy(var4, 0, var6, 0, var3);
                  if (var3 < var6.length) {
                     System.arraycopy(var4, var3 + 2, var6, var3, var6.length - var3);
                  }

                  this.table = var6.length == 0 ? null : var6;
               }
            }

            if (this.size() == 7 && !this.isArray()) {
               this.shrink();
            }
         }

         return var2;
      }
   }

   public void clear() {
      this.table = null;
   }

   public Object clone() {
      ArrayTable var1 = new ArrayTable();
      if (this.isArray()) {
         Object[] var2 = (Object[])((Object[])this.table);

         for(int var3 = 0; var3 < var2.length - 1; var3 += 2) {
            var1.put(var2[var3], var2[var3 + 1]);
         }
      } else {
         Hashtable var5 = (Hashtable)this.table;
         Enumeration var6 = var5.keys();

         while(var6.hasMoreElements()) {
            Object var4 = var6.nextElement();
            var1.put(var4, var5.get(var4));
         }
      }

      return var1;
   }

   public Object[] getKeys(Object[] var1) {
      if (this.table == null) {
         return null;
      } else {
         int var4;
         if (this.isArray()) {
            Object[] var2 = (Object[])((Object[])this.table);
            if (var1 == null) {
               var1 = new Object[var2.length / 2];
            }

            int var3 = 0;

            for(var4 = 0; var3 < var2.length - 1; ++var4) {
               var1[var4] = var2[var3];
               var3 += 2;
            }
         } else {
            Hashtable var5 = (Hashtable)this.table;
            Enumeration var6 = var5.keys();
            var4 = var5.size();
            if (var1 == null) {
               var1 = new Object[var4];
            }

            while(var4 > 0) {
               --var4;
               var1[var4] = var6.nextElement();
            }
         }

         return var1;
      }
   }

   private boolean isArray() {
      return this.table instanceof Object[];
   }

   private void grow() {
      Object[] var1 = (Object[])((Object[])this.table);
      Hashtable var2 = new Hashtable(var1.length / 2);

      for(int var3 = 0; var3 < var1.length; var3 += 2) {
         var2.put(var1[var3], var1[var3 + 1]);
      }

      this.table = var2;
   }

   private void shrink() {
      Hashtable var1 = (Hashtable)this.table;
      Object[] var2 = new Object[var1.size() * 2];
      Enumeration var3 = var1.keys();

      for(int var4 = 0; var3.hasMoreElements(); var4 += 2) {
         Object var5 = var3.nextElement();
         var2[var4] = var5;
         var2[var4 + 1] = var1.get(var5);
      }

      this.table = var2;
   }
}
