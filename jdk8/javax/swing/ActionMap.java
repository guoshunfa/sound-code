package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class ActionMap implements Serializable {
   private transient ArrayTable arrayTable;
   private ActionMap parent;

   public void setParent(ActionMap var1) {
      this.parent = var1;
   }

   public ActionMap getParent() {
      return this.parent;
   }

   public void put(Object var1, Action var2) {
      if (var1 != null) {
         if (var2 == null) {
            this.remove(var1);
         } else {
            if (this.arrayTable == null) {
               this.arrayTable = new ArrayTable();
            }

            this.arrayTable.put(var1, var2);
         }

      }
   }

   public Action get(Object var1) {
      Action var2 = this.arrayTable == null ? null : (Action)this.arrayTable.get(var1);
      if (var2 == null) {
         ActionMap var3 = this.getParent();
         if (var3 != null) {
            return var3.get(var1);
         }
      }

      return var2;
   }

   public void remove(Object var1) {
      if (this.arrayTable != null) {
         this.arrayTable.remove(var1);
      }

   }

   public void clear() {
      if (this.arrayTable != null) {
         this.arrayTable.clear();
      }

   }

   public Object[] keys() {
      return this.arrayTable == null ? null : this.arrayTable.getKeys((Object[])null);
   }

   public int size() {
      return this.arrayTable == null ? 0 : this.arrayTable.size();
   }

   public Object[] allKeys() {
      int var1 = this.size();
      ActionMap var2 = this.getParent();
      if (var1 == 0) {
         return var2 != null ? var2.allKeys() : this.keys();
      } else if (var2 == null) {
         return this.keys();
      } else {
         Object[] var3 = this.keys();
         Object[] var4 = var2.allKeys();
         if (var4 == null) {
            return var3;
         } else if (var3 == null) {
            return var4;
         } else {
            HashMap var5 = new HashMap();

            int var6;
            for(var6 = var3.length - 1; var6 >= 0; --var6) {
               var5.put(var3[var6], var3[var6]);
            }

            for(var6 = var4.length - 1; var6 >= 0; --var6) {
               var5.put(var4[var6], var4[var6]);
            }

            return var5.keySet().toArray();
         }
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      ArrayTable.writeArrayTable(var1, this.arrayTable);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();

      for(int var2 = var1.readInt() - 1; var2 >= 0; --var2) {
         this.put(var1.readObject(), (Action)var1.readObject());
      }

   }
}
