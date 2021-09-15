package com.apple.laf;

import apple.laf.JRSUIConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicListUI;

public class AquaListUI extends BasicListUI {
   public static ComponentUI createUI(JComponent var0) {
      return new AquaListUI();
   }

   protected FocusListener createFocusListener() {
      return new AquaListUI.FocusHandler();
   }

   protected MouseInputListener createMouseInputListener() {
      return new AquaListUI.MouseInputHandler();
   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      this.list.getActionMap().put("aquaHome", new AquaListUI.AquaHomeEndAction(true));
      this.list.getActionMap().put("aquaEnd", new AquaListUI.AquaHomeEndAction(false));
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return new AquaListUI.AquaPropertyChangeHandler();
   }

   JList getComponent() {
      return this.list;
   }

   protected void repaintCell(Object var1, int var2, boolean var3) {
      Rectangle var4 = this.getCellBounds(this.list, var2, var2);
      if (var4 != null) {
         ListCellRenderer var5 = this.list.getCellRenderer();
         if (var5 != null) {
            Component var6 = var5.getListCellRendererComponent(this.list, var1, var2, var3, true);
            if (var6 != null) {
               AquaComboBoxRenderer var7 = var5 instanceof AquaComboBoxRenderer ? (AquaComboBoxRenderer)var5 : null;
               if (var7 != null) {
                  var7.setDrawCheckedItem(false);
               }

               this.rendererPane.paintComponent(this.list.getGraphics().create(), var6, this.list, var4.x, var4.y, var4.width, var4.height, true);
               if (var7 != null) {
                  var7.setDrawCheckedItem(true);
               }

            }
         }
      }
   }

   public static Border getSourceListBackgroundPainter() {
      AquaListUI.ComponentPainter var0 = new AquaListUI.ComponentPainter();
      var0.painter.state.set(JRSUIConstants.Widget.GRADIENT);
      var0.painter.state.set(JRSUIConstants.Variant.GRADIENT_SIDE_BAR);
      return var0;
   }

   public static Border getSourceListSelectionBackgroundPainter() {
      AquaListUI.ComponentPainter var0 = new AquaListUI.ComponentPainter();
      var0.painter.state.set(JRSUIConstants.Widget.GRADIENT);
      var0.painter.state.set(JRSUIConstants.Variant.GRADIENT_SIDE_BAR_SELECTION);
      return var0;
   }

   public static Border getSourceListFocusedSelectionBackgroundPainter() {
      AquaListUI.ComponentPainter var0 = new AquaListUI.ComponentPainter();
      var0.painter.state.set(JRSUIConstants.Widget.GRADIENT);
      var0.painter.state.set(JRSUIConstants.Variant.GRADIENT_SIDE_BAR_FOCUSED_SELECTION);
      return var0;
   }

   public static Border getListEvenBackgroundPainter() {
      AquaListUI.ComponentPainter var0 = new AquaListUI.ComponentPainter();
      var0.painter.state.set(JRSUIConstants.Widget.GRADIENT);
      var0.painter.state.set(JRSUIConstants.Variant.GRADIENT_LIST_BACKGROUND_EVEN);
      return var0;
   }

   public static Border getListOddBackgroundPainter() {
      AquaListUI.ComponentPainter var0 = new AquaListUI.ComponentPainter();
      var0.painter.state.set(JRSUIConstants.Widget.GRADIENT);
      var0.painter.state.set(JRSUIConstants.Variant.GRADIENT_LIST_BACKGROUND_ODD);
      return var0;
   }

   static class ComponentPainter extends AquaBorder.Default {
      public void paintBorder(Component var1, Graphics var2, int var3, int var4, int var5, int var6) {
         JComponent var7 = var1 instanceof JComponent ? (JComponent)var1 : null;
         if (var7 != null && !AquaFocusHandler.isActive(var7)) {
            this.painter.state.set(JRSUIConstants.State.INACTIVE);
         } else {
            this.painter.state.set(JRSUIConstants.State.ACTIVE);
         }

         super.paintBorder(var1, var2, var3, var4, var5, var6);
      }
   }

   class MouseInputHandler extends BasicListUI.MouseInputHandler {
      MouseInputHandler() {
         super();
      }
   }

   class AquaPropertyChangeHandler extends BasicListUI.PropertyChangeHandler {
      AquaPropertyChangeHandler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         if ("Frame.active".equals(var2)) {
            AquaBorder.repaintBorder(AquaListUI.this.getComponent());
            AquaFocusHandler.swapSelectionColors("List", AquaListUI.this.getComponent(), var1.getNewValue());
         } else {
            super.propertyChange(var1);
         }

      }
   }

   class FocusHandler extends BasicListUI.FocusHandler {
      FocusHandler() {
         super();
      }

      public void focusGained(FocusEvent var1) {
         super.focusGained(var1);
         AquaBorder.repaintBorder(AquaListUI.this.getComponent());
      }

      public void focusLost(FocusEvent var1) {
         super.focusLost(var1);
         AquaBorder.repaintBorder(AquaListUI.this.getComponent());
      }
   }

   static class AquaHomeEndAction extends AbstractAction {
      private boolean fHomeAction = false;

      protected AquaHomeEndAction(boolean var1) {
         this.fHomeAction = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         JList var2 = (JList)var1.getSource();
         if (this.fHomeAction) {
            var2.ensureIndexIsVisible(0);
         } else {
            int var3 = var2.getModel().getSize();
            var2.ensureIndexIsVisible(var3 - 1);
         }

      }
   }
}
