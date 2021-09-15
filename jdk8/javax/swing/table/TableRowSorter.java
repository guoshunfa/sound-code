package javax.swing.table;

import java.text.Collator;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;

public class TableRowSorter<M extends TableModel> extends DefaultRowSorter<M, Integer> {
   private static final Comparator COMPARABLE_COMPARATOR = new TableRowSorter.ComparableComparator();
   private M tableModel;
   private TableStringConverter stringConverter;

   public TableRowSorter() {
      this((TableModel)null);
   }

   public TableRowSorter(M var1) {
      this.setModel(var1);
   }

   public void setModel(M var1) {
      this.tableModel = var1;
      this.setModelWrapper(new TableRowSorter.TableRowSorterModelWrapper());
   }

   public void setStringConverter(TableStringConverter var1) {
      this.stringConverter = var1;
   }

   public TableStringConverter getStringConverter() {
      return this.stringConverter;
   }

   public Comparator<?> getComparator(int var1) {
      Comparator var2 = super.getComparator(var1);
      if (var2 != null) {
         return var2;
      } else {
         Class var3 = ((TableModel)this.getModel()).getColumnClass(var1);
         if (var3 == String.class) {
            return Collator.getInstance();
         } else {
            return (Comparator)(Comparable.class.isAssignableFrom(var3) ? COMPARABLE_COMPARATOR : Collator.getInstance());
         }
      }
   }

   protected boolean useToString(int var1) {
      Comparator var2 = super.getComparator(var1);
      if (var2 != null) {
         return false;
      } else {
         Class var3 = ((TableModel)this.getModel()).getColumnClass(var1);
         if (var3 == String.class) {
            return false;
         } else {
            return !Comparable.class.isAssignableFrom(var3);
         }
      }
   }

   private static class ComparableComparator implements Comparator {
      private ComparableComparator() {
      }

      public int compare(Object var1, Object var2) {
         return ((Comparable)var1).compareTo(var2);
      }

      // $FF: synthetic method
      ComparableComparator(Object var1) {
         this();
      }
   }

   private class TableRowSorterModelWrapper extends DefaultRowSorter.ModelWrapper<M, Integer> {
      private TableRowSorterModelWrapper() {
      }

      public M getModel() {
         return TableRowSorter.this.tableModel;
      }

      public int getColumnCount() {
         return TableRowSorter.this.tableModel == null ? 0 : TableRowSorter.this.tableModel.getColumnCount();
      }

      public int getRowCount() {
         return TableRowSorter.this.tableModel == null ? 0 : TableRowSorter.this.tableModel.getRowCount();
      }

      public Object getValueAt(int var1, int var2) {
         return TableRowSorter.this.tableModel.getValueAt(var1, var2);
      }

      public String getStringValueAt(int var1, int var2) {
         TableStringConverter var3 = TableRowSorter.this.getStringConverter();
         if (var3 != null) {
            String var6 = var3.toString(TableRowSorter.this.tableModel, var1, var2);
            return var6 != null ? var6 : "";
         } else {
            Object var4 = this.getValueAt(var1, var2);
            if (var4 == null) {
               return "";
            } else {
               String var5 = var4.toString();
               return var5 == null ? "" : var5;
            }
         }
      }

      public Integer getIdentifier(int var1) {
         return var1;
      }

      // $FF: synthetic method
      TableRowSorterModelWrapper(Object var2) {
         this();
      }
   }
}
