package com.apple.laf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import sun.swing.SwingUtilities2;

class AquaComboBoxRendererInternal extends JLabel implements ListCellRenderer {
   final JComboBox fComboBox;
   boolean fSelected;
   boolean fChecked;
   boolean fInList;
   boolean fEditable;
   boolean fDrawCheckedItem = true;

   public AquaComboBoxRendererInternal(JComboBox var1) {
      this.fComboBox = var1;
   }

   public Dimension getPreferredSize() {
      String var2 = this.getText();
      Dimension var1;
      if (var2 != null && !"".equals(var2)) {
         var1 = super.getPreferredSize();
      } else {
         this.setText(" ");
         var1 = super.getPreferredSize();
         this.setText("");
      }

      return var1;
   }

   protected void paintBorder(Graphics var1) {
   }

   public int getBaseline(int var1, int var2) {
      return super.getBaseline(var1, var2) - 1;
   }

   public Component getListCellRendererComponent(JList var1, Object var2, int var3, boolean var4, boolean var5) {
      this.fInList = var3 >= 0;
      this.fSelected = var4;
      if (var3 < 0) {
         var3 = this.fComboBox.getSelectedIndex();
      }

      if (var3 >= 0) {
         Object var6 = this.fComboBox.getItemAt(var3);
         this.fChecked = this.fInList && var6 != null && var6.equals(this.fComboBox.getSelectedItem());
      } else {
         this.fChecked = false;
      }

      this.fEditable = this.fComboBox.isEditable();
      if (var4) {
         if (this.fEditable) {
            this.setBackground(UIManager.getColor("List.selectionBackground"));
            this.setForeground(UIManager.getColor("List.selectionForeground"));
         } else {
            this.setBackground(var1.getSelectionBackground());
            this.setForeground(var1.getSelectionForeground());
         }
      } else if (this.fEditable) {
         this.setBackground(UIManager.getColor("List.background"));
         this.setForeground(UIManager.getColor("List.foreground"));
      } else {
         this.setBackground(var1.getBackground());
         this.setForeground(var1.getForeground());
      }

      this.setFont(var1.getFont());
      if (var2 instanceof Icon) {
         this.setIcon((Icon)var2);
      } else {
         this.setText(var2 == null ? " " : var2.toString());
      }

      return this;
   }

   public Insets getInsets(Insets var1) {
      if (var1 == null) {
         var1 = new Insets(0, 0, 0, 0);
      }

      var1.top = 1;
      var1.bottom = 1;
      var1.right = 5;
      var1.left = this.fInList && !this.fEditable ? 23 : 5;
      return var1;
   }

   protected void setDrawCheckedItem(boolean var1) {
      this.fDrawCheckedItem = var1;
   }

   protected void paintComponent(Graphics var1) {
      if (this.fInList) {
         if (this.fSelected && !this.fEditable) {
            AquaMenuPainter.instance().paintSelectedMenuItemBackground(var1, this.getWidth(), this.getHeight());
         } else {
            var1.setColor(this.getBackground());
            var1.fillRect(0, 0, this.getWidth(), this.getHeight());
         }

         if (this.fChecked && !this.fEditable && this.fDrawCheckedItem) {
            int var2 = this.getHeight() - 4;
            var1.setColor(this.getForeground());
            SwingUtilities2.drawString(this.fComboBox, var1, (String)"✓", 6, var2);
         }
      }

      super.paintComponent(var1);
   }
}
