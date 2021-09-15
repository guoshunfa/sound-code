package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class JCheckBoxMenuItem extends JMenuItem implements SwingConstants, Accessible {
   private static final String uiClassID = "CheckBoxMenuItemUI";

   public JCheckBoxMenuItem() {
      this((String)null, (Icon)null, false);
   }

   public JCheckBoxMenuItem(Icon var1) {
      this((String)null, var1, false);
   }

   public JCheckBoxMenuItem(String var1) {
      this(var1, (Icon)null, false);
   }

   public JCheckBoxMenuItem(Action var1) {
      this();
      this.setAction(var1);
   }

   public JCheckBoxMenuItem(String var1, Icon var2) {
      this(var1, var2, false);
   }

   public JCheckBoxMenuItem(String var1, boolean var2) {
      this(var1, (Icon)null, var2);
   }

   public JCheckBoxMenuItem(String var1, Icon var2, boolean var3) {
      super(var1, var2);
      this.setModel(new JToggleButton.ToggleButtonModel());
      this.setSelected(var3);
      this.setFocusable(false);
   }

   public String getUIClassID() {
      return "CheckBoxMenuItemUI";
   }

   public boolean getState() {
      return this.isSelected();
   }

   public synchronized void setState(boolean var1) {
      this.setSelected(var1);
   }

   public Object[] getSelectedObjects() {
      if (!this.isSelected()) {
         return null;
      } else {
         Object[] var1 = new Object[]{this.getText()};
         return var1;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("CheckBoxMenuItemUI")) {
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

   boolean shouldUpdateSelectedStateFromAction() {
      return true;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JCheckBoxMenuItem.AccessibleJCheckBoxMenuItem();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJCheckBoxMenuItem extends JMenuItem.AccessibleJMenuItem {
      protected AccessibleJCheckBoxMenuItem() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.CHECK_BOX;
      }
   }
}
