package javax.swing;

import java.util.EventObject;
import javax.swing.event.CellEditorListener;

public interface CellEditor {
   Object getCellEditorValue();

   boolean isCellEditable(EventObject var1);

   boolean shouldSelectCell(EventObject var1);

   boolean stopCellEditing();

   void cancelCellEditing();

   void addCellEditorListener(CellEditorListener var1);

   void removeCellEditorListener(CellEditorListener var1);
}
