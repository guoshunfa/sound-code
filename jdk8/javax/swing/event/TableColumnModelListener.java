package javax.swing.event;

import java.util.EventListener;

public interface TableColumnModelListener extends EventListener {
   void columnAdded(TableColumnModelEvent var1);

   void columnRemoved(TableColumnModelEvent var1);

   void columnMoved(TableColumnModelEvent var1);

   void columnMarginChanged(ChangeEvent var1);

   void columnSelectionChanged(ListSelectionEvent var1);
}
