package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MotifComboBoxUI extends BasicComboBoxUI implements Serializable {
   Icon arrowIcon;
   static final int HORIZ_MARGIN = 3;

   public static ComponentUI createUI(JComponent var0) {
      return new MotifComboBoxUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.arrowIcon = new MotifComboBoxUI.MotifComboBoxArrowIcon(UIManager.getColor("controlHighlight"), UIManager.getColor("controlShadow"), UIManager.getColor("control"));
      Runnable var2 = new Runnable() {
         public void run() {
            if (MotifComboBoxUI.this.motifGetEditor() != null) {
               MotifComboBoxUI.this.motifGetEditor().setBackground(UIManager.getColor("text"));
            }

         }
      };
      SwingUtilities.invokeLater(var2);
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (!this.isMinimumSizeDirty) {
         return new Dimension(this.cachedMinimumSize);
      } else {
         Insets var3 = this.getInsets();
         Dimension var2 = this.getDisplaySize();
         var2.height += var3.top + var3.bottom;
         int var4 = this.iconAreaWidth();
         var2.width += var3.left + var3.right + var4;
         this.cachedMinimumSize.setSize(var2.width, var2.height);
         this.isMinimumSizeDirty = false;
         return var2;
      }
   }

   protected ComboPopup createPopup() {
      return new MotifComboBoxUI.MotifComboPopup(this.comboBox);
   }

   protected void installComponents() {
      if (this.comboBox.isEditable()) {
         this.addEditor();
      }

      this.comboBox.add(this.currentValuePane);
   }

   protected void uninstallComponents() {
      this.removeEditor();
      this.comboBox.removeAll();
   }

   public void paint(Graphics var1, JComponent var2) {
      boolean var3 = this.comboBox.hasFocus();
      if (this.comboBox.isEnabled()) {
         var1.setColor(this.comboBox.getBackground());
      } else {
         var1.setColor(UIManager.getColor("ComboBox.disabledBackground"));
      }

      var1.fillRect(0, 0, var2.getWidth(), var2.getHeight());
      Rectangle var4;
      if (!this.comboBox.isEditable()) {
         var4 = this.rectangleForCurrentValue();
         this.paintCurrentValue(var1, var4, var3);
      }

      var4 = this.rectangleForArrowIcon();
      this.arrowIcon.paintIcon(var2, var1, var4.x, var4.y);
      if (!this.comboBox.isEditable()) {
         Border var5 = this.comboBox.getBorder();
         Insets var6;
         if (var5 != null) {
            var6 = var5.getBorderInsets(this.comboBox);
         } else {
            var6 = new Insets(0, 0, 0, 0);
         }

         if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
            var4.x -= 5;
         } else {
            var4.x += var4.width + 3 + 1;
         }

         var4.y = var6.top;
         var4.width = 1;
         var4.height = this.comboBox.getBounds().height - var6.bottom - var6.top;
         var1.setColor(UIManager.getColor("controlShadow"));
         var1.fillRect(var4.x, var4.y, var4.width, var4.height);
         ++var4.x;
         var1.setColor(UIManager.getColor("controlHighlight"));
         var1.fillRect(var4.x, var4.y, var4.width, var4.height);
      }

   }

   public void paintCurrentValue(Graphics var1, Rectangle var2, boolean var3) {
      ListCellRenderer var4 = this.comboBox.getRenderer();
      Component var5 = var4.getListCellRendererComponent(this.listBox, this.comboBox.getSelectedItem(), -1, false, false);
      var5.setFont(this.comboBox.getFont());
      if (this.comboBox.isEnabled()) {
         var5.setForeground(this.comboBox.getForeground());
         var5.setBackground(this.comboBox.getBackground());
      } else {
         var5.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
         var5.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
      }

      Dimension var6 = var5.getPreferredSize();
      this.currentValuePane.paintComponent(var1, var5, this.comboBox, var2.x, var2.y, var2.width, var6.height);
   }

   protected Rectangle rectangleForArrowIcon() {
      Rectangle var1 = this.comboBox.getBounds();
      Border var2 = this.comboBox.getBorder();
      Insets var3;
      if (var2 != null) {
         var3 = var2.getBorderInsets(this.comboBox);
      } else {
         var3 = new Insets(0, 0, 0, 0);
      }

      var1.x = var3.left;
      var1.y = var3.top;
      var1.width -= var3.left + var3.right;
      var1.height -= var3.top + var3.bottom;
      if (MotifGraphicsUtils.isLeftToRight(this.comboBox)) {
         var1.x = var1.x + var1.width - 3 - this.arrowIcon.getIconWidth();
      } else {
         var1.x += 3;
      }

      var1.y += (var1.height - this.arrowIcon.getIconHeight()) / 2;
      var1.width = this.arrowIcon.getIconWidth();
      var1.height = this.arrowIcon.getIconHeight();
      return var1;
   }

   protected Rectangle rectangleForCurrentValue() {
      int var1 = this.comboBox.getWidth();
      int var2 = this.comboBox.getHeight();
      Insets var3 = this.getInsets();
      return MotifGraphicsUtils.isLeftToRight(this.comboBox) ? new Rectangle(var3.left, var3.top, var1 - (var3.left + var3.right) - this.iconAreaWidth(), var2 - (var3.top + var3.bottom)) : new Rectangle(var3.left + this.iconAreaWidth(), var3.top, var1 - (var3.left + var3.right) - this.iconAreaWidth(), var2 - (var3.top + var3.bottom));
   }

   public int iconAreaWidth() {
      return this.comboBox.isEditable() ? this.arrowIcon.getIconWidth() + 6 : this.arrowIcon.getIconWidth() + 9 + 2;
   }

   public void configureEditor() {
      super.configureEditor();
      this.editor.setBackground(UIManager.getColor("text"));
   }

   protected LayoutManager createLayoutManager() {
      return new MotifComboBoxUI.ComboBoxLayoutManager();
   }

   private Component motifGetEditor() {
      return this.editor;
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new MotifComboBoxUI.MotifPropertyChangeListener();
   }

   private class MotifPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
      private MotifPropertyChangeListener() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         super.propertyChange(var1);
         String var2 = var1.getPropertyName();
         if (var2 == "enabled" && MotifComboBoxUI.this.comboBox.isEnabled()) {
            Component var3 = MotifComboBoxUI.this.motifGetEditor();
            if (var3 != null) {
               var3.setBackground(UIManager.getColor("text"));
            }
         }

      }

      // $FF: synthetic method
      MotifPropertyChangeListener(Object var2) {
         this();
      }
   }

   static class MotifComboBoxArrowIcon implements Icon, Serializable {
      private Color lightShadow;
      private Color darkShadow;
      private Color fill;

      public MotifComboBoxArrowIcon(Color var1, Color var2, Color var3) {
         this.lightShadow = var1;
         this.darkShadow = var2;
         this.fill = var3;
      }

      public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
         int var5 = this.getIconWidth();
         int var6 = this.getIconHeight();
         var2.setColor(this.lightShadow);
         var2.drawLine(var3, var4, var3 + var5 - 1, var4);
         var2.drawLine(var3, var4 + 1, var3 + var5 - 3, var4 + 1);
         var2.setColor(this.darkShadow);
         var2.drawLine(var3 + var5 - 2, var4 + 1, var3 + var5 - 1, var4 + 1);
         int var7 = var3 + 1;
         int var8 = var4 + 2;

         for(int var9 = var5 - 6; var8 + 1 < var4 + var6; var8 += 2) {
            var2.setColor(this.lightShadow);
            var2.drawLine(var7, var8, var7 + 1, var8);
            var2.drawLine(var7, var8 + 1, var7 + 1, var8 + 1);
            if (var9 > 0) {
               var2.setColor(this.fill);
               var2.drawLine(var7 + 2, var8, var7 + 1 + var9, var8);
               var2.drawLine(var7 + 2, var8 + 1, var7 + 1 + var9, var8 + 1);
            }

            var2.setColor(this.darkShadow);
            var2.drawLine(var7 + var9 + 2, var8, var7 + var9 + 3, var8);
            var2.drawLine(var7 + var9 + 2, var8 + 1, var7 + var9 + 3, var8 + 1);
            ++var7;
            var9 -= 2;
         }

         var2.setColor(this.darkShadow);
         var2.drawLine(var3 + var5 / 2, var4 + var6 - 1, var3 + var5 / 2, var4 + var6 - 1);
      }

      public int getIconWidth() {
         return 11;
      }

      public int getIconHeight() {
         return 11;
      }
   }

   public class ComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
      public ComboBoxLayoutManager() {
         MotifComboBoxUI.this.getClass();
         super();
      }

      public void layoutContainer(Container var1) {
         if (MotifComboBoxUI.this.motifGetEditor() != null) {
            Rectangle var2 = MotifComboBoxUI.this.rectangleForCurrentValue();
            ++var2.x;
            ++var2.y;
            --var2.width;
            var2.height -= 2;
            MotifComboBoxUI.this.motifGetEditor().setBounds(var2);
         }

      }
   }

   protected class MotifComboPopup extends BasicComboPopup {
      public MotifComboPopup(JComboBox var2) {
         super(var2);
      }

      public MouseMotionListener createListMouseMotionListener() {
         return new MouseMotionAdapter() {
         };
      }

      public KeyListener createKeyListener() {
         return super.createKeyListener();
      }

      protected class InvocationKeyHandler extends BasicComboPopup.InvocationKeyHandler {
         protected InvocationKeyHandler() {
            MotifComboPopup.this.getClass();
            super();
         }
      }
   }
}
