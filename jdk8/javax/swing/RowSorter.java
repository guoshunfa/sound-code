package javax.swing;

import java.util.List;
import javax.swing.event.EventListenerList;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;

public abstract class RowSorter<M> {
   private EventListenerList listenerList = new EventListenerList();

   public abstract M getModel();

   public abstract void toggleSortOrder(int var1);

   public abstract int convertRowIndexToModel(int var1);

   public abstract int convertRowIndexToView(int var1);

   public abstract void setSortKeys(List<? extends RowSorter.SortKey> var1);

   public abstract List<? extends RowSorter.SortKey> getSortKeys();

   public abstract int getViewRowCount();

   public abstract int getModelRowCount();

   public abstract void modelStructureChanged();

   public abstract void allRowsChanged();

   public abstract void rowsInserted(int var1, int var2);

   public abstract void rowsDeleted(int var1, int var2);

   public abstract void rowsUpdated(int var1, int var2);

   public abstract void rowsUpdated(int var1, int var2, int var3);

   public void addRowSorterListener(RowSorterListener var1) {
      this.listenerList.add(RowSorterListener.class, var1);
   }

   public void removeRowSorterListener(RowSorterListener var1) {
      this.listenerList.remove(RowSorterListener.class, var1);
   }

   protected void fireSortOrderChanged() {
      this.fireRowSorterChanged(new RowSorterEvent(this));
   }

   protected void fireRowSorterChanged(int[] var1) {
      this.fireRowSorterChanged(new RowSorterEvent(this, RowSorterEvent.Type.SORTED, var1));
   }

   void fireRowSorterChanged(RowSorterEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == RowSorterListener.class) {
            ((RowSorterListener)var2[var3 + 1]).sorterChanged(var1);
         }
      }

   }

   public static class SortKey {
      private int column;
      private SortOrder sortOrder;

      public SortKey(int var1, SortOrder var2) {
         if (var2 == null) {
            throw new IllegalArgumentException("sort order must be non-null");
         } else {
            this.column = var1;
            this.sortOrder = var2;
         }
      }

      public final int getColumn() {
         return this.column;
      }

      public final SortOrder getSortOrder() {
         return this.sortOrder;
      }

      public int hashCode() {
         byte var1 = 17;
         int var2 = 37 * var1 + this.column;
         var2 = 37 * var2 + this.sortOrder.hashCode();
         return var2;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof RowSorter.SortKey)) {
            return false;
         } else {
            return ((RowSorter.SortKey)var1).column == this.column && ((RowSorter.SortKey)var1).sortOrder == this.sortOrder;
         }
      }
   }
}
