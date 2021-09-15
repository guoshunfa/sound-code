package javax.swing.table;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public abstract class AbstractTableModel implements TableModel, Serializable {
   protected EventListenerList listenerList = new EventListenerList();

   public String getColumnName(int var1) {
      String var2;
      for(var2 = ""; var1 >= 0; var1 = var1 / 26 - 1) {
         var2 = (char)((char)(var1 % 26) + 65) + var2;
      }

      return var2;
   }

   public int findColumn(String var1) {
      for(int var2 = 0; var2 < this.getColumnCount(); ++var2) {
         if (var1.equals(this.getColumnName(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public Class<?> getColumnClass(int var1) {
      return Object.class;
   }

   public boolean isCellEditable(int var1, int var2) {
      return false;
   }

   public void setValueAt(Object var1, int var2, int var3) {
   }

   public void addTableModelListener(TableModelListener var1) {
      this.listenerList.add(TableModelListener.class, var1);
   }

   public void removeTableModelListener(TableModelListener var1) {
      this.listenerList.remove(TableModelListener.class, var1);
   }

   public TableModelListener[] getTableModelListeners() {
      return (TableModelListener[])this.listenerList.getListeners(TableModelListener.class);
   }

   public void fireTableDataChanged() {
      this.fireTableChanged(new TableModelEvent(this));
   }

   public void fireTableStructureChanged() {
      this.fireTableChanged(new TableModelEvent(this, -1));
   }

   public void fireTableRowsInserted(int var1, int var2) {
      this.fireTableChanged(new TableModelEvent(this, var1, var2, -1, 1));
   }

   public void fireTableRowsUpdated(int var1, int var2) {
      this.fireTableChanged(new TableModelEvent(this, var1, var2, -1, 0));
   }

   public void fireTableRowsDeleted(int var1, int var2) {
      this.fireTableChanged(new TableModelEvent(this, var1, var2, -1, -1));
   }

   public void fireTableCellUpdated(int var1, int var2) {
      this.fireTableChanged(new TableModelEvent(this, var1, var1, var2));
   }

   public void fireTableChanged(TableModelEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == TableModelListener.class) {
            ((TableModelListener)var2[var3 + 1]).tableChanged(var1);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }
}
