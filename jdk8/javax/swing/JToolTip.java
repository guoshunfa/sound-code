package javax.swing;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.ToolTipUI;

public class JToolTip extends JComponent implements Accessible {
   private static final String uiClassID = "ToolTipUI";
   String tipText;
   JComponent component;

   public JToolTip() {
      this.setOpaque(true);
      this.updateUI();
   }

   public ToolTipUI getUI() {
      return (ToolTipUI)this.ui;
   }

   public void updateUI() {
      this.setUI((ToolTipUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "ToolTipUI";
   }

   public void setTipText(String var1) {
      String var2 = this.tipText;
      this.tipText = var1;
      this.firePropertyChange("tiptext", var2, var1);
      if (!Objects.equals(var2, var1)) {
         this.revalidate();
         this.repaint();
      }

   }

   public String getTipText() {
      return this.tipText;
   }

   public void setComponent(JComponent var1) {
      JComponent var2 = this.component;
      this.component = var1;
      this.firePropertyChange("component", var2, var1);
   }

   public JComponent getComponent() {
      return this.component;
   }

   boolean alwaysOnTop() {
      return true;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("ToolTipUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      String var1 = this.tipText != null ? this.tipText : "";
      return super.paramString() + ",tipText=" + var1;
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JToolTip.AccessibleJToolTip();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJToolTip extends JComponent.AccessibleJComponent {
      protected AccessibleJToolTip() {
         super();
      }

      public String getAccessibleDescription() {
         String var1 = this.accessibleDescription;
         if (var1 == null) {
            var1 = (String)JToolTip.this.getClientProperty("AccessibleDescription");
         }

         if (var1 == null) {
            var1 = JToolTip.this.getTipText();
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.TOOL_TIP;
      }
   }
}
