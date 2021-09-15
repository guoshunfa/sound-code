package javax.swing.undo;

import java.io.Serializable;
import javax.swing.UIManager;

public class AbstractUndoableEdit implements UndoableEdit, Serializable {
   protected static final String UndoName = "Undo";
   protected static final String RedoName = "Redo";
   boolean hasBeenDone = true;
   boolean alive = true;

   public void die() {
      this.alive = false;
   }

   public void undo() throws CannotUndoException {
      if (!this.canUndo()) {
         throw new CannotUndoException();
      } else {
         this.hasBeenDone = false;
      }
   }

   public boolean canUndo() {
      return this.alive && this.hasBeenDone;
   }

   public void redo() throws CannotRedoException {
      if (!this.canRedo()) {
         throw new CannotRedoException();
      } else {
         this.hasBeenDone = true;
      }
   }

   public boolean canRedo() {
      return this.alive && !this.hasBeenDone;
   }

   public boolean addEdit(UndoableEdit var1) {
      return false;
   }

   public boolean replaceEdit(UndoableEdit var1) {
      return false;
   }

   public boolean isSignificant() {
      return true;
   }

   public String getPresentationName() {
      return "";
   }

   public String getUndoPresentationName() {
      String var1 = this.getPresentationName();
      if (!"".equals(var1)) {
         var1 = UIManager.getString("AbstractUndoableEdit.undoText") + " " + var1;
      } else {
         var1 = UIManager.getString("AbstractUndoableEdit.undoText");
      }

      return var1;
   }

   public String getRedoPresentationName() {
      String var1 = this.getPresentationName();
      if (!"".equals(var1)) {
         var1 = UIManager.getString("AbstractUndoableEdit.redoText") + " " + var1;
      } else {
         var1 = UIManager.getString("AbstractUndoableEdit.redoText");
      }

      return var1;
   }

   public String toString() {
      return super.toString() + " hasBeenDone: " + this.hasBeenDone + " alive: " + this.alive;
   }
}
