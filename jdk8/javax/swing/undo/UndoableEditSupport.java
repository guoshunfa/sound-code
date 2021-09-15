package javax.swing.undo;

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;

public class UndoableEditSupport {
   protected int updateLevel;
   protected CompoundEdit compoundEdit;
   protected Vector<UndoableEditListener> listeners;
   protected Object realSource;

   public UndoableEditSupport() {
      this((Object)null);
   }

   public UndoableEditSupport(Object var1) {
      this.realSource = var1 == null ? this : var1;
      this.updateLevel = 0;
      this.compoundEdit = null;
      this.listeners = new Vector();
   }

   public synchronized void addUndoableEditListener(UndoableEditListener var1) {
      this.listeners.addElement(var1);
   }

   public synchronized void removeUndoableEditListener(UndoableEditListener var1) {
      this.listeners.removeElement(var1);
   }

   public synchronized UndoableEditListener[] getUndoableEditListeners() {
      return (UndoableEditListener[])this.listeners.toArray(new UndoableEditListener[0]);
   }

   protected void _postEdit(UndoableEdit var1) {
      UndoableEditEvent var2 = new UndoableEditEvent(this.realSource, var1);
      Enumeration var3 = ((Vector)this.listeners.clone()).elements();

      while(var3.hasMoreElements()) {
         ((UndoableEditListener)var3.nextElement()).undoableEditHappened(var2);
      }

   }

   public synchronized void postEdit(UndoableEdit var1) {
      if (this.updateLevel == 0) {
         this._postEdit(var1);
      } else {
         this.compoundEdit.addEdit(var1);
      }

   }

   public int getUpdateLevel() {
      return this.updateLevel;
   }

   public synchronized void beginUpdate() {
      if (this.updateLevel == 0) {
         this.compoundEdit = this.createCompoundEdit();
      }

      ++this.updateLevel;
   }

   protected CompoundEdit createCompoundEdit() {
      return new CompoundEdit();
   }

   public synchronized void endUpdate() {
      --this.updateLevel;
      if (this.updateLevel == 0) {
         this.compoundEdit.end();
         this._postEdit(this.compoundEdit);
         this.compoundEdit = null;
      }

   }

   public String toString() {
      return super.toString() + " updateLevel: " + this.updateLevel + " listeners: " + this.listeners + " compoundEdit: " + this.compoundEdit;
   }
}
