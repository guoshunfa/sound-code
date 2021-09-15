package javax.accessibility;

import java.util.Vector;

public class AccessibleRelationSet {
   protected Vector<AccessibleRelation> relations = null;

   public AccessibleRelationSet() {
      this.relations = null;
   }

   public AccessibleRelationSet(AccessibleRelation[] var1) {
      if (var1.length != 0) {
         this.relations = new Vector(var1.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.add(var1[var2]);
         }
      }

   }

   public boolean add(AccessibleRelation var1) {
      if (this.relations == null) {
         this.relations = new Vector();
      }

      AccessibleRelation var2 = this.get(var1.getKey());
      if (var2 == null) {
         this.relations.addElement(var1);
         return true;
      } else {
         Object[] var3 = var2.getTarget();
         Object[] var4 = var1.getTarget();
         int var5 = var3.length + var4.length;
         Object[] var6 = new Object[var5];

         int var7;
         for(var7 = 0; var7 < var3.length; ++var7) {
            var6[var7] = var3[var7];
         }

         var7 = var3.length;

         for(int var8 = 0; var7 < var5; ++var8) {
            var6[var7] = var4[var8];
            ++var7;
         }

         var2.setTarget(var6);
         return true;
      }
   }

   public void addAll(AccessibleRelation[] var1) {
      if (var1.length != 0) {
         if (this.relations == null) {
            this.relations = new Vector(var1.length);
         }

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.add(var1[var2]);
         }
      }

   }

   public boolean remove(AccessibleRelation var1) {
      return this.relations == null ? false : this.relations.removeElement(var1);
   }

   public void clear() {
      if (this.relations != null) {
         this.relations.removeAllElements();
      }

   }

   public int size() {
      return this.relations == null ? 0 : this.relations.size();
   }

   public boolean contains(String var1) {
      return this.get(var1) != null;
   }

   public AccessibleRelation get(String var1) {
      if (this.relations == null) {
         return null;
      } else {
         int var2 = this.relations.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            AccessibleRelation var4 = (AccessibleRelation)this.relations.elementAt(var3);
            if (var4 != null && var4.getKey().equals(var1)) {
               return var4;
            }
         }

         return null;
      }
   }

   public AccessibleRelation[] toArray() {
      if (this.relations == null) {
         return new AccessibleRelation[0];
      } else {
         AccessibleRelation[] var1 = new AccessibleRelation[this.relations.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = (AccessibleRelation)this.relations.elementAt(var2);
         }

         return var1;
      }
   }

   public String toString() {
      String var1 = "";
      if (this.relations != null && this.relations.size() > 0) {
         var1 = ((AccessibleRelation)this.relations.elementAt(0)).toDisplayString();

         for(int var2 = 1; var2 < this.relations.size(); ++var2) {
            var1 = var1 + "," + ((AccessibleRelation)this.relations.elementAt(var2)).toDisplayString();
         }
      }

      return var1;
   }
}
