package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class JRadioButtonMenuItem extends JMenuItem implements Accessible {
   private static final String uiClassID = "RadioButtonMenuItemUI";

   public JRadioButtonMenuItem() {
      this((String)null, (Icon)null, false);
   }

   public JRadioButtonMenuItem(Icon var1) {
      this((String)null, var1, false);
   }

   public JRadioButtonMenuItem(String var1) {
      this(var1, (Icon)null, false);
   }

   public JRadioButtonMenuItem(Action var1) {
      this();
      this.setAction(var1);
   }

   public JRadioButtonMenuItem(String var1, Icon var2) {
      this(var1, var2, false);
   }

   public JRadioButtonMenuItem(String var1, boolean var2) {
      this(var1);
      this.setSelected(var2);
   }

   public JRadioButtonMenuItem(Icon var1, boolean var2) {
      this((String)null, var1, var2);
   }

   public JRadioButtonMenuItem(String var1, Icon var2, boolean var3) {
      super(var1, var2);
      this.setModel(new JToggleButton.ToggleButtonModel());
      this.setSelected(var3);
      this.setFocusable(false);
   }

   public String getUIClassID() {
      return "RadioButtonMenuItemUI";
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("RadioButtonMenuItemUI")) {
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
         this.accessibleContext = new JRadioButtonMenuItem.AccessibleJRadioButtonMenuItem();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJRadioButtonMenuItem extends JMenuItem.AccessibleJMenuItem {
      protected AccessibleJRadioButtonMenuItem() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.RADIO_BUTTON;
      }
   }
}
