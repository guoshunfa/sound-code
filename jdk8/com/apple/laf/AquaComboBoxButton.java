package com.apple.laf;

import apple.laf.JRSUIConstants;
import apple.laf.JRSUIState;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

class AquaComboBoxButton extends JButton {
   protected final JComboBox comboBox;
   protected final JList list;
   protected final CellRendererPane rendererPane;
   protected final AquaComboBoxUI ui;
   protected final AquaPainter<JRSUIState> painter = AquaPainter.create(JRSUIState.getInstance());
   boolean isPopDown;
   boolean isSquare;

   protected AquaComboBoxButton(AquaComboBoxUI var1, JComboBox var2, CellRendererPane var3, JList var4) {
      super("");
      this.putClientProperty("JButton.buttonType", "comboboxInternal");
      this.ui = var1;
      this.comboBox = var2;
      this.rendererPane = var3;
      this.list = var4;
      this.setModel(new DefaultButtonModel() {
         public void setArmed(boolean var1) {
            super.setArmed(this.isPressed() ? true : var1);
         }
      });
      this.setEnabled(var2.isEnabled());
   }

   public boolean isEnabled() {
      return this.comboBox == null ? true : this.comboBox.isEnabled();
   }

   public boolean isFocusTraversable() {
      return false;
   }

   protected void setIsPopDown(boolean var1) {
      this.isPopDown = var1;
      this.repaint();
   }

   protected void setIsSquare(boolean var1) {
      this.isSquare = var1;
      this.repaint();
   }

   protected JRSUIConstants.State getState(ButtonModel var1) {
      if (!this.comboBox.isEnabled()) {
         return JRSUIConstants.State.DISABLED;
      } else if (!AquaFocusHandler.isActive(this.comboBox)) {
         return JRSUIConstants.State.INACTIVE;
      } else {
         return var1.isArmed() ? JRSUIConstants.State.PRESSED : JRSUIConstants.State.ACTIVE;
      }
   }

   public void paintComponent(Graphics var1) {
      boolean var2 = this.comboBox.isEditable();
      int var3 = 0;
      int var4 = 0;
      int var5 = this.getWidth();
      int var6 = this.getHeight();
      if (this.comboBox.isOpaque()) {
         var1.setColor(this.getBackground());
         var1.fillRect(0, 0, var5, var6);
      }

      JRSUIConstants.Size var7 = AquaUtilControlSize.getUserSizeFrom(this.comboBox);
      this.painter.state.set(var7 == null ? JRSUIConstants.Size.REGULAR : var7);
      ButtonModel var8 = this.getModel();
      this.painter.state.set(this.getState(var8));
      this.painter.state.set(JRSUIConstants.AlignmentVertical.CENTER);
      if (AquaComboBoxUI.isTableCellEditor(this.comboBox)) {
         this.painter.state.set(JRSUIConstants.AlignmentHorizontal.RIGHT);
         this.painter.state.set(JRSUIConstants.Widget.BUTTON_POP_UP);
         this.painter.state.set(JRSUIConstants.ArrowsOnly.YES);
         this.painter.paint(var1, this, var4, var3, var5, var6);
         this.doRendererPaint(var1, var8, var2, this.getInsets(), var4, var3, var5, var6);
      } else {
         this.painter.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
         Insets var9 = this.getInsets();
         if (!var2) {
            var3 += var9.top;
            var4 += var9.left;
            var5 -= var9.left + var9.right;
            var6 -= var9.top + var9.bottom;
         }

         if (var6 > 0 && var5 > 0) {
            boolean var10 = this.comboBox.hasFocus();
            if (var2) {
               this.painter.state.set(JRSUIConstants.Widget.BUTTON_COMBO_BOX);
               this.painter.state.set(JRSUIConstants.IndicatorOnly.YES);
               this.painter.state.set(JRSUIConstants.AlignmentHorizontal.LEFT);
               var10 |= this.comboBox.getEditor().getEditorComponent().hasFocus();
            } else {
               this.painter.state.set(JRSUIConstants.IndicatorOnly.NO);
               this.painter.state.set(JRSUIConstants.AlignmentHorizontal.CENTER);
               if (this.isPopDown) {
                  this.painter.state.set(this.isSquare ? JRSUIConstants.Widget.BUTTON_POP_DOWN_SQUARE : JRSUIConstants.Widget.BUTTON_POP_DOWN);
               } else {
                  this.painter.state.set(this.isSquare ? JRSUIConstants.Widget.BUTTON_POP_UP_SQUARE : JRSUIConstants.Widget.BUTTON_POP_UP);
               }
            }

            this.painter.state.set(var10 ? JRSUIConstants.Focused.YES : JRSUIConstants.Focused.NO);
            if (this.isSquare) {
               this.painter.paint(var1, this.comboBox, var4 + 2, var3 - 1, var5 - 4, var6);
            } else {
               this.painter.paint(var1, this.comboBox, var4, var3, var5, var6);
            }

            if (!var2 && this.comboBox != null) {
               this.doRendererPaint(var1, var8, var2, var9, var4, var3, var5, var6);
            }

         }
      }
   }

   protected void doRendererPaint(Graphics var1, ButtonModel var2, boolean var3, Insets var4, int var5, int var6, int var7, int var8) {
      ListCellRenderer var9 = this.comboBox.getRenderer();
      Component var10 = var9.getListCellRendererComponent(this.list, this.comboBox.getSelectedItem(), -1, false, false);
      if (!var3 && !AquaComboBoxUI.isTableCellEditor(this.comboBox)) {
         ++var6;
         var8 -= 4;
         var5 += 10;
         var7 -= 34;
      }

      var10.setFont(this.rendererPane.getFont());
      if (var2.isArmed() && var2.isPressed()) {
         if (this.isOpaque()) {
            var10.setBackground(UIManager.getColor("Button.select"));
         }

         var10.setForeground(this.comboBox.getForeground());
      } else if (!this.comboBox.isEnabled()) {
         if (this.isOpaque()) {
            var10.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
         }

         var10.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
      } else {
         var10.setForeground(this.comboBox.getForeground());
         var10.setBackground(this.comboBox.getBackground());
      }

      boolean var11 = false;
      if (var10 instanceof JPanel) {
         var11 = true;
      }

      int var13 = var7 - (var4.right + 0);
      var6 = var8 / 2 - 8;
      byte var16 = 19;
      Color var14 = var10.getBackground();
      boolean var15 = var14 instanceof UIResource;
      if (var15) {
         var10.setBackground(new Color(0, 0, 0, 0));
      }

      this.rendererPane.paintComponent(var1, var10, this, var5, var6, var13, var16, var11);
      if (var15) {
         var10.setBackground(var14);
      }

   }
}
