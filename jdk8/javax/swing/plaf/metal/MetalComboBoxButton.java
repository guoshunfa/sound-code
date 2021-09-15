package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class MetalComboBoxButton extends JButton {
   protected JComboBox comboBox;
   protected JList listBox;
   protected CellRendererPane rendererPane;
   protected Icon comboIcon;
   protected boolean iconOnly;

   public final JComboBox getComboBox() {
      return this.comboBox;
   }

   public final void setComboBox(JComboBox var1) {
      this.comboBox = var1;
   }

   public final Icon getComboIcon() {
      return this.comboIcon;
   }

   public final void setComboIcon(Icon var1) {
      this.comboIcon = var1;
   }

   public final boolean isIconOnly() {
      return this.iconOnly;
   }

   public final void setIconOnly(boolean var1) {
      this.iconOnly = var1;
   }

   MetalComboBoxButton() {
      super("");
      this.iconOnly = false;
      DefaultButtonModel var1 = new DefaultButtonModel() {
         public void setArmed(boolean var1) {
            super.setArmed(this.isPressed() ? true : var1);
         }
      };
      this.setModel(var1);
   }

   public MetalComboBoxButton(JComboBox var1, Icon var2, CellRendererPane var3, JList var4) {
      this();
      this.comboBox = var1;
      this.comboIcon = var2;
      this.rendererPane = var3;
      this.listBox = var4;
      this.setEnabled(this.comboBox.isEnabled());
   }

   public MetalComboBoxButton(JComboBox var1, Icon var2, boolean var3, CellRendererPane var4, JList var5) {
      this(var1, var2, var4, var5);
      this.iconOnly = var3;
   }

   public boolean isFocusTraversable() {
      return false;
   }

   public void setEnabled(boolean var1) {
      super.setEnabled(var1);
      if (var1) {
         this.setBackground(this.comboBox.getBackground());
         this.setForeground(this.comboBox.getForeground());
      } else {
         this.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
         this.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      }

   }

   public void paintComponent(Graphics var1) {
      boolean var2 = MetalUtils.isLeftToRight(this.comboBox);
      super.paintComponent(var1);
      Insets var3 = this.getInsets();
      int var4 = this.getWidth() - (var3.left + var3.right);
      int var5 = this.getHeight() - (var3.top + var3.bottom);
      if (var5 > 0 && var4 > 0) {
         int var6 = var3.left;
         int var7 = var3.top;
         int var8 = var6 + (var4 - 1);
         int var9 = var7 + (var5 - 1);
         int var10 = 0;
         int var10000 = var2 ? var8 : var6;
         if (this.comboIcon != null) {
            var10 = this.comboIcon.getIconWidth();
            int var12 = this.comboIcon.getIconHeight();
            boolean var13 = false;
            int var11;
            int var18;
            if (this.iconOnly) {
               var11 = this.getWidth() / 2 - var10 / 2;
               var18 = this.getHeight() / 2 - var12 / 2;
            } else {
               if (var2) {
                  var11 = var6 + (var4 - 1) - var10;
               } else {
                  var11 = var6;
               }

               var18 = var7 + (var9 - var7) / 2 - var12 / 2;
            }

            this.comboIcon.paintIcon(this, var1, var11, var18);
            if (this.comboBox.hasFocus() && (!MetalLookAndFeel.usingOcean() || this.comboBox.isEditable())) {
               var1.setColor(MetalLookAndFeel.getFocusColor());
               var1.drawRect(var6 - 1, var7 - 1, var4 + 3, var5 + 1);
            }
         }

         if (!MetalLookAndFeel.usingOcean()) {
            if (!this.iconOnly && this.comboBox != null) {
               ListCellRenderer var17 = this.comboBox.getRenderer();
               boolean var14 = this.getModel().isPressed();
               Component var19 = var17.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, var14, false);
               var19.setFont(this.rendererPane.getFont());
               if (this.model.isArmed() && this.model.isPressed()) {
                  if (this.isOpaque()) {
                     var19.setBackground(UIManager.getColor("Button.select"));
                  }

                  var19.setForeground(this.comboBox.getForeground());
               } else if (!this.comboBox.isEnabled()) {
                  if (this.isOpaque()) {
                     var19.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
                  }

                  var19.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
               } else {
                  var19.setForeground(this.comboBox.getForeground());
                  var19.setBackground(this.comboBox.getBackground());
               }

               int var15 = var4 - (var3.right + var10);
               boolean var16 = false;
               if (var19 instanceof JPanel) {
                  var16 = true;
               }

               if (var2) {
                  this.rendererPane.paintComponent(var1, var19, this, var6, var7, var15, var5, var16);
               } else {
                  this.rendererPane.paintComponent(var1, var19, this, var6 + var10, var7, var15, var5, var16);
               }
            }

         }
      }
   }

   public Dimension getMinimumSize() {
      Dimension var1 = new Dimension();
      Insets var2 = this.getInsets();
      var1.width = var2.left + this.getComboIcon().getIconWidth() + var2.right;
      var1.height = var2.bottom + this.getComboIcon().getIconHeight() + var2.top;
      return var1;
   }
}
