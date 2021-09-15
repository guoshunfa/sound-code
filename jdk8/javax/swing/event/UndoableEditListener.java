package javax.swing.event;

import java.util.EventListener;

public interface UndoableEditListener extends EventListener {
   void undoableEditHappened(UndoableEditEvent var1);
}
