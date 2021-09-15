package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;

public class JRadioButton extends JToggleButton implements Accessible {
   private static final String uiClassID = "RadioButtonUI";

   public JRadioButton() {
      this((String)null, (Icon)null, false);
   }

   public JRadioButton(Icon var1) {
      this((String)null, var1, false);
   }

   public JRadioButton(Action var1) {
      this();
      this.setAction(var1);
   }

   public JRadioButton(Icon var1, boolean var2) {
      this((String)null, var1, var2);
   }

   public JRadioButton(String var1) {
      this(var1, (Icon)null, false);
   }

   public JRadioButton(String var1, boolean var2) {
      this(var1, (Icon)null, var2);
   }

   public JRadioButton(String var1, Icon var2) {
      this(var1, var2, false);
   }

   public JRadioButton(String var1, Icon var2, boolean var3) {
      super(var1, var2, var3);
      this.setBorderPainted(false);
      this.setHorizontalAlignment(10);
   }

   public void updateUI() {
      this.setUI((ButtonUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "RadioButtonUI";
   }

   void setIconFromAction(Action var1) {
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("RadioButtonUI")) {
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
         this.accessibleContext = new JRadioButton.AccessibleJRadioButton();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJRadioButton extends JToggleButton.AccessibleJToggleButton {
      protected AccessibleJRadioButton() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.RADIO_BUTTON;
      }
   }
}
