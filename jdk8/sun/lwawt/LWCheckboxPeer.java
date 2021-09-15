package sun.lwawt;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.peer.CheckboxPeer;
import java.beans.Transient;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

final class LWCheckboxPeer extends LWComponentPeer<Checkbox, LWCheckboxPeer.CheckboxDelegate> implements CheckboxPeer, ItemListener {
   LWCheckboxPeer(Checkbox var1, PlatformComponent var2) {
      super(var1, var2);
   }

   LWCheckboxPeer.CheckboxDelegate createDelegate() {
      return new LWCheckboxPeer.CheckboxDelegate();
   }

   Component getDelegateFocusOwner() {
      return ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).getCurrentButton();
   }

   void initializeImpl() {
      super.initializeImpl();
      this.setLabel(((Checkbox)this.getTarget()).getLabel());
      this.setState(((Checkbox)this.getTarget()).getState());
      this.setCheckboxGroup(((Checkbox)this.getTarget()).getCheckboxGroup());
   }

   public void itemStateChanged(final ItemEvent var1) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            boolean var1x = true;
            CheckboxGroup var2 = ((Checkbox)LWCheckboxPeer.this.getTarget()).getCheckboxGroup();
            if (var2 != null) {
               if (var1.getStateChange() == 1) {
                  if (var2.getSelectedCheckbox() != LWCheckboxPeer.this.getTarget()) {
                     var2.setSelectedCheckbox((Checkbox)LWCheckboxPeer.this.getTarget());
                  } else {
                     var1x = false;
                  }
               } else {
                  var1x = false;
                  if (var2.getSelectedCheckbox() == LWCheckboxPeer.this.getTarget()) {
                     ((Checkbox)LWCheckboxPeer.this.getTarget()).setState(true);
                  }
               }
            } else {
               ((Checkbox)LWCheckboxPeer.this.getTarget()).setState(var1.getStateChange() == 1);
            }

            if (var1x) {
               LWCheckboxPeer.this.postEvent(new ItemEvent((ItemSelectable)LWCheckboxPeer.this.getTarget(), 701, ((Checkbox)LWCheckboxPeer.this.getTarget()).getLabel(), var1.getStateChange()));
            }

         }
      });
   }

   public void setCheckboxGroup(CheckboxGroup var1) {
      synchronized(this.getDelegateLock()) {
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).getCurrentButton().removeItemListener(this);
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).setRadioButton(var1 != null);
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).getCurrentButton().addItemListener(this);
      }

      this.repaintPeer();
   }

   public void setLabel(String var1) {
      synchronized(this.getDelegateLock()) {
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).setText(var1);
      }
   }

   public void setState(boolean var1) {
      synchronized(this.getDelegateLock()) {
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).getCurrentButton().removeItemListener(this);
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).setSelected(var1);
         ((LWCheckboxPeer.CheckboxDelegate)this.getDelegate()).getCurrentButton().addItemListener(this);
      }

      this.repaintPeer();
   }

   public boolean isFocusable() {
      return true;
   }

   final class CheckboxDelegate extends JComponent {
      private final JCheckBox cb = new JCheckBox() {
         public boolean hasFocus() {
            return ((Checkbox)LWCheckboxPeer.this.getTarget()).hasFocus();
         }
      };
      private final JRadioButton rb = new JRadioButton() {
         public boolean hasFocus() {
            return ((Checkbox)LWCheckboxPeer.this.getTarget()).hasFocus();
         }
      };

      CheckboxDelegate() {
         this.setLayout((LayoutManager)null);
         this.setRadioButton(false);
         this.add(this.rb);
         this.add(this.cb);
      }

      public void setEnabled(boolean var1) {
         super.setEnabled(var1);
         this.rb.setEnabled(var1);
         this.cb.setEnabled(var1);
      }

      public void setOpaque(boolean var1) {
         super.setOpaque(var1);
         this.rb.setOpaque(var1);
         this.cb.setOpaque(var1);
      }

      /** @deprecated */
      @Deprecated
      public void reshape(int var1, int var2, int var3, int var4) {
         super.reshape(var1, var2, var3, var4);
         this.cb.setBounds(0, 0, var3, var4);
         this.rb.setBounds(0, 0, var3, var4);
      }

      public Dimension getPreferredSize() {
         return this.getCurrentButton().getPreferredSize();
      }

      @Transient
      public Dimension getMinimumSize() {
         return this.getCurrentButton().getMinimumSize();
      }

      void setRadioButton(boolean var1) {
         this.rb.setVisible(var1);
         this.cb.setVisible(!var1);
      }

      @Transient
      JToggleButton getCurrentButton() {
         return (JToggleButton)(this.cb.isVisible() ? this.cb : this.rb);
      }

      void setText(String var1) {
         this.cb.setText(var1);
         this.rb.setText(var1);
      }

      void setSelected(boolean var1) {
         this.cb.setSelected(var1);
         this.rb.setSelected(var1);
      }
   }
}
