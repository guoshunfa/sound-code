package javax.swing.text;

import java.io.Serializable;

public class TabSet implements Serializable {
   private TabStop[] tabs;
   private int hashCode = Integer.MAX_VALUE;

   public TabSet(TabStop[] var1) {
      if (var1 != null) {
         int var2 = var1.length;
         this.tabs = new TabStop[var2];
         System.arraycopy(var1, 0, this.tabs, 0, var2);
      } else {
         this.tabs = null;
      }

   }

   public int getTabCount() {
      return this.tabs == null ? 0 : this.tabs.length;
   }

   public TabStop getTab(int var1) {
      int var2 = this.getTabCount();
      if (var1 >= 0 && var1 < var2) {
         return this.tabs[var1];
      } else {
         throw new IllegalArgumentException(var1 + " is outside the range of tabs");
      }
   }

   public TabStop getTabAfter(float var1) {
      int var2 = this.getTabIndexAfter(var1);
      return var2 == -1 ? null : this.tabs[var2];
   }

   public int getTabIndex(TabStop var1) {
      for(int var2 = this.getTabCount() - 1; var2 >= 0; --var2) {
         if (this.getTab(var2) == var1) {
            return var2;
         }
      }

      return -1;
   }

   public int getTabIndexAfter(float var1) {
      int var3 = 0;
      int var4 = this.getTabCount();

      while(true) {
         while(var3 != var4) {
            int var2 = (var4 - var3) / 2 + var3;
            if (var1 <= this.tabs[var2].getPosition()) {
               if (var2 == 0 || var1 > this.tabs[var2 - 1].getPosition()) {
                  return var2;
               }

               var4 = var2;
            } else if (var3 == var2) {
               var3 = var4;
            } else {
               var3 = var2;
            }
         }

         return -1;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabSet)) {
         return false;
      } else {
         TabSet var2 = (TabSet)var1;
         int var3 = this.getTabCount();
         if (var2.getTabCount() != var3) {
            return false;
         } else {
            for(int var4 = 0; var4 < var3; ++var4) {
               TabStop var5 = this.getTab(var4);
               TabStop var6 = var2.getTab(var4);
               if (var5 == null && var6 != null || var5 != null && !this.getTab(var4).equals(var2.getTab(var4))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      if (this.hashCode == Integer.MAX_VALUE) {
         this.hashCode = 0;
         int var1 = this.getTabCount();

         for(int var2 = 0; var2 < var1; ++var2) {
            TabStop var3 = this.getTab(var2);
            this.hashCode ^= var3 != null ? this.getTab(var2).hashCode() : 0;
         }

         if (this.hashCode == Integer.MAX_VALUE) {
            --this.hashCode;
         }
      }

      return this.hashCode;
   }

   public String toString() {
      int var1 = this.getTabCount();
      StringBuilder var2 = new StringBuilder("[ ");

      for(int var3 = 0; var3 < var1; ++var3) {
         if (var3 > 0) {
            var2.append(" - ");
         }

         var2.append(this.getTab(var3).toString());
      }

      var2.append(" ]");
      return var2.toString();
   }
}
