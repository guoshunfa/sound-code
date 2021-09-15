package javax.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.swing.event.SwingPropertyChangeSupport;
import sun.security.action.GetPropertyAction;

public abstract class AbstractAction implements Action, Cloneable, Serializable {
   private static Boolean RECONFIGURE_ON_NULL;
   protected boolean enabled;
   private transient ArrayTable arrayTable;
   protected SwingPropertyChangeSupport changeSupport;

   static boolean shouldReconfigure(PropertyChangeEvent var0) {
      if (var0.getPropertyName() == null) {
         Class var1 = AbstractAction.class;
         synchronized(AbstractAction.class) {
            if (RECONFIGURE_ON_NULL == null) {
               RECONFIGURE_ON_NULL = Boolean.valueOf((String)AccessController.doPrivileged((PrivilegedAction)(new GetPropertyAction("swing.actions.reconfigureOnNull", "false"))));
            }

            return RECONFIGURE_ON_NULL;
         }
      } else {
         return false;
      }
   }

   static void setEnabledFromAction(JComponent var0, Action var1) {
      var0.setEnabled(var1 != null ? var1.isEnabled() : true);
   }

   static void setToolTipTextFromAction(JComponent var0, Action var1) {
      var0.setToolTipText(var1 != null ? (String)var1.getValue("ShortDescription") : null);
   }

   static boolean hasSelectedKey(Action var0) {
      return var0 != null && var0.getValue("SwingSelectedKey") != null;
   }

   static boolean isSelected(Action var0) {
      return Boolean.TRUE.equals(var0.getValue("SwingSelectedKey"));
   }

   public AbstractAction() {
      this.enabled = true;
   }

   public AbstractAction(String var1) {
      this.enabled = true;
      this.putValue("Name", var1);
   }

   public AbstractAction(String var1, Icon var2) {
      this(var1);
      this.putValue("SmallIcon", var2);
   }

   public Object getValue(String var1) {
      if (var1 == "enabled") {
         return this.enabled;
      } else {
         return this.arrayTable == null ? null : this.arrayTable.get(var1);
      }
   }

   public void putValue(String var1, Object var2) {
      Object var3 = null;
      if (var1 == "enabled") {
         if (var2 == null || !(var2 instanceof Boolean)) {
            var2 = false;
         }

         var3 = this.enabled;
         this.enabled = (Boolean)var2;
      } else {
         if (this.arrayTable == null) {
            this.arrayTable = new ArrayTable();
         }

         if (this.arrayTable.containsKey(var1)) {
            var3 = this.arrayTable.get(var1);
         }

         if (var2 == null) {
            this.arrayTable.remove(var1);
         } else {
            this.arrayTable.put(var1, var2);
         }
      }

      this.firePropertyChange(var1, var3, var2);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean var1) {
      boolean var2 = this.enabled;
      if (var2 != var1) {
         this.enabled = var1;
         this.firePropertyChange("enabled", var2, var1);
      }

   }

   public Object[] getKeys() {
      if (this.arrayTable == null) {
         return null;
      } else {
         Object[] var1 = new Object[this.arrayTable.size()];
         this.arrayTable.getKeys(var1);
         return var1;
      }
   }

   protected void firePropertyChange(String var1, Object var2, Object var3) {
      if (this.changeSupport != null && (var2 == null || var3 == null || !var2.equals(var3))) {
         this.changeSupport.firePropertyChange(var1, var2, var3);
      }
   }

   public synchronized void addPropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport == null) {
         this.changeSupport = new SwingPropertyChangeSupport(this);
      }

      this.changeSupport.addPropertyChangeListener(var1);
   }

   public synchronized void removePropertyChangeListener(PropertyChangeListener var1) {
      if (this.changeSupport != null) {
         this.changeSupport.removePropertyChangeListener(var1);
      }
   }

   public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
      return this.changeSupport == null ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners();
   }

   protected Object clone() throws CloneNotSupportedException {
      AbstractAction var1 = (AbstractAction)super.clone();
      synchronized(this) {
         if (this.arrayTable != null) {
            var1.arrayTable = (ArrayTable)this.arrayTable.clone();
         }

         return var1;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      ArrayTable.writeArrayTable(var1, this.arrayTable);
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultReadObject();

      for(int var2 = var1.readInt() - 1; var2 >= 0; --var2) {
         this.putValue((String)var1.readObject(), var1.readObject());
      }

   }
}
