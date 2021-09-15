package javax.swing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.swing.plaf.ButtonUI;

public class JToggleButton extends AbstractButton implements Accessible {
   private static final String uiClassID = "ToggleButtonUI";

   public JToggleButton() {
      this((String)null, (Icon)null, false);
   }

   public JToggleButton(Icon var1) {
      this((String)null, var1, false);
   }

   public JToggleButton(Icon var1, boolean var2) {
      this((String)null, var1, var2);
   }

   public JToggleButton(String var1) {
      this(var1, (Icon)null, false);
   }

   public JToggleButton(String var1, boolean var2) {
      this(var1, (Icon)null, var2);
   }

   public JToggleButton(Action var1) {
      this();
      this.setAction(var1);
   }

   public JToggleButton(String var1, Icon var2) {
      this(var1, var2, false);
   }

   public JToggleButton(String var1, Icon var2, boolean var3) {
      this.setModel(new JToggleButton.ToggleButtonModel());
      this.model.setSelected(var3);
      this.init(var1, var2);
   }

   public void updateUI() {
      this.setUI((ButtonUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ToggleButtonUI";
   }

   boolean shouldUpdateSelectedStateFromAction() {
      return true;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ToggleButtonUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      return super.paramString();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JToggleButton.AccessibleJToggleButton();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJToggleButton extends AbstractButton.AccessibleAbstractButton implements ItemListener {
      public AccessibleJToggleButton() {
         super();
         JToggleButton.this.addItemListener(this);
      }

      public void itemStateChanged(ItemEvent var1) {
         JToggleButton var2 = (JToggleButton)var1.getSource();
         if (JToggleButton.this.accessibleContext != null) {
            if (var2.isSelected()) {
               JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", (Object)null, AccessibleState.CHECKED);
            } else {
               JToggleButton.this.accessibleContext.firePropertyChange("AccessibleState", AccessibleState.CHECKED, (Object)null);
            }
         }

      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TOGGLE_BUTTON;
      }
   }

   public static class ToggleButtonModel extends DefaultButtonModel {
      public boolean isSelected() {
         return (this.stateMask & 2) != 0;
      }

      public void setSelected(boolean var1) {
         ButtonGroup var2 = this.getGroup();
         if (var2 != null) {
            var2.setSelected(this, var1);
            var1 = var2.isSelected(this);
         }

         if (this.isSelected() != var1) {
            if (var1) {
               this.stateMask |= 2;
            } else {
               this.stateMask &= -3;
            }

            this.fireStateChanged();
            this.fireItemStateChanged(new ItemEvent(this, 701, this, this.isSelected() ? 1 : 2));
         }
      }

      public void setPressed(boolean var1) {
         if (this.isPressed() != var1 && this.isEnabled()) {
            if (!var1 && this.isArmed()) {
               this.setSelected(!this.isSelected());
            }

            if (var1) {
               this.stateMask |= 4;
            } else {
               this.stateMask &= -5;
            }

            this.fireStateChanged();
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

         }
      }
   }
}
