package javax.swing.undo;

public interface UndoableEdit {
   void undo() throws CannotUndoException;

   boolean canUndo();

   void redo() throws CannotRedoException;

   boolean canRedo();

   void die();

   boolean addEdit(UndoableEdit var1);

   boolean replaceEdit(UndoableEdit var1);

   boolean isSignificant();

   String getPresentationName();

   String getUndoPresentationName();

   String getRedoPresentationName();
}
