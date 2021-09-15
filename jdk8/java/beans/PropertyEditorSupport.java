package java.beans;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

public class PropertyEditorSupport implements PropertyEditor {
   private Object value;
   private Object source;
   private Vector<PropertyChangeListener> listeners;

   public PropertyEditorSupport() {
      this.setSource(this);
   }

   public PropertyEditorSupport(Object var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.setSource(var1);
      }
   }

   public Object getSource() {
      return this.source;
   }

   public void setSource(Object var1) {
      this.source = var1;
   }

   public void setValue(Object var1) {
      this.value = var1;
      this.firePropertyChange();
   }

   public Object getValue() {
      return this.value;
   }

   public boolean isPaintable() {
      return false;
   }

   public void paintValue(Graphics var1, Rectangle var2) {
   }

   public String getJavaInitializationString() {
      return "???";
   }

   public String getAsText() {
      return this.value != null ? this.value.toString() : null;
   }

   public void setAsText(String var1) throws IllegalArgumentException {
      if (this.value instanceof String) {
         this.setValue(var1);
      } else {
         throw new IllegalArgumentException(var1);
      }
   }

   public String[] getTags() {
      return null;
   }

   public Component getCustomEditor() {
      return null;
   }

   public boolean supportsCustomEditor() {
      return false;
   }

   public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.listeners == null) {
         this.listeners = new Vector();
      }

      this.listeners.addElement(var1);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.listeners != null) {
         this.listeners.removeElement(var1);
      }
   }

   public void firePropertyChange() {
      Vector var1;
      synchronized(this) {
         if (this.listeners == null) {
            return;
         }

         var1 = this.unsafeClone(this.listeners);
      }

      PropertyChangeEvent var2 = new PropertyChangeEvent(this.source, (String)null, (Object)null, (Object)null);

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         PropertyChangeListener var4 = (PropertyChangeListener)var1.elementAt(var3);
         var4.propertyChange(var2);
      }

   }

   private <T> Vector<T> unsafeClone(Vector<T> var1) {
      return (Vector)var1.clone();
   }
}
