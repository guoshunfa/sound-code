package javax.swing.event;

import java.util.EventListener;

public interface CellEditorListener extends EventListener {
   void editingStopped(ChangeEvent var1);

   void editingCanceled(ChangeEvent var1);
}
