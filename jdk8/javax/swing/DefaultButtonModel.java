package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;
import java.util.EventListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultButtonModel implements ButtonModel, Serializable {
   protected int stateMask = 0;
   protected String actionCommand = null;
   protected ButtonGroup group = null;
   protected int mnemonic = 0;
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   private boolean menuItem = false;
   public static final int ARMED = 1;
   public static final int SELECTED = 2;
   public static final int PRESSED = 4;
   public static final int ENABLED = 8;
   public static final int ROLLOVER = 16;

   public DefaultButtonModel() {
      this.stateMask = 0;
      this.setEnabled(true);
   }

   public void setActionCommand(String var1) {
      this.actionCommand = var1;
   }

   public String getActionCommand() {
      return this.actionCommand;
   }

   public boolean isArmed() {
      return (this.stateMask & 1) != 0;
   }

   public boolean isSelected() {
      return (this.stateMask & 2) != 0;
   }

   public boolean isEnabled() {
      return (this.stateMask & 8) != 0;
   }

   public boolean isPressed() {
      return (this.stateMask & 4) != 0;
   }

   public boolean isRollover() {
      return (this.stateMask & 16) != 0;
   }

   public void setArmed(boolean var1) {
      if (this.isMenuItem() && UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
         if (this.isArmed() == var1) {
            return;
         }
      } else if (this.isArmed() == var1 || !this.isEnabled()) {
         return;
      }

      if (var1) {
         this.stateMask |= 1;
      } else {
         this.stateMask &= -2;
      }

      this.fireStateChanged();
   }

   public void setEnabled(boolean var1) {
      if (this.isEnabled() != var1) {
         if (var1) {
            this.stateMask |= 8;
         } else {
            this.stateMask &= -9;
            this.stateMask &= -2;
            this.stateMask &= -5;
         }

         this.fireStateChanged();
      }
   }

   public void setSelected(boolean var1) {
      if (this.isSelected() != var1) {
         if (var1) {
            this.stateMask |= 2;
         } else {
            this.stateMask &= -3;
         }

         this.fireItemStateChanged(new ItemEvent(this, 701, this, var1 ? 1 : 2));
         this.fireStateChanged();
      }
   }

   public void setPressed(boolean var1) {
      if (this.isPressed() != var1 && this.isEnabled()) {
         if (var1) {
            this.stateMask |= 4;
         } else {
            this.stateMask &= -5;
         }

         if (!this.isPressed() && this.isArmed()) {
            int var2 = 0;
            AWTEvent var3 = EventQueue.getCurrentEvent();
            if (var3 instanceof InputEvent) {
               var2 = ((InputEvent)var3).getModifiers();
            } else if (var3 instanceof ActionEvent) {
               var2 = ((ActionEvent)var3).getModifiers();
            }

            this.fireActionPerformed(new ActionEvent(this, 1001, this.getActionCommand(), EventQueue.getMostRecentEventTime(), var2));
         }

         this.fireStateChanged();
      }
   }

   public void setRollover(boolean var1) {
      if (this.isRollover() != var1 && this.isEnabled()) {
         if (var1) {
            this.stateMask |= 16;
         } else {
            this.stateMask &= -17;
         }

         this.fireStateChanged();
      }
   }

   public void setMnemonic(int var1) {
      this.mnemonic = var1;
      this.fireStateChanged();
   }

   public int getMnemonic() {
      return this.mnemonic;
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

   public void addActionListener(ActionListener var1) {
      this.listenerList.add(ActionListener.class, var1);
   }

   public void removeActionListener(ActionListener var1) {
      this.listenerList.remove(ActionListener.class, var1);
   }

   public ActionListener[] getActionListeners() {
      return (ActionListener[])this.listenerList.getListeners(ActionListener.class);
   }

   protected void fireActionPerformed(ActionEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == ActionListener.class) {
            ((ActionListener)var2[var3 + 1]).actionPerformed(var1);
         }
      }

   }

   public void addItemListener(ItemListener var1) {
      this.listenerList.add(ItemListener.class, var1);
   }

   public void removeItemListener(ItemListener var1) {
      this.listenerList.remove(ItemListener.class, var1);
   }

   public ItemListener[] getItemListeners() {
      return (ItemListener[])this.listenerList.getListeners(ItemListener.class);
   }

   protected void fireItemStateChanged(ItemEvent var1) {
      Object[] var2 = this.listenerList.getListenerList();

      for(int var3 = var2.length - 2; var3 >= 0; var3 -= 2) {
         if (var2[var3] == ItemListener.class) {
            ((ItemListener)var2[var3 + 1]).itemStateChanged(var1);
         }
      }

   }

   public <T extends EventListener> T[] getListeners(Class<T> var1) {
      return this.listenerList.getListeners(var1);
   }

   public Object[] getSelectedObjects() {
      return null;
   }

   public void setGroup(ButtonGroup var1) {
      this.group = var1;
   }

   public ButtonGroup getGroup() {
      return this.group;
   }

   boolean isMenuItem() {
      return this.menuItem;
   }

   void setMenuItem(boolean var1) {
      this.menuItem = var1;
   }
}
