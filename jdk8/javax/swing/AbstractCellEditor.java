package javax.swing;

import java.io.Serializable;
import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;

public abstract class AbstractCellEditor implements CellEditor, Serializable {
   protected EventListenerList listenerList = new EventListenerList();
   protected transient ChangeEvent changeEvent = null;

   public boolean isCellEditable(EventObject var1) {
      return true;
   }

   public boolean shouldSelectCell(EventObject var1) {
      return true;
   }

   public boolean stopCellEditing() {
      this.fireEditingStopped();
      return true;
   }

   public void cancelCellEditing() {
      this.fireEditingCanceled();
   }

   public void addCellEditorListener(CellEditorListener var1) {
      this.listenerList.add(CellEditorListener.class, var1);
   }

   public void removeCellEditorListener(CellEditorListener var1) {
      this.listenerList.remove(CellEditorListener.class, var1);
   }

   public CellEditorListener[] getCellEditorListeners() {
      return (CellEditorListener[])this.listenerList.getListeners(CellEditorListener.class);
   }

   protected void fireEditingStopped() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == CellEditorListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((CellEditorListener)var1[var2 + 1]).editingStopped(this.changeEvent);
         }
      }

   }

   protected void fireEditingCanceled() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == CellEditorListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((CellEditorListener)var1[var2 + 1]).editingCanceled(this.changeEvent);
         }
      }

   }
}
