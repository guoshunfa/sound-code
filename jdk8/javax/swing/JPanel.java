package javax.swing;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.PanelUI;

public class JPanel extends JComponent implements Accessible {
   private static final String uiClassID = "PanelUI";

   public JPanel(LayoutManager var1, boolean var2) {
      this.setLayout(var1);
      this.setDoubleBuffered(var2);
      this.setUIProperty("opaque", Boolean.TRUE);
      this.updateUI();
   }

   public JPanel(LayoutManager var1) {
      this(var1, true);
   }

   public JPanel(boolean var1) {
      this(new FlowLayout(), var1);
   }

   public JPanel() {
      this(true);
   }

   public void updateUI() {
      this.setUI((PanelUI)UIManager.getUI(this));
   }

   public PanelUI getUI() {
      return (PanelUI)this.ui;
   }

   public void setUI(PanelUI var1) {
      super.setUI(var1);
   }

   public String getUIClassID() {
      return "PanelUI";
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("PanelUI")) {
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
         this.accessibleContext = new JPanel.AccessibleJPanel();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJPanel extends JComponent.AccessibleJComponent {
      protected AccessibleJPanel() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.PANEL;
      }
   }
}
