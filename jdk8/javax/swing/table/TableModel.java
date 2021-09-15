package javax.swing.table;

import javax.swing.event.TableModelListener;

public interface TableModel {
   int getRowCount();

   int getColumnCount();

   String getColumnName(int var1);

   Class<?> getColumnClass(int var1);

   boolean isCellEditable(int var1, int var2);

   Object getValueAt(int var1, int var2);

   void setValueAt(Object var1, int var2, int var3);

   void addTableModelListener(TableModelListener var1);

   void removeTableModelListener(TableModelListener var1);
}
