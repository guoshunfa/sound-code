package javax.swing;

import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultSingleSelectionModel implements SingleSelectionModel, Serializable {
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   private int index = -1;

   public int getSelectedIndex() {
      return this.index;
   }

   public void setSelectedIndex(int var1) {
      if (this.index != var1) {
         this.index = var1;
         this.fireStateChanged();
      }

   }

   public void clearSelection() {
      this.setSelectedIndex(-1);
   }

   public boolean isSelected() {
      boolean var1 = false;
      if (this.getSelectedIndex() != -1) {
         var1 = true;
      }

      return var1;
   }

   public void addChangeListener(ChangeListener var1) {
      this.listenerList.add(ChangeListener.class, var1);
   }

   public void removeChangeListener(ChangeListener var1) {
      this.listenerList.remove(ChangeListener.class, var1);
   }

   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class);
   }

   protected void fireStateChanged() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == ChangeListener.class) {
            if (this.changeEvent == null) {
               this.changeEvent = new ChangeEvent(this);
            }

            ((ChangeListener)var1[var2 + 1]).stateChanged(this.changeEvent);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }
}
