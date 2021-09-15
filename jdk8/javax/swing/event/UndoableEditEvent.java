package javax.swing.event;

import java.util.EventObject;
import javax.swing.undo.UndoableEdit;

public class UndoableEditEvent extends EventObject {
   private UndoableEdit myEdit;

   public UndoableEditEvent(Object var1, UndoableEdit var2) {
      super(var1);
      this.myEdit = var2;
   }

   public UndoableEdit getEdit() {
      return this.myEdit;
   }
}
