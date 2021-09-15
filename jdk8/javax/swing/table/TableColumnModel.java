package javax.swing.table;

import java.util.Enumeration;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableColumnModelListener;

public interface TableColumnModel {
   void addColumn(TableColumn var1);

   void removeColumn(TableColumn var1);

   void moveColumn(int var1, int var2);

   void setColumnMargin(int var1);

   int getColumnCount();

   Enumeration<TableColumn> getColumns();

   int getColumnIndex(Object var1);

   TableColumn getColumn(int var1);

   int getColumnMargin();

   int getColumnIndexAtX(int var1);

   int getTotalColumnWidth();

   void setColumnSelectionAllowed(boolean var1);

   boolean getColumnSelectionAllowed();

   int[] getSelectedColumns();

   int getSelectedColumnCount();

   void setSelectionModel(ListSelectionModel var1);

   ListSelectionModel getSelectionModel();

   void addColumnModelListener(TableColumnModelListener var1);

   void removeColumnModelListener(TableColumnModelListener var1);
}
