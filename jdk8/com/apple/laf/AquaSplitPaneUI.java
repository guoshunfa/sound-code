package com.apple.laf;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class AquaSplitPaneUI extends BasicSplitPaneUI implements MouseListener, PropertyChangeListener {
   static final String DIVIDER_PAINTER_KEY = "JSplitPane.dividerPainter";

   public static ComponentUI createUI(JComponent var0) {
      return new AquaSplitPaneUI();
   }

   public BasicSplitPaneDivider createDefaultDivider() {
      return new AquaSplitPaneDividerUI(this);
   }

   protected void installListeners() {
      super.installListeners();
      this.splitPane.addPropertyChangeListener("JSplitPane.dividerPainter", this);
      this.divider.addMouseListener(this);
   }

   protected void uninstallListeners() {
      this.divider.removeMouseListener(this);
      this.splitPane.removePropertyChangeListener("JSplitPane.dividerPainter", this);
      super.uninstallListeners();
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() >= 2) {
         if (this.splitPane.isOneTouchExpandable()) {
            double var2 = this.splitPane.getResizeWeight();
            int var4 = this.splitPane.getWidth();
            int var5 = this.splitPane.getDividerSize();
            int var6 = this.splitPane.getDividerLocation();
            int var7 = this.splitPane.getLastDividerLocation();
            if (var4 - var5 <= var6 + 5) {
               this.splitPane.setDividerLocation(var7);
            } else if (var5 >= var6 - 5) {
               this.splitPane.setDividerLocation(var7);
            } else {
               if (var2 > 0.5D) {
                  this.splitPane.setDividerLocation(0);
               } else {
                  this.splitPane.setDividerLocation(var4);
               }

            }
         }
      }
   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
   }

   public void mouseReleased(MouseEvent var1) {
   }

   public void propertyChange(PropertyChangeEvent var1) {
      if ("JSplitPane.dividerPainter".equals(var1.getPropertyName())) {
         Object var2 = var1.getNewValue();
         if (var2 instanceof Border) {
            this.divider.setBorder((Border)var2);
         } else {
            this.divider.setBorder((Border)null);
         }

      }
   }
}
