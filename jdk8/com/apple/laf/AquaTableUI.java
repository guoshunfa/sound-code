package com.apple.laf;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;

public class AquaTableUI extends BasicTableUI {
   protected AquaFocusHandler focusHandler = new AquaFocusHandler() {
      public void propertyChange(PropertyChangeEvent var1) {
         super.propertyChange(var1);
         if ("Frame.active".equals(var1.getPropertyName())) {
            AquaFocusHandler.swapSelectionColors("Table", AquaTableUI.this.getComponent(), var1.getNewValue());
         }
      }
   };

   public static ComponentUI createUI(JComponent var0) {
      return new AquaTableUI();
   }

   protected FocusListener createFocusListener() {
      return new AquaTableUI.FocusHandler();
   }

   protected MouseInputListener createMouseInputListener() {
      return new AquaTableUI.MouseInputHandler();
   }

   protected void installListeners() {
      super.installListeners();
      this.table.addFocusListener(this.focusHandler);
      this.table.addPropertyChangeListener(this.focusHandler);
   }

   protected void uninstallListeners() {
      this.table.removePropertyChangeListener(this.focusHandler);
      this.table.removeFocusListener(this.focusHandler);
      super.uninstallListeners();
   }

   JTable getComponent() {
      return this.table;
   }

   public class MouseInputHandler extends BasicTableUI.MouseInputHandler {
      public MouseInputHandler() {
         super();
      }
   }

   public class FocusHandler extends BasicTableUI.FocusHandler {
      public FocusHandler() {
         super();
      }

      public void focusGained(FocusEvent var1) {
         super.focusGained(var1);
         AquaBorder.repaintBorder(AquaTableUI.this.getComponent());
      }

      public void focusLost(FocusEvent var1) {
         super.focusLost(var1);
         AquaBorder.repaintBorder(AquaTableUI.this.getComponent());
      }
   }
}
