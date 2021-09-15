package com.apple.laf;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class AquaFocusHandler implements FocusListener, PropertyChangeListener {
   private boolean wasTemporary = false;
   private boolean repaintBorder = false;
   protected static final String FRAME_ACTIVE_PROPERTY = "Frame.active";
   static final PropertyChangeListener REPAINT_LISTENER = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent var1) {
         Object var2 = var1.getSource();
         if (var2 instanceof JComponent) {
            ((JComponent)var2).repaint();
         }

      }
   };

   public void focusGained(FocusEvent var1) {
      if (!this.wasTemporary || this.repaintBorder) {
         AquaBorder.repaintBorder((JComponent)var1.getSource());
         this.repaintBorder = false;
      }

      this.wasTemporary = false;
   }

   public void focusLost(FocusEvent var1) {
      this.wasTemporary = var1.isTemporary();
      if (!this.wasTemporary) {
         AquaBorder.repaintBorder((JComponent)var1.getSource());
      }

   }

   public void propertyChange(PropertyChangeEvent var1) {
      if ("Frame.active".equals(var1.getPropertyName())) {
         if (Boolean.TRUE.equals(var1.getNewValue())) {
            this.repaintBorder = true;
         } else if (this.wasTemporary) {
            AquaBorder.repaintBorder((JComponent)var1.getSource());
         }

      }
   }

   protected static boolean isActive(JComponent var0) {
      if (var0 == null) {
         return true;
      } else {
         Object var1 = var0.getClientProperty("Frame.active");
         return !Boolean.FALSE.equals(var1);
      }
   }

   protected static void install(JComponent var0) {
      var0.addPropertyChangeListener("Frame.active", REPAINT_LISTENER);
   }

   protected static void uninstall(JComponent var0) {
      var0.removePropertyChangeListener("Frame.active", REPAINT_LISTENER);
   }

   static void swapSelectionColors(String var0, JTree var1, Object var2) {
   }

   static void swapSelectionColors(String var0, JTable var1, Object var2) {
      if (isComponentValid(var1)) {
         Color var3 = var1.getSelectionBackground();
         Color var4 = var1.getSelectionForeground();
         if (var3 instanceof UIResource && var4 instanceof UIResource) {
            if (Boolean.FALSE.equals(var2)) {
               setSelectionColors(var1, "Table.selectionInactiveForeground", "Table.selectionInactiveBackground");
            } else if (Boolean.TRUE.equals(var2)) {
               setSelectionColors(var1, "Table.selectionForeground", "Table.selectionBackground");
            }
         }
      }
   }

   static void setSelectionColors(JTable var0, String var1, String var2) {
      var0.setSelectionForeground(UIManager.getColor(var1));
      var0.setSelectionBackground(UIManager.getColor(var2));
   }

   static void swapSelectionColors(String var0, JList var1, Object var2) {
      if (isComponentValid(var1)) {
         Color var3 = var1.getSelectionBackground();
         Color var4 = var1.getSelectionForeground();
         if (var3 instanceof UIResource && var4 instanceof UIResource) {
            if (Boolean.FALSE.equals(var2)) {
               setSelectionColors(var1, "List.selectionInactiveForeground", "List.selectionInactiveBackground");
            } else if (Boolean.TRUE.equals(var2)) {
               setSelectionColors(var1, "List.selectionForeground", "List.selectionBackground");
            }
         }
      }
   }

   static void setSelectionColors(JList var0, String var1, String var2) {
      var0.setSelectionForeground(UIManager.getColor(var1));
      var0.setSelectionBackground(UIManager.getColor(var2));
   }

   static boolean isComponentValid(JComponent var0) {
      if (var0 == null) {
         return false;
      } else {
         Window var1 = SwingUtilities.getWindowAncestor(var0);
         return var1 != null;
      }
   }
}
