package javax.accessibility;

import java.util.Vector;

public class AccessibleStateSet {
   protected Vector<AccessibleState> states = null;

   public AccessibleStateSet() {
      this.states = null;
   }

   public AccessibleStateSet(AccessibleState[] var1) {
      if (var1.length != 0) {
         this.states = new Vector(var1.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (!this.states.contains(var1[var2])) {
               this.states.addElement(var1[var2]);
            }
         }
      }

   }

   public boolean add(AccessibleState var1) {
      if (this.states == null) {
         this.states = new Vector();
      }

      if (!this.states.contains(var1)) {
         this.states.addElement(var1);
         return true;
      } else {
         return false;
      }
   }

   public void addAll(AccessibleState[] var1) {
      if (var1.length != 0) {
         if (this.states == null) {
            this.states = new Vector(var1.length);
         }

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (!this.states.contains(var1[var2])) {
               this.states.addElement(var1[var2]);
            }
         }
      }

   }

   public boolean remove(AccessibleState var1) {
      return this.states == null ? false : this.states.removeElement(var1);
   }

   public void clear() {
      if (this.states != null) {
         this.states.removeAllElements();
      }

   }

   public boolean contains(AccessibleState var1) {
      return this.states == null ? false : this.states.contains(var1);
   }

   public AccessibleState[] toArray() {
      if (this.states == null) {
         return new AccessibleState[0];
      } else {
         AccessibleState[] var1 = new AccessibleState[this.states.size()];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            var1[var2] = (AccessibleState)this.states.elementAt(var2);
         }

         return var1;
      }
   }

   public String toString() {
      String var1 = null;
      if (this.states != null && this.states.size() > 0) {
         var1 = ((AccessibleState)this.states.elementAt(0)).toDisplayString();

         for(int var2 = 1; var2 < this.states.size(); ++var2) {
            var1 = var1 + "," + ((AccessibleState)this.states.elementAt(var2)).toDisplayString();
         }
      }

      return var1;
   }
}
