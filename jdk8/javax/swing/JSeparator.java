package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.SeparatorUI;

public class JSeparator extends JComponent implements SwingConstants, Accessible {
   private static final String uiClassID = "SeparatorUI";
   private int orientation;

   public JSeparator() {
      this(0);
   }

   public JSeparator(int var1) {
      this.orientation = 0;
      this.checkOrientation(var1);
      this.orientation = var1;
      this.setFocusable(false);
      this.updateUI();
   }

   public SeparatorUI getUI() {
      return (SeparatorUI)this.ui;
   }

   public void setUI(SeparatorUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((SeparatorUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "SeparatorUI";
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("SeparatorUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   public int getOrientation() {
      return this.orientation;
   }

   public void setOrientation(int var1) {
      if (this.orientation != var1) {
         int var2 = this.orientation;
         this.checkOrientation(var1);
         this.orientation = var1;
         this.firePropertyChange("orientation", var2, var1);
         this.revalidate();
         this.repaint();
      }
   }

   private void checkOrientation(int var1) {
      switch(var1) {
      case 0:
      case 1:
         return;
      default:
         throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
      }
   }

   protected String paramString() {
      String var1 = this.orientation == 0 ? "HORIZONTAL" : "VERTICAL";
      return super.paramString() + ",orientation=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JSeparator.AccessibleJSeparator();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJSeparator extends JComponent.AccessibleJComponent {
      protected AccessibleJSeparator() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.SEPARATOR;
      }
   }
}
