package sun.swing;

import java.util.ArrayList;
import java.util.List;

public class BakedArrayList extends ArrayList {
   private int _hashCode;

   public BakedArrayList(int var1) {
      super(var1);
   }

   public BakedArrayList(List var1) {
      this(var1.size());
      int var2 = 0;

      for(int var3 = var1.size(); var2 < var3; ++var2) {
         this.add(var1.get(var2));
      }

      this.cacheHashCode();
   }

   public void cacheHashCode() {
      this._hashCode = 1;

      for(int var1 = this.size() - 1; var1 >= 0; --var1) {
         this._hashCode = 31 * this._hashCode + this.get(var1).hashCode();
      }

   }

   public int hashCode() {
      return this._hashCode;
   }

   public boolean equals(Object var1) {
      BakedArrayList var2 = (BakedArrayList)var1;
      int var3 = this.size();
      if (var2.size() != var3) {
         return false;
      } else {
         do {
            if (var3-- <= 0) {
               return true;
            }
         } while(this.get(var3).equals(var2.get(var3)));

         return false;
      }
   }
}
