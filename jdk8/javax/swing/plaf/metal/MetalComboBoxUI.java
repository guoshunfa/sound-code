package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class MetalComboBoxUI extends BasicComboBoxUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MetalComboBoxUI();
   }

   public void paint(Graphics var1, JComponent var2) {
      if (MetalLookAndFeel.usingOcean()) {
         super.paint(var1, var2);
      }

   }

   public void paintCurrentValue(Graphics var1, Rectangle var2, boolean var3) {
      if (MetalLookAndFeel.usingOcean()) {
         var2.x += 2;
         var2.width -= 3;
         if (this.arrowButton != null) {
            Insets var4 = this.arrowButton.getInsets();
            var2.y += var4.top;
            var2.height -= var4.top + var4.bottom;
         } else {
            var2.y += 2;
            var2.height -= 4;
         }

         super.paintCurrentValue(var1, var2, var3);
      } else if (var1 == null || var2 == null) {
         throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
      }

   }

   public void paintCurrentValueBackground(Graphics var1, Rectangle var2, boolean var3) {
      if (MetalLookAndFeel.usingOcean()) {
         var1.setColor(MetalLookAndFeel.getControlDarkShadow());
         var1.drawRect(var2.x, var2.y, var2.width, var2.height - 1);
         var1.setColor(MetalLookAndFeel.getControlShadow());
         var1.drawRect(var2.x + 1, var2.y + 1, var2.width - 2, var2.height - 3);
         if (var3 && !this.isPopupVisible(this.comboBox) && this.arrowButton != null) {
            var1.setColor(this.listBox.getSelectionBackground());
            Insets var4 = this.arrowButton.getInsets();
            if (var4.top > 2) {
               var1.fillRect(var2.x + 2, var2.y + 2, var2.width - 3, var4.top - 2);
            }

            if (var4.bottom > 2) {
               var1.fillRect(var2.x + 2, var2.y + var2.height - var4.bottom, var2.width - 3, var4.bottom - 2);
            }
         }
      } else if (var1 == null || var2 == null) {
         throw new NullPointerException("Must supply a non-null Graphics and Rectangle");
      }

   }

   public int getBaseline(JComponent var1, int var2, int var3) {
      int var4;
      if (MetalLookAndFeel.usingOcean() && var3 >= 4) {
         var3 -= 4;
         var4 = super.getBaseline(var1, var2, var3);
         if (var4 >= 0) {
            var4 += 2;
         }
      } else {
         var4 = super.getBaseline(var1, var2, var3);
      }

      return var4;
   }

   protected ComboBoxEditor createEditor() {
      return new MetalComboBoxEditor.UIResource();
   }

   protected ComboPopup createPopup() {
      return super.createPopup();
   }

   protected JButton createArrowButton() {
      boolean var1 = this.comboBox.isEditable() || MetalLookAndFeel.usingOcean();
      MetalComboBoxButton var2 = new MetalComboBoxButton(this.comboBox, new MetalComboBoxIcon(), var1, this.currentValuePane, this.listBox);
      var2.setMargin(new Insets(0, 1, 1, 3));
      if (MetalLookAndFeel.usingOcean()) {
         var2.putClientProperty(MetalBorders.NO_BUTTON_ROLLOVER, Boolean.TRUE);
      }

      this.updateButtonForOcean(var2);
      return var2;
   }

   private void updateButtonForOcean(JButton var1) {
      if (MetalLookAndFeel.usingOcean()) {
         var1.setFocusPainted(this.comboBox.isEditable());
      }

   }

   public PropertyChangeListener createPropertyChangeListener() {
      return new MetalComboBoxUI.MetalPropertyChangeListener();
   }

   /** @deprecated */
   @Deprecated
   protected void editablePropertyChanged(PropertyChangeEvent var1) {
   }

   protected LayoutManager createLayoutManager() {
      return new MetalComboBoxUI.MetalComboBoxLayoutManager();
   }

   public void layoutComboBox(Container var1, MetalComboBoxUI.MetalComboBoxLayoutManager var2) {
      if (this.comboBox.isEditable() && !MetalLookAndFeel.usingOcean()) {
         var2.superLayout(var1);
      } else {
         if (this.arrowButton != null) {
            Insets var3;
            int var4;
            if (MetalLookAndFeel.usingOcean()) {
               var3 = this.comboBox.getInsets();
               var4 = this.arrowButton.getMinimumSize().width;
               this.arrowButton.setBounds(MetalUtils.isLeftToRight(this.comboBox) ? this.comboBox.getWidth() - var3.right - var4 : var3.left, var3.top, var4, this.comboBox.getHeight() - var3.top - var3.bottom);
            } else {
               var3 = this.comboBox.getInsets();
               var4 = this.comboBox.getWidth();
               int var5 = this.comboBox.getHeight();
               this.arrowButton.setBounds(var3.left, var3.top, var4 - (var3.left + var3.right), var5 - (var3.top + var3.bottom));
            }
         }

         if (this.editor != null && MetalLookAndFeel.usingOcean()) {
            Rectangle var6 = this.rectangleForCurrentValue();
            this.editor.setBounds(var6);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   protected void removeListeners() {
      if (this.propertyChangeListener != null) {
         this.comboBox.removePropertyChangeListener(this.propertyChangeListener);
      }

   }

   public void configureEditor() {
      super.configureEditor();
   }

   public void unconfigureEditor() {
      super.unconfigureEditor();
   }

   public Dimension getMinimumSize(JComponent var1) {
      if (!this.isMinimumSizeDirty) {
         return new Dimension(this.cachedMinimumSize);
      } else {
         Dimension var2 = null;
         Insets var3;
         if (!this.comboBox.isEditable() && this.arrowButton != null) {
            var3 = this.arrowButton.getInsets();
            Insets var4 = this.comboBox.getInsets();
            var2 = this.getDisplaySize();
            var2.width += var4.left + var4.right;
            var2.width += var3.right;
            var2.width += this.arrowButton.getMinimumSize().width;
            var2.height += var4.top + var4.bottom;
            var2.height += var3.top + var3.bottom;
         } else if (this.comboBox.isEditable() && this.arrowButton != null && this.editor != null) {
            var2 = super.getMinimumSize(var1);
            var3 = this.arrowButton.getMargin();
            var2.height += var3.top + var3.bottom;
            var2.width += var3.left + var3.right;
         } else {
            var2 = super.getMinimumSize(var1);
         }

         this.cachedMinimumSize.setSize(var2.width, var2.height);
         this.isMinimumSizeDirty = false;
         return new Dimension(this.cachedMinimumSize);
      }
   }

   /** @deprecated */
   @Deprecated
   public class MetalComboPopup extends BasicComboPopup {
      public MetalComboPopup(JComboBox var2) {
         super(var2);
      }

      public void delegateFocus(MouseEvent var1) {
         super.delegateFocus(var1);
      }
   }

   public class MetalComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
      public MetalComboBoxLayoutManager() {
         super();
      }

      public void layoutContainer(Container var1) {
         MetalComboBoxUI.this.layoutComboBox(var1, this);
      }

      public void superLayout(Container var1) {
         super.layoutContainer(var1);
      }
   }

   public class MetalPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
      public MetalPropertyChangeListener() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         super.propertyChange(var1);
         String var2 = var1.getPropertyName();
         if (var2 == "editable") {
            if (MetalComboBoxUI.this.arrowButton instanceof MetalComboBoxButton) {
               MetalComboBoxButton var3 = (MetalComboBoxButton)MetalComboBoxUI.this.arrowButton;
               var3.setIconOnly(MetalComboBoxUI.this.comboBox.isEditable() || MetalLookAndFeel.usingOcean());
            }

            MetalComboBoxUI.this.comboBox.repaint();
            MetalComboBoxUI.this.updateButtonForOcean(MetalComboBoxUI.this.arrowButton);
         } else {
            Color var4;
            if (var2 == "background") {
               var4 = (Color)var1.getNewValue();
               MetalComboBoxUI.this.arrowButton.setBackground(var4);
               MetalComboBoxUI.this.listBox.setBackground(var4);
            } else if (var2 == "foreground") {
               var4 = (Color)var1.getNewValue();
               MetalComboBoxUI.this.arrowButton.setForeground(var4);
               MetalComboBoxUI.this.listBox.setForeground(var4);
            }
         }

      }
   }
}
