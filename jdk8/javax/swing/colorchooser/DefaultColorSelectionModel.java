package javax.swing.colorchooser;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultColorSelectionModel implements ColorSelectionModel, Serializable {
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   private Color selectedColor;

   public DefaultColorSelectionModel() {
      this.selectedColor = Color.white;
   }

   public DefaultColorSelectionModel(Color var1) {
      this.selectedColor = var1;
   }

   public Color getSelectedColor() {
      return this.selectedColor;
   }

   public void setSelectedColor(Color var1) {
      if (var1 != null && !this.selectedColor.equals(var1)) {
         this.selectedColor = var1;
         this.fireStateChanged();
      }

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
}
