package javax.swing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SpinnerListModel extends AbstractSpinnerModel implements Serializable {
   private List list;
   private int index;

   public SpinnerListModel(List<?> var1) {
      if (var1 != null && var1.size() != 0) {
         this.list = var1;
         this.index = 0;
      } else {
         throw new IllegalArgumentException("SpinnerListModel(List) expects non-null non-empty List");
      }
   }

   public SpinnerListModel(Object[] var1) {
      if (var1 != null && var1.length != 0) {
         this.list = Arrays.asList(var1);
         this.index = 0;
      } else {
         throw new IllegalArgumentException("SpinnerListModel(Object[]) expects non-null non-empty Object[]");
      }
   }

   public SpinnerListModel() {
      this(new Object[]{"empty"});
   }

   public List<?> getList() {
      return this.list;
   }

   public void setList(List<?> var1) {
      if (var1 != null && var1.size() != 0) {
         if (!var1.equals(this.list)) {
            this.list = var1;
            this.index = 0;
            this.fireStateChanged();
         }

      } else {
         throw new IllegalArgumentException("invalid list");
      }
   }

   public Object getValue() {
      return this.list.get(this.index);
   }

   public void setValue(Object var1) {
      int var2 = this.list.indexOf(var1);
      if (var2 == -1) {
         throw new IllegalArgumentException("invalid sequence element");
      } else {
         if (var2 != this.index) {
            this.index = var2;
            this.fireStateChanged();
         }

      }
   }

   public Object getNextValue() {
      return this.index >= this.list.size() - 1 ? null : this.list.get(this.index + 1);
   }

   public Object getPreviousValue() {
      return this.index <= 0 ? null : this.list.get(this.index - 1);
   }

   Object findNextMatch(String var1) {
      int var2 = this.list.size();
      if (var2 == 0) {
         return null;
      } else {
         int var3 = this.index;

         do {
            Object var4 = this.list.get(var3);
            String var5 = var4.toString();
            if (var5 != null && var5.startsWith(var1)) {
               return var4;
            }

            var3 = (var3 + 1) % var2;
         } while(var3 != this.index);

         return null;
      }
   }
}
