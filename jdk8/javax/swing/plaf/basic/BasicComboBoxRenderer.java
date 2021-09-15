package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class BasicComboBoxRenderer extends JLabel implements ListCellRenderer, Serializable {
   protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
   private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

   public BasicComboBoxRenderer() {
      this.setOpaque(true);
      this.setBorder(getNoFocusBorder());
   }

   private static Border getNoFocusBorder() {
      return System.getSecurityManager() != null ? SAFE_NO_FOCUS_BORDER : noFocusBorder;
   }

   public Dimension getPreferredSize() {
      Dimension var1;
      if (this.getText() != null && !this.getText().equals("")) {
         var1 = super.getPreferredSize();
      } else {
         this.setText(" ");
         var1 = super.getPreferredSize();
         this.setText("");
      }

      return var1;
   }

   public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
      if (var4) {
         this.setBackground(var1.getSelectionBackground());
         this.setForeground(var1.getSelectionForeground());
      } else {
         this.setBackground(var1.getBackground());
         this.setForeground(var1.getForeground());
      }

      this.setFont(var1.getFont());
      if (var2 instanceof Icon) {
         this.setIcon((Icon)var2);
      } else {
         this.setText(var2 == null ? "" : var2.toString());
      }

      return this;
   }

   public static class UIResource extends BasicComboBoxRenderer implements javax.swing.plaf.UIResource {
   }
}
