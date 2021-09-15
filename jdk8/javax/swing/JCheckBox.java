package javax.swing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ButtonUI;

public class JCheckBox extends JToggleButton implements Accessible {
   public static final String BORDER_PAINTED_FLAT_CHANGED_PROPERTY = "borderPaintedFlat";
   private boolean flat;
   private static final String uiClassID = "CheckBoxUI";

   public JCheckBox() {
      this((String)null, (Icon)null, false);
   }

   public JCheckBox(Icon var1) {
      this((String)null, var1, false);
   }

   public JCheckBox(Icon var1, boolean var2) {
      this((String)null, var1, var2);
   }

   public JCheckBox(String var1) {
      this(var1, (Icon)null, false);
   }

   public JCheckBox(Action var1) {
      this();
      this.setAction(var1);
   }

   public JCheckBox(String var1, boolean var2) {
      this(var1, (Icon)null, var2);
   }

   public JCheckBox(String var1, Icon var2) {
      this(var1, var2, false);
   }

   public JCheckBox(String var1, Icon var2, boolean var3) {
      super(var1, var2, var3);
      this.flat = false;
      this.setUIProperty("borderPainted", Boolean.FALSE);
      this.setHorizontalAlignment(10);
   }

   public void setBorderPaintedFlat(boolean var1) {
      boolean var2 = this.flat;
      this.flat = var1;
      this.firePropertyChange("borderPaintedFlat", var2, this.flat);
      if (var1 != var2) {
         this.revalidate();
         this.repaint();
      }

   }

   public boolean isBorderPaintedFlat() {
      return this.flat;
   }

   public void updateUI() {
      this.setUI((ButtonUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "CheckBoxUI";
   }

   void setIconFromAction(Action var1) {
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("CheckBoxUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      if (this.getUIClassID().equals("CheckBoxUI")) {
         this.updateUI();
      }

   }

   protected String paramString() {
      return super.paramString();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JCheckBox.AccessibleJCheckBox();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJCheckBox extends JToggleButton.AccessibleJToggleButton {
      protected AccessibleJCheckBox() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.CHECK_BOX;
      }
   }
}
