package javax.swing.event;

import java.util.EventObject;
import javax.swing.RowSorter;

public class RowSorterEvent extends EventObject {
   private RowSorterEvent.Type type;
   private int[] oldViewToModel;

   public RowSorterEvent(RowSorter var1) {
      this(var1, RowSorterEvent.Type.SORT_ORDER_CHANGED, (int[])null);
   }

   public RowSorterEvent(RowSorter var1, RowSorterEvent.Type var2, int[] var3) {
      super(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("type must be non-null");
      } else {
         this.type = var2;
         this.oldViewToModel = var3;
      }
   }

   public RowSorter getSource() {
      return (RowSorter)super.getSource();
   }

   public RowSorterEvent.Type getType() {
      return this.type;
   }

   public int convertPreviousRowIndexToModel(int var1) {
      return this.oldViewToModel != null && var1 >= 0 && var1 < this.oldViewToModel.length ? this.oldViewToModel[var1] : -1;
   }

   public int getPreviousRowCount() {
      return this.oldViewToModel == null ? 0 : this.oldViewToModel.length;
   }

   public static enum Type {
      SORT_ORDER_CHANGED,
      SORTED;
   }
}
