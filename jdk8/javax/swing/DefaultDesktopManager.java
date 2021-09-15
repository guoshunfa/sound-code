package javax.swing;

import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DefaultDesktopManager implements DesktopManager, Serializable {
   static final String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";
   static final int DEFAULT_DRAG_MODE = 0;
   static final int OUTLINE_DRAG_MODE = 1;
   static final int FASTER_DRAG_MODE = 2;
   int dragMode = 0;
   private transient Rectangle currentBounds = null;
   private transient Graphics desktopGraphics = null;
   private transient Rectangle desktopBounds = null;
   private transient Rectangle[] floatingItems = new Rectangle[0];
   private transient boolean didDrag;
   private transient Point currentLoc = null;

   public void openFrame(JInternalFrame var1) {
      if (var1.getDesktopIcon().getParent() != null) {
         var1.getDesktopIcon().getParent().add(var1);
         this.removeIconFor(var1);
      }

   }

   public void closeFrame(JInternalFrame var1) {
      JDesktopPane var2 = var1.getDesktopPane();
      if (var2 != null) {
         boolean var3 = var1.isSelected();
         Container var4 = var1.getParent();
         JInternalFrame var5 = null;
         if (var3) {
            var5 = var2.getNextFrame(var1);

            try {
               var1.setSelected(false);
            } catch (PropertyVetoException var8) {
            }
         }

         if (var4 != null) {
            var4.remove(var1);
            var4.repaint(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
         }

         this.removeIconFor(var1);
         if (var1.getNormalBounds() != null) {
            var1.setNormalBounds((Rectangle)null);
         }

         if (this.wasIcon(var1)) {
            this.setWasIcon(var1, (Boolean)null);
         }

         if (var5 != null) {
            try {
               var5.setSelected(true);
            } catch (PropertyVetoException var7) {
            }
         } else if (var3 && var2.getComponentCount() == 0) {
            var2.requestFocus();
         }

      }
   }

   public void maximizeFrame(JInternalFrame var1) {
      if (var1.isIcon()) {
         try {
            var1.setIcon(false);
         } catch (PropertyVetoException var4) {
         }
      } else {
         var1.setNormalBounds(var1.getBounds());
         Rectangle var2 = var1.getParent().getBounds();
         this.setBoundsForFrame(var1, 0, 0, var2.width, var2.height);
      }

      try {
         var1.setSelected(true);
      } catch (PropertyVetoException var3) {
      }

   }

   public void minimizeFrame(JInternalFrame var1) {
      if (var1.isIcon()) {
         this.iconifyFrame(var1);
      } else {
         if (var1.getNormalBounds() != null) {
            Rectangle var2 = var1.getNormalBounds();
            var1.setNormalBounds((Rectangle)null);

            try {
               var1.setSelected(true);
            } catch (PropertyVetoException var4) {
            }

            this.setBoundsForFrame(var1, var2.x, var2.y, var2.width, var2.height);
         }

      }
   }

   public void iconifyFrame(JInternalFrame var1) {
      Container var3 = var1.getParent();
      JDesktopPane var4 = var1.getDesktopPane();
      boolean var5 = var1.isSelected();
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      if (!this.wasIcon(var1)) {
         Rectangle var6 = this.getBoundsForIconOf(var1);
         var2.setBounds(var6.x, var6.y, var6.width, var6.height);
         var2.revalidate();
         this.setWasIcon(var1, Boolean.TRUE);
      }

      if (var3 != null && var4 != null) {
         if (var3 instanceof JLayeredPane) {
            JLayeredPane var8 = (JLayeredPane)var3;
            int var7 = JLayeredPane.getLayer((JComponent)var1);
            JLayeredPane.putLayer(var2, var7);
         }

         if (!var1.isMaximum()) {
            var1.setNormalBounds(var1.getBounds());
         }

         var4.setComponentOrderCheckingEnabled(false);
         var3.remove(var1);
         var3.add(var2);
         var4.setComponentOrderCheckingEnabled(true);
         var3.repaint(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
         if (var5 && var4.selectFrame(true) == null) {
            var1.restoreSubcomponentFocus();
         }

      }
   }

   public void deiconifyFrame(JInternalFrame var1) {
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      Container var3 = var2.getParent();
      JDesktopPane var4 = var1.getDesktopPane();
      if (var3 != null && var4 != null) {
         var3.add(var1);
         if (var1.isMaximum()) {
            Rectangle var5 = var3.getBounds();
            if (var1.getWidth() != var5.width || var1.getHeight() != var5.height) {
               this.setBoundsForFrame(var1, 0, 0, var5.width, var5.height);
            }
         }

         this.removeIconFor(var1);
         if (var1.isSelected()) {
            var1.moveToFront();
            var1.restoreSubcomponentFocus();
         } else {
            try {
               var1.setSelected(true);
            } catch (PropertyVetoException var6) {
            }
         }
      }

   }

   public void activateFrame(JInternalFrame var1) {
      Container var2 = var1.getParent();
      JDesktopPane var4 = var1.getDesktopPane();
      JInternalFrame var5 = var4 == null ? null : var4.getSelectedFrame();
      if (var2 == null) {
         var2 = var1.getDesktopIcon().getParent();
         if (var2 == null) {
            return;
         }
      }

      if (var5 == null) {
         if (var4 != null) {
            var4.setSelectedFrame(var1);
         }
      } else if (var5 != var1) {
         if (var5.isSelected()) {
            try {
               var5.setSelected(false);
            } catch (PropertyVetoException var7) {
            }
         }

         if (var4 != null) {
            var4.setSelectedFrame(var1);
         }
      }

      var1.moveToFront();
   }

   public void deactivateFrame(JInternalFrame var1) {
      JDesktopPane var2 = var1.getDesktopPane();
      JInternalFrame var3 = var2 == null ? null : var2.getSelectedFrame();
      if (var3 == var1) {
         var2.setSelectedFrame((JInternalFrame)null);
      }

   }

   public void beginDraggingFrame(JComponent var1) {
      this.setupDragMode(var1);
      if (this.dragMode == 2) {
         Container var2 = var1.getParent();
         this.floatingItems = this.findFloatingItems(var1);
         this.currentBounds = var1.getBounds();
         if (var2 instanceof JComponent) {
            this.desktopBounds = ((JComponent)var2).getVisibleRect();
         } else {
            this.desktopBounds = var2.getBounds();
            this.desktopBounds.x = this.desktopBounds.y = 0;
         }

         this.desktopGraphics = JComponent.safelyGetGraphics(var2);
         ((JInternalFrame)var1).isDragging = true;
         this.didDrag = false;
      }

   }

   private void setupDragMode(JComponent var1) {
      JDesktopPane var2 = this.getDesktopPane(var1);
      Container var3 = var1.getParent();
      this.dragMode = 0;
      if (var2 != null) {
         String var4 = (String)var2.getClientProperty("JDesktopPane.dragMode");
         Window var5 = SwingUtilities.getWindowAncestor(var1);
         if (var5 != null && !AWTUtilities.isWindowOpaque(var5)) {
            this.dragMode = 0;
         } else if (var4 != null && var4.equals("outline")) {
            this.dragMode = 1;
         } else if (var4 == null || !var4.equals("faster") || !(var1 instanceof JInternalFrame) || !((JInternalFrame)var1).isOpaque() || var3 != null && !var3.isOpaque()) {
            if (var2.getDragMode() == 1) {
               this.dragMode = 1;
            } else if (var2.getDragMode() == 0 && var1 instanceof JInternalFrame && ((JInternalFrame)var1).isOpaque()) {
               this.dragMode = 2;
            } else {
               this.dragMode = 0;
            }
         } else {
            this.dragMode = 2;
         }
      }

   }

   public void dragFrame(JComponent var1, int var2, int var3) {
      if (this.dragMode == 1) {
         JDesktopPane var4 = this.getDesktopPane(var1);
         if (var4 != null) {
            Graphics var5 = JComponent.safelyGetGraphics(var4);
            var5.setXORMode(Color.white);
            if (this.currentLoc != null) {
               var5.drawRect(this.currentLoc.x, this.currentLoc.y, var1.getWidth() - 1, var1.getHeight() - 1);
            }

            var5.drawRect(var2, var3, var1.getWidth() - 1, var1.getHeight() - 1);
            SurfaceData var6 = ((SunGraphics2D)var5).getSurfaceData();
            if (!var6.isSurfaceLost()) {
               this.currentLoc = new Point(var2, var3);
            }

            var5.dispose();
         }
      } else if (this.dragMode == 2) {
         this.dragFrameFaster(var1, var2, var3);
      } else {
         this.setBoundsForFrame(var1, var2, var3, var1.getWidth(), var1.getHeight());
      }

   }

   public void endDraggingFrame(JComponent var1) {
      if (this.dragMode == 1 && this.currentLoc != null) {
         this.setBoundsForFrame(var1, this.currentLoc.x, this.currentLoc.y, var1.getWidth(), var1.getHeight());
         this.currentLoc = null;
      } else if (this.dragMode == 2) {
         this.currentBounds = null;
         if (this.desktopGraphics != null) {
            this.desktopGraphics.dispose();
            this.desktopGraphics = null;
         }

         this.desktopBounds = null;
         ((JInternalFrame)var1).isDragging = false;
      }

   }

   public void beginResizingFrame(JComponent var1, int var2) {
      this.setupDragMode(var1);
   }

   public void resizeFrame(JComponent var1, int var2, int var3, int var4, int var5) {
      if (this.dragMode != 0 && this.dragMode != 2) {
         JDesktopPane var6 = this.getDesktopPane(var1);
         if (var6 != null) {
            Graphics var7 = JComponent.safelyGetGraphics(var6);
            var7.setXORMode(Color.white);
            if (this.currentBounds != null) {
               var7.drawRect(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width - 1, this.currentBounds.height - 1);
            }

            var7.drawRect(var2, var3, var4 - 1, var5 - 1);
            SurfaceData var8 = ((SunGraphics2D)var7).getSurfaceData();
            if (!var8.isSurfaceLost()) {
               this.currentBounds = new Rectangle(var2, var3, var4, var5);
            }

            var7.setPaintMode();
            var7.dispose();
         }
      } else {
         this.setBoundsForFrame(var1, var2, var3, var4, var5);
      }

   }

   public void endResizingFrame(JComponent var1) {
      if (this.dragMode == 1 && this.currentBounds != null) {
         this.setBoundsForFrame(var1, this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
         this.currentBounds = null;
      }

   }

   public void setBoundsForFrame(JComponent var1, int var2, int var3, int var4, int var5) {
      var1.setBounds(var2, var3, var4, var5);
      var1.revalidate();
   }

   protected void removeIconFor(JInternalFrame var1) {
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      Container var3 = var2.getParent();
      if (var3 != null) {
         var3.remove(var2);
         var3.repaint(var2.getX(), var2.getY(), var2.getWidth(), var2.getHeight());
      }

   }

   protected Rectangle getBoundsForIconOf(JInternalFrame var1) {
      JInternalFrame.JDesktopIcon var2 = var1.getDesktopIcon();
      Dimension var3 = var2.getPreferredSize();
      Container var4 = var1.getParent();
      if (var4 == null) {
         var4 = var1.getDesktopIcon().getParent();
      }

      if (var4 == null) {
         return new Rectangle(0, 0, var3.width, var3.height);
      } else {
         Rectangle var5 = var4.getBounds();
         Component[] var6 = var4.getComponents();
         Rectangle var7 = null;
         JInternalFrame.JDesktopIcon var8 = null;
         int var9 = 0;
         int var10 = var5.height - var3.height;
         int var11 = var3.width;
         int var12 = var3.height;
         boolean var13 = false;

         while(!var13) {
            var7 = new Rectangle(var9, var10, var11, var12);
            var13 = true;

            for(int var14 = 0; var14 < var6.length; ++var14) {
               if (var6[var14] instanceof JInternalFrame) {
                  var8 = ((JInternalFrame)var6[var14]).getDesktopIcon();
               } else {
                  if (!(var6[var14] instanceof JInternalFrame.JDesktopIcon)) {
                     continue;
                  }

                  var8 = (JInternalFrame.JDesktopIcon)var6[var14];
               }

               if (!var8.equals(var2) && var7.intersects(var8.getBounds())) {
                  var13 = false;
                  break;
               }
            }

            if (var8 == null) {
               return var7;
            }

            var9 += var8.getBounds().width;
            if (var9 + var11 > var5.width) {
               var9 = 0;
               var10 -= var12;
            }
         }

         return var7;
      }
   }

   protected void setPreviousBounds(JInternalFrame var1, Rectangle var2) {
      var1.setNormalBounds(var2);
   }

   protected Rectangle getPreviousBounds(JInternalFrame var1) {
      return var1.getNormalBounds();
   }

   protected void setWasIcon(JInternalFrame var1, Boolean var2) {
      if (var2 != null) {
         var1.putClientProperty("wasIconOnce", var2);
      }

   }

   protected boolean wasIcon(JInternalFrame var1) {
      return var1.getClientProperty("wasIconOnce") == Boolean.TRUE;
   }

   JDesktopPane getDesktopPane(JComponent var1) {
      JDesktopPane var2 = null;
      Container var3 = var1.getParent();

      while(var2 == null) {
         if (var3 instanceof JDesktopPane) {
            var2 = (JDesktopPane)var3;
         } else {
            if (var3 == null) {
               break;
            }

            var3 = var3.getParent();
         }
      }

      return var2;
   }

   private void dragFrameFaster(JComponent var1, int var2, int var3) {
      Rectangle var4 = new Rectangle(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
      this.currentBounds.x = var2;
      this.currentBounds.y = var3;
      if (this.didDrag) {
         this.emergencyCleanup(var1);
      } else {
         this.didDrag = true;
         ((JInternalFrame)var1).danger = false;
      }

      boolean var5 = this.isFloaterCollision(var4, this.currentBounds);
      JComponent var6 = (JComponent)var1.getParent();
      Rectangle var7 = var4.intersection(this.desktopBounds);
      RepaintManager var8 = RepaintManager.currentManager(var1);
      var8.beginPaint();

      try {
         if (!var5) {
            var8.copyArea(var6, this.desktopGraphics, var7.x, var7.y, var7.width, var7.height, var2 - var4.x, var3 - var4.y, true);
         }

         var1.setBounds(this.currentBounds);
         Rectangle var9;
         if (!var5) {
            var9 = this.currentBounds;
            var8.notifyRepaintPerformed(var6, var9.x, var9.y, var9.width, var9.height);
         }

         if (var5) {
            ((JInternalFrame)var1).isDragging = false;
            var6.paintImmediately(this.currentBounds);
            ((JInternalFrame)var1).isDragging = true;
         }

         var8.markCompletelyClean(var6);
         var8.markCompletelyClean(var1);
         var9 = null;
         Rectangle[] var15;
         if (var4.intersects(this.currentBounds)) {
            var15 = SwingUtilities.computeDifference(var4, this.currentBounds);
         } else {
            var15 = new Rectangle[]{var4};
         }

         int var10 = 0;

         label121:
         while(true) {
            Rectangle var11;
            if (var10 >= var15.length) {
               if (var7.equals(var4)) {
                  break;
               }

               var15 = SwingUtilities.computeDifference(var4, this.desktopBounds);
               var10 = 0;

               while(true) {
                  if (var10 >= var15.length) {
                     break label121;
                  }

                  var15[var10].x += var2 - var4.x;
                  var15[var10].y += var3 - var4.y;
                  ((JInternalFrame)var1).isDragging = false;
                  var6.paintImmediately(var15[var10]);
                  ((JInternalFrame)var1).isDragging = true;
                  var11 = var15[var10];
                  var8.notifyRepaintPerformed(var6, var11.x, var11.y, var11.width, var11.height);
                  ++var10;
               }
            }

            var6.paintImmediately(var15[var10]);
            var11 = var15[var10];
            var8.notifyRepaintPerformed(var6, var11.x, var11.y, var11.width, var11.height);
            ++var10;
         }
      } finally {
         var8.endPaint();
      }

      Window var16 = SwingUtilities.getWindowAncestor(var1);
      Toolkit var17 = Toolkit.getDefaultToolkit();
      if (!var16.isOpaque() && var17 instanceof SunToolkit && ((SunToolkit)var17).needUpdateWindow()) {
         AWTAccessor.getWindowAccessor().updateWindow(var16);
      }

   }

   private boolean isFloaterCollision(Rectangle var1, Rectangle var2) {
      if (this.floatingItems.length == 0) {
         return false;
      } else {
         for(int var3 = 0; var3 < this.floatingItems.length; ++var3) {
            boolean var4 = var1.intersects(this.floatingItems[var3]);
            if (var4) {
               return true;
            }

            boolean var5 = var2.intersects(this.floatingItems[var3]);
            if (var5) {
               return true;
            }
         }

         return false;
      }
   }

   private Rectangle[] findFloatingItems(JComponent var1) {
      Container var2 = var1.getParent();
      Component[] var3 = var2.getComponents();
      boolean var4 = false;

      int var6;
      for(var6 = 0; var6 < var3.length && var3[var6] != var1; ++var6) {
      }

      Rectangle[] var5 = new Rectangle[var6];

      for(var6 = 0; var6 < var5.length; ++var6) {
         var5[var6] = var3[var6].getBounds();
      }

      return var5;
   }

   private void emergencyCleanup(final JComponent var1) {
      if (((JInternalFrame)var1).danger) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
               ((JInternalFrame)var1).isDragging = false;
               var1.paintImmediately(0, 0, var1.getWidth(), var1.getHeight());
               ((JInternalFrame)var1).isDragging = true;
            }
         });
         ((JInternalFrame)var1).danger = false;
      }

   }
}
