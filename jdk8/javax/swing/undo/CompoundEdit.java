package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;

public class CompoundEdit extends AbstractUndoableEdit {
   boolean inProgress = true;
   protected Vector<UndoableEdit> edits = new Vector();

   public void undo() throws CannotUndoException {
      super.undo();
      int var1 = this.edits.size();

      while(var1-- > 0) {
         UndoableEdit var2 = (UndoableEdit)this.edits.elementAt(var1);
         var2.undo();
      }

   }

   public void redo() throws CannotRedoException {
      super.redo();
      Enumeration var1 = this.edits.elements();

      while(var1.hasMoreElements()) {
         ((UndoableEdit)var1.nextElement()).redo();
      }

   }

   protected UndoableEdit lastEdit() {
      int var1 = this.edits.size();
      return var1 > 0 ? (UndoableEdit)this.edits.elementAt(var1 - 1) : null;
   }

   public void die() {
      int var1 = this.edits.size();

      for(int var2 = var1 - 1; var2 >= 0; --var2) {
         UndoableEdit var3 = (UndoableEdit)this.edits.elementAt(var2);
         var3.die();
      }

      super.die();
   }

   public boolean addEdit(UndoableEdit var1) {
      if (!this.inProgress) {
         return false;
      } else {
         UndoableEdit var2 = this.lastEdit();
         if (var2 == null) {
            this.edits.addElement(var1);
         } else if (!var2.addEdit(var1)) {
            if (var1.replaceEdit(var2)) {
               this.edits.removeElementAt(this.edits.size() - 1);
            }

            this.edits.addElement(var1);
         }

         return true;
      }
   }

   public void end() {
      this.inProgress = false;
   }

   public boolean canUndo() {
      return !this.isInProgress() && super.canUndo();
   }

   public boolean canRedo() {
      return !this.isInProgress() && super.canRedo();
   }

   public boolean isInProgress() {
      return this.inProgress;
   }

   public boolean isSignificant() {
      Enumeration var1 = this.edits.elements();

      do {
         if (!var1.hasMoreElements()) {
            return false;
         }
      } while(!((UndoableEdit)var1.nextElement()).isSignificant());

      return true;
   }

   public String getPresentationName() {
      UndoableEdit var1 = this.lastEdit();
      return var1 != null ? var1.getPresentationName() : super.getPresentationName();
   }

   public String getUndoPresentationName() {
      UndoableEdit var1 = this.lastEdit();
      return var1 != null ? var1.getUndoPresentationName() : super.getUndoPresentationName();
   }

   public String getRedoPresentationName() {
      UndoableEdit var1 = this.lastEdit();
      return var1 != null ? var1.getRedoPresentationName() : super.getRedoPresentationName();
   }

   public String toString() {
      return super.toString() + " inProgress: " + this.inProgress + " edits: " + this.edits;
   }
}
