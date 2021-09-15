package javax.swing.table;

import java.awt.Component;
import javax.swing.CellEditor;
import javax.swing.JTable;

public interface TableCellEditor extends CellEditor {
   Component getTableCellEditorComponent(JTable var1, Object var2, boolean var3, int var4, int var5);
}
