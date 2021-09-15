package javax.swing;

import java.io.Serializable;
import java.util.Vector;

public class DefaultComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable {
   Vector<E> objects;
   Object selectedObject;

   public DefaultComboBoxModel() {
      this.objects = new Vector();
   }

   public DefaultComboBoxModel(E[] var1) {
      this.objects = new Vector(var1.length);
      int var2 = 0;

      for(int var3 = var1.length; var2 < var3; ++var2) {
         this.objects.addElement(var1[var2]);
      }

      if (this.getSize() > 0) {
         this.selectedObject = this.getElementAt(0);
      }

   }

   public DefaultComboBoxModel(Vector<E> var1) {
      this.objects = var1;
      if (this.getSize() > 0) {
         this.selectedObject = this.getElementAt(0);
      }

   }

   public void setSelectedItem(Object var1) {
      if (this.selectedObject != null && !this.selectedObject.equals(var1) || this.selectedObject == null && var1 != null) {
         this.selectedObject = var1;
         this.fireContentsChanged(this, -1, -1);
      }

   }

   public Object getSelectedItem() {
      return this.selectedObject;
   }

   public int getSize() {
      return this.objects.size();
   }

   public E getElementAt(int var1) {
      return var1 >= 0 && var1 < this.objects.size() ? this.objects.elementAt(var1) : null;
   }

   public int getIndexOf(Object var1) {
      return this.objects.indexOf(var1);
   }

   public void addElement(E var1) {
      this.objects.addElement(var1);
      this.fireIntervalAdded(this, this.objects.size() - 1, this.objects.size() - 1);
      if (this.objects.size() == 1 && this.selectedObject == null && var1 != null) {
         this.setSelectedItem(var1);
      }

   }

   public void insertElementAt(E var1, int var2) {
      this.objects.insertElementAt(var1, var2);
      this.fireIntervalAdded(this, var2, var2);
   }

   public void removeElementAt(int var1) {
      if (this.getElementAt(var1) == this.selectedObject) {
         if (var1 == 0) {
            this.setSelectedItem(this.getSize() == 1 ? null : this.getElementAt(var1 + 1));
         } else {
            this.setSelectedItem(this.getElementAt(var1 - 1));
         }
      }

      this.objects.removeElementAt(var1);
      this.fireIntervalRemoved(this, var1, var1);
   }

   public void removeElement(Object var1) {
      int var2 = this.objects.indexOf(var1);
      if (var2 != -1) {
         this.removeElementAt(var2);
      }

   }

   public void removeAllElements() {
      if (this.objects.size() > 0) {
         byte var1 = 0;
         int var2 = this.objects.size() - 1;
         this.objects.removeAllElements();
         this.selectedObject = null;
         this.fireIntervalRemoved(this, var1, var2);
      } else {
         this.selectedObject = null;
      }

   }
}
