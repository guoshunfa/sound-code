package javax.swing;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;

public class JButton extends AbstractButton implements Accessible {
   private static final String uiClassID = "ButtonUI";

   public JButton() {
      this((String)null, (Icon)null);
   }

   public JButton(Icon var1) {
      this((String)null, var1);
   }

   @ConstructorProperties({"text"})
   public JButton(String var1) {
      this(var1, (Icon)null);
   }

   public JButton(Action var1) {
      this();
      this.setAction(var1);
   }

   public JButton(String var1, Icon var2) {
      this.setModel(new DefaultButtonModel());
      this.init(var1, var2);
   }

   public void updateUI() {
      this.setUI((ButtonUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ButtonUI";
   }

   public boolean isDefaultButton() {
      JRootPane var1 = SwingUtilities.getRootPane(this);
      if (var1 != null) {
         return var1.getDefaultButton() == this;
      } else {
         return false;
      }
   }

   public boolean isDefaultCapable() {
      return this.defaultCapable;
   }

   public void setDefaultCapable(boolean var1) {
      boolean var2 = this.defaultCapable;
      this.defaultCapable = var1;
      this.firePropertyChange("defaultCapable", var2, var1);
   }

   public void removeNotify() {
      JRootPane var1 = SwingUtilities.getRootPane(this);
      if (var1 != null && var1.getDefaultButton() == this) {
         var1.setDefaultButton((JButton)null);
      }

      super.removeNotify();
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ButtonUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.defaultCapable ? "true" : "false";
      return super.paramString() + ",defaultCapable=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JButton.AccessibleJButton();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJButton extends AbstractButton.AccessibleAbstractButton {
      protected AccessibleJButton() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PUSH_BUTTON;
      }
   }
}
