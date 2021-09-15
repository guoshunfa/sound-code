package javax.swing.undo;

import java.util.Iterator;
import java.util.Vector;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoManager extends CompoundEdit implements UndoableEditListener {
   int indexOfNextAdd = 0;
   int limit = 100;

   public UndoManager() {
      this.edits.ensureCapacity(this.limit);
   }

   public synchronized int getLimit() {
      return this.limit;
   }

   public synchronized void discardAllEdits() {
      Iterator var1 = this.edits.iterator();

      while(var1.hasNext()) {
         UndoableEdit var2 = (UndoableEdit)var1.next();
         var2.die();
      }

      this.edits = new Vector();
      this.indexOfNextAdd = 0;
   }

   protected void trimForLimit() {
      if (this.limit >= 0) {
         int var1 = this.edits.size();
         if (var1 > this.limit) {
            int var2 = this.limit / 2;
            int var3 = this.indexOfNextAdd - 1 - var2;
            int var4 = this.indexOfNextAdd - 1 + var2;
            if (var4 - var3 + 1 > this.limit) {
               ++var3;
            }

            if (var3 < 0) {
               var4 -= var3;
               var3 = 0;
            }

            if (var4 >= var1) {
               int var5 = var1 - var4 - 1;
               var4 += var5;
               var3 += var5;
            }

            this.trimEdits(var4 + 1, var1 - 1);
            this.trimEdits(0, var3 - 1);
         }
      }

   }

   protected void trimEdits(int var1, int var2) {
      if (var1 <= var2) {
         for(int var3 = var2; var1 <= var3; --var3) {
            UndoableEdit var4 = (UndoableEdit)this.edits.elementAt(var3);
            var4.die();
            this.edits.removeElementAt(var3);
         }

         if (this.indexOfNextAdd > var2) {
            this.indexOfNextAdd -= var2 - var1 + 1;
         } else if (this.indexOfNextAdd >= var1) {
            this.indexOfNextAdd = var1;
         }
      }

   }

   public synchronized void setLimit(int var1) {
      if (!this.inProgress) {
         throw new RuntimeException("Attempt to call UndoManager.setLimit() after UndoManager.end() has been called");
      } else {
         this.limit = var1;
         this.trimForLimit();
      }
   }

   protected UndoableEdit editToBeUndone() {
      int var1 = this.indexOfNextAdd;

      UndoableEdit var2;
      do {
         if (var1 <= 0) {
            return null;
         }

         --var1;
         var2 = (UndoableEdit)this.edits.elementAt(var1);
      } while(!var2.isSignificant());

      return var2;
   }

   protected UndoableEdit editToBeRedone() {
      int var1 = this.edits.size();
      int var2 = this.indexOfNextAdd;

      UndoableEdit var3;
      do {
         if (var2 >= var1) {
            return null;
         }

         var3 = (UndoableEdit)this.edits.elementAt(var2++);
      } while(!var3.isSignificant());

      return var3;
   }

   protected void undoTo(UndoableEdit var1) throws CannotUndoException {
      UndoableEdit var3;
      for(boolean var2 = false; !var2; var2 = var3 == var1) {
         var3 = (UndoableEdit)this.edits.elementAt(--this.indexOfNextAdd);
         var3.undo();
      }

   }

   protected void redoTo(UndoableEdit var1) throws CannotRedoException {
      UndoableEdit var3;
      for(boolean var2 = false; !var2; var2 = var3 == var1) {
         var3 = (UndoableEdit)this.edits.elementAt(this.indexOfNextAdd++);
         var3.redo();
      }

   }

   public synchronized void undoOrRedo() throws CannotRedoException, CannotUndoException {
      if (this.indexOfNextAdd == this.edits.size()) {
         this.undo();
      } else {
         this.redo();
      }

   }

   public synchronized boolean canUndoOrRedo() {
      return this.indexOfNextAdd == this.edits.size() ? this.canUndo() : this.canRedo();
   }

   public synchronized void undo() throws CannotUndoException {
      if (this.inProgress) {
         UndoableEdit var1 = this.editToBeUndone();
         if (var1 == null) {
            throw new CannotUndoException();
         }

         this.undoTo(var1);
      } else {
         super.undo();
      }

   }

   public synchronized boolean canUndo() {
      if (!this.inProgress) {
         return super.canUndo();
      } else {
         UndoableEdit var1 = this.editToBeUndone();
         return var1 != null && var1.canUndo();
      }
   }

   public synchronized void redo() throws CannotRedoException {
      if (this.inProgress) {
         UndoableEdit var1 = this.editToBeRedone();
         if (var1 == null) {
            throw new CannotRedoException();
         }

         this.redoTo(var1);
      } else {
         super.redo();
      }

   }

   public synchronized boolean canRedo() {
      if (!this.inProgress) {
         return super.canRedo();
      } else {
         UndoableEdit var1 = this.editToBeRedone();
         return var1 != null && var1.canRedo();
      }
   }

   public synchronized boolean addEdit(UndoableEdit var1) {
      this.trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
      boolean var2 = super.addEdit(var1);
      if (this.inProgress) {
         var2 = true;
      }

      this.indexOfNextAdd = this.edits.size();
      this.trimForLimit();
      return var2;
   }

   public synchronized void end() {
      super.end();
      this.trimEdits(this.indexOfNextAdd, this.edits.size() - 1);
   }

   public synchronized String getUndoOrRedoPresentationName() {
      return this.indexOfNextAdd == this.edits.size() ? this.getUndoPresentationName() : this.getRedoPresentationName();
   }

   public synchronized String getUndoPresentationName() {
      if (this.inProgress) {
         return this.canUndo() ? this.editToBeUndone().getUndoPresentationName() : UIManager.getString("AbstractUndoableEdit.undoText");
      } else {
         return super.getUndoPresentationName();
      }
   }

   public synchronized String getRedoPresentationName() {
      if (this.inProgress) {
         return this.canRedo() ? this.editToBeRedone().getRedoPresentationName() : UIManager.getString("AbstractUndoableEdit.redoText");
      } else {
         return super.getRedoPresentationName();
      }
   }

   public void undoableEditHappened(UndoableEditEvent var1) {
      this.addEdit(var1.getEdit());
   }

   public String toString() {
      return super.toString() + " limit: " + this.limit + " indexOfNextAdd: " + this.indexOfNextAdd;
   }
}
