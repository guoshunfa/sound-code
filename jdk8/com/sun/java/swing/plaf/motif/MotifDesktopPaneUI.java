package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class MotifDesktopPaneUI extends BasicDesktopPaneUI {
   public static ComponentUI createUI(JComponent var0) {
      return new MotifDesktopPaneUI();
   }

   protected void installDesktopManager() {
      this.desktopManager = this.desktop.getDesktopManager();
      if (this.desktopManager == null) {
         this.desktopManager = new MotifDesktopPaneUI.MotifDesktopManager();
         this.desktop.setDesktopManager(this.desktopManager);
         ((MotifDesktopPaneUI.MotifDesktopManager)this.desktopManager).adjustIcons(this.desktop);
      }

   }

   public Insets getInsets(JComponent var1) {
      return new Insets(0, 0, 0, 0);
   }

   private class MotifDesktopManager extends DefaultDesktopManager implements Serializable, UIResource {
      JComponent dragPane;
      boolean usingDragPane;
      private transient JLayeredPane layeredPaneForDragPane;
      int iconWidth;
      int iconHeight;

      private MotifDesktopManager() {
         this.usingDragPane = false;
      }

      public void setBoundsForFrame(JComponent var1, int var2, int var3, int var4, int var5) {
         if (!this.usingDragPane) {
            boolean var6 = var1.getWidth() != var4 || var1.getHeight() != var5;
            Rectangle var7 = var1.getBounds();
            var1.setBounds(var2, var3, var4, var5);
            SwingUtilities.computeUnion(var2, var3, var4, var5, var7);
            var1.getParent().repaint(var7.x, var7.y, var7.width, var7.height);
            if (var6) {
               var1.validate();
            }
         } else {
            Rectangle var8 = this.dragPane.getBounds();
            this.dragPane.setBounds(var2, var3, var4, var5);
            SwingUtilities.computeUnion(var2, var3, var4, var5, var8);
            this.dragPane.getParent().repaint(var8.x, var8.y, var8.width, var8.height);
         }

      }

      public void beginDraggingFrame(JComponent var1) {
         this.usingDragPane = false;
         if (var1.getParent() instanceof JLayeredPane) {
            if (this.dragPane == null) {
               this.dragPane = MotifDesktopPaneUI.this.new DragPane();
            }

            this.layeredPaneForDragPane = (JLayeredPane)var1.getParent();
            this.layeredPaneForDragPane.setLayer(this.dragPane, Integer.MAX_VALUE);
            this.dragPane.setBounds(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
            this.layeredPaneForDragPane.add(this.dragPane);
            this.usingDragPane = true;
         }

      }

      public void dragFrame(JComponent var1, int var2, int var3) {
         this.setBoundsForFrame(var1, var2, var3, var1.getWidth(), var1.getHeight());
      }

      public void endDraggingFrame(JComponent var1) {
         if (this.usingDragPane) {
            this.layeredPaneForDragPane.remove(this.dragPane);
            this.usingDragPane = false;
            if (var1 instanceof JInternalFrame) {
               this.setBoundsForFrame(var1, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
            } else if (var1 instanceof JInternalFrame.JDesktopIcon) {
               this.adjustBoundsForIcon((JInternalFrame.JDesktopIcon)var1, this.dragPane.getX(), this.dragPane.getY());
            }
         }

      }

      public void beginResizingFrame(JComponent var1, int var2) {
         this.usingDragPane = false;
         if (var1.getParent() instanceof JLayeredPane) {
            if (this.dragPane == null) {
               this.dragPane = MotifDesktopPaneUI.this.new DragPane();
            }

            JLayeredPane var3 = (JLayeredPane)var1.getParent();
            var3.setLayer(this.dragPane, Integer.MAX_VALUE);
            this.dragPane.setBounds(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
            var3.add(this.dragPane);
            this.usingDragPane = true;
         }

      }

      public void resizeFrame(JComponent var1, int var2, int var3, int var4, int var5) {
         this.setBoundsForFrame(var1, var2, var3, var4, var5);
      }

      public void endResizingFrame(JComponent var1) {
         if (this.usingDragPane) {
            JLayeredPane var2 = (JLayeredPane)var1.getParent();
            var2.remove(this.dragPane);
            this.usingDragPane = false;
            this.setBoundsForFrame(var1, this.dragPane.getX(), this.dragPane.getY(), this.dragPane.getWidth(), this.dragPane.getHeight());
         }

      }

      public void iconifyFrame(JInternalFrame var1) {
         JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
         Point var3 = var2.getLocation();
         this.adjustBoundsForIcon(var2, var3.x, var3.y);
         super.iconifyFrame(var1);
      }

      protected void adjustIcons(JDesktopPane var1) {
         JInternalFrame.JDesktopIcon var2 = new JInternalFrame.JDesktopIcon(new JInternalFrame());
         Dimension var3 = var2.getPreferredSize();
         this.iconWidth = var3.width;
         this.iconHeight = var3.height;
         JInternalFrame[] var4 = var1.getAllFrames();

         for(int var5 = 0; var5 < var4.length; ++var5) {
            var2 = var4[var5].getDesktopIcon();
            Point var6 = var2.getLocation();
            this.adjustBoundsForIcon(var2, var6.x, var6.y);
         }

      }

      protected void adjustBoundsForIcon(JInternalFrame.JDesktopIcon var1, int var2, int var3) {
         JDesktopPane var4 = var1.getDesktopPane();
         int var5 = var4.getHeight();
         int var6 = this.iconWidth;
         int var7 = this.iconHeight;
         var4.repaint(var2, var3, var6, var7);
         var2 = var2 < 0 ? 0 : var2;
         var3 = var3 < 0 ? 0 : var3;
         var3 = var3 >= var5 ? var5 - 1 : var3;
         int var8 = var2 / var6 * var6;
         int var9 = var5 % var7;
         int var10 = (var3 - var9) / var7 * var7 + var9;
         int var11 = var2 - var8;
         int var12 = var3 - var10;
         var2 = var11 < var6 / 2 ? var8 : var8 + var6;

         for(var3 = var12 < var7 / 2 ? var10 : (var10 + var7 < var5 ? var10 + var7 : var10); this.getIconAt(var4, var1, var2, var3) != null; var2 += var6) {
         }

         if (var2 <= var4.getWidth()) {
            if (var1.getParent() != null) {
               this.setBoundsForFrame(var1, var2, var3, var6, var7);
            } else {
               var1.setLocation(var2, var3);
            }

         }
      }

      protected JInternalFrame.JDesktopIcon getIconAt(JDesktopPane var1, JInternalFrame.JDesktopIcon var2, int var3, int var4) {
         Object var5 = null;
         Component[] var6 = var1.getComponents();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            Component var8 = var6[var7];
            if (var8 instanceof JInternalFrame.JDesktopIcon && var8 != var2) {
               Point var9 = var8.getLocation();
               if (var9.x == var3 && var9.y == var4) {
                  return (JInternalFrame.JDesktopIcon)var8;
               }
            }
         }

         return null;
      }

      // $FF: synthetic method
      MotifDesktopManager(Object var2) {
         this();
      }
   }

   private class DragPane extends JComponent {
      private DragPane() {
      }

      public void paint(Graphics var1) {
         var1.setColor(Color.darkGray);
         var1.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
      }

      // $FF: synthetic method
      DragPane(Object var2) {
         this();
      }
   }
}
