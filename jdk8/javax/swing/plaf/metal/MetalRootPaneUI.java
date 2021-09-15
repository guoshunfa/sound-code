package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class MetalRootPaneUI extends BasicRootPaneUI {
   private static final String[] borderKeys = new String[]{null, "RootPane.frameBorder", "RootPane.plainDialogBorder", "RootPane.informationDialogBorder", "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder", "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder", "RootPane.warningDialogBorder"};
   private static final int CORNER_DRAG_WIDTH = 16;
   private static final int BORDER_DRAG_THICKNESS = 5;
   private Window window;
   private JComponent titlePane;
   private MouseInputListener mouseInputListener;
   private LayoutManager layoutManager;
   private LayoutManager savedOldLayout;
   private JRootPane root;
   private Cursor lastCursor = Cursor.getPredefinedCursor(0);
   private static final int[] cursorMapping = new int[]{6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 4, 4, 9, 5, 5};

   public static ComponentUI createUI(JComponent var0) {
      return new MetalRootPaneUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      this.root = (JRootPane)var1;
      int var2 = this.root.getWindowDecorationStyle();
      if (var2 != 0) {
         this.installClientDecorations(this.root);
      }

   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      this.uninstallClientDecorations(this.root);
      this.layoutManager = null;
      this.mouseInputListener = null;
      this.root = null;
   }

   void installBorder(JRootPane var1) {
      int var2 = var1.getWindowDecorationStyle();
      if (var2 == 0) {
         LookAndFeel.uninstallBorder(var1);
      } else {
         LookAndFeel.installBorder(var1, borderKeys[var2]);
      }

   }

   private void uninstallBorder(JRootPane var1) {
      LookAndFeel.uninstallBorder(var1);
   }

   private void installWindowListeners(JRootPane var1, Component var2) {
      if (var2 instanceof Window) {
         this.window = (Window)var2;
      } else {
         this.window = SwingUtilities.getWindowAncestor(var2);
      }

      if (this.window != null) {
         if (this.mouseInputListener == null) {
            this.mouseInputListener = this.createWindowMouseInputListener(var1);
         }

         this.window.addMouseListener(this.mouseInputListener);
         this.window.addMouseMotionListener(this.mouseInputListener);
      }

   }

   private void uninstallWindowListeners(JRootPane var1) {
      if (this.window != null) {
         this.window.removeMouseListener(this.mouseInputListener);
         this.window.removeMouseMotionListener(this.mouseInputListener);
      }

   }

   private void installLayout(JRootPane var1) {
      if (this.layoutManager == null) {
         this.layoutManager = this.createLayoutManager();
      }

      this.savedOldLayout = var1.getLayout();
      var1.setLayout(this.layoutManager);
   }

   private void uninstallLayout(JRootPane var1) {
      if (this.savedOldLayout != null) {
         var1.setLayout(this.savedOldLayout);
         this.savedOldLayout = null;
      }

   }

   private void installClientDecorations(JRootPane var1) {
      this.installBorder(var1);
      JComponent var2 = this.createTitlePane(var1);
      this.setTitlePane(var1, var2);
      this.installWindowListeners(var1, var1.getParent());
      this.installLayout(var1);
      if (this.window != null) {
         var1.revalidate();
         var1.repaint();
      }

   }

   private void uninstallClientDecorations(JRootPane var1) {
      this.uninstallBorder(var1);
      this.uninstallWindowListeners(var1);
      this.setTitlePane(var1, (JComponent)null);
      this.uninstallLayout(var1);
      int var2 = var1.getWindowDecorationStyle();
      if (var2 == 0) {
         var1.repaint();
         var1.revalidate();
      }

      if (this.window != null) {
         this.window.setCursor(Cursor.getPredefinedCursor(0));
      }

      this.window = null;
   }

   private JComponent createTitlePane(JRootPane var1) {
      return new MetalTitlePane(var1, this);
   }

   private MouseInputListener createWindowMouseInputListener(JRootPane var1) {
      return new MetalRootPaneUI.MouseInputHandler();
   }

   private LayoutManager createLayoutManager() {
      return new MetalRootPaneUI.MetalRootLayout();
   }

   private void setTitlePane(JRootPane var1, JComponent var2) {
      JLayeredPane var3 = var1.getLayeredPane();
      JComponent var4 = this.getTitlePane();
      if (var4 != null) {
         var4.setVisible(false);
         var3.remove(var4);
      }

      if (var2 != null) {
         var3.add(var2, JLayeredPane.FRAME_CONTENT_LAYER);
         var2.setVisible(true);
      }

      this.titlePane = var2;
   }

   private JComponent getTitlePane() {
      return this.titlePane;
   }

   private JRootPane getRootPane() {
      return this.root;
   }

   public void propertyChange(PropertyChangeEvent var1) {
      super.propertyChange(var1);
      String var2 = var1.getPropertyName();
      if (var2 != null) {
         if (var2.equals("windowDecorationStyle")) {
            JRootPane var3 = (JRootPane)var1.getSource();
            int var4 = var3.getWindowDecorationStyle();
            this.uninstallClientDecorations(var3);
            if (var4 != 0) {
               this.installClientDecorations(var3);
            }
         } else if (var2.equals("ancestor")) {
            this.uninstallWindowListeners(this.root);
            if (((JRootPane)var1.getSource()).getWindowDecorationStyle() != 0) {
               this.installWindowListeners(this.root, this.root.getParent());
            }
         }

      }
   }

   private class MouseInputHandler implements MouseInputListener {
      private boolean isMovingWindow;
      private int dragCursor;
      private int dragOffsetX;
      private int dragOffsetY;
      private int dragWidth;
      private int dragHeight;

      private MouseInputHandler() {
      }

      public void mousePressed(MouseEvent var1) {
         JRootPane var2 = MetalRootPaneUI.this.getRootPane();
         if (var2.getWindowDecorationStyle() != 0) {
            Point var3 = var1.getPoint();
            Window var4 = (Window)var1.getSource();
            if (var4 != null) {
               var4.toFront();
            }

            Point var5 = SwingUtilities.convertPoint(var4, var3, MetalRootPaneUI.this.getTitlePane());
            Frame var6 = null;
            Dialog var7 = null;
            if (var4 instanceof Frame) {
               var6 = (Frame)var4;
            } else if (var4 instanceof Dialog) {
               var7 = (Dialog)var4;
            }

            int var8 = var6 != null ? var6.getExtendedState() : 0;
            if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(var5)) {
               if ((var6 != null && (var8 & 6) == 0 || var7 != null) && var3.y >= 5 && var3.x >= 5 && var3.x < var4.getWidth() - 5) {
                  this.isMovingWindow = true;
                  this.dragOffsetX = var3.x;
                  this.dragOffsetY = var3.y;
               }
            } else if (var6 != null && var6.isResizable() && (var8 & 6) == 0 || var7 != null && var7.isResizable()) {
               this.dragOffsetX = var3.x;
               this.dragOffsetY = var3.y;
               this.dragWidth = var4.getWidth();
               this.dragHeight = var4.getHeight();
               this.dragCursor = this.getCursor(this.calculateCorner(var4, var3.x, var3.y));
            }

         }
      }

      public void mouseReleased(MouseEvent var1) {
         if (this.dragCursor != 0 && MetalRootPaneUI.this.window != null && !MetalRootPaneUI.this.window.isValid()) {
            MetalRootPaneUI.this.window.validate();
            MetalRootPaneUI.this.getRootPane().repaint();
         }

         this.isMovingWindow = false;
         this.dragCursor = 0;
      }

      public void mouseMoved(MouseEvent var1) {
         JRootPane var2 = MetalRootPaneUI.this.getRootPane();
         if (var2.getWindowDecorationStyle() != 0) {
            Window var3 = (Window)var1.getSource();
            Frame var4 = null;
            Dialog var5 = null;
            if (var3 instanceof Frame) {
               var4 = (Frame)var3;
            } else if (var3 instanceof Dialog) {
               var5 = (Dialog)var3;
            }

            int var6 = this.getCursor(this.calculateCorner(var3, var1.getX(), var1.getY()));
            if (var6 == 0 || (var4 == null || !var4.isResizable() || (var4.getExtendedState() & 6) != 0) && (var5 == null || !var5.isResizable())) {
               var3.setCursor(MetalRootPaneUI.this.lastCursor);
            } else {
               var3.setCursor(Cursor.getPredefinedCursor(var6));
            }

         }
      }

      private void adjust(Rectangle var1, Dimension var2, int var3, int var4, int var5, int var6) {
         var1.x += var3;
         var1.y += var4;
         var1.width += var5;
         var1.height += var6;
         if (var2 != null) {
            int var7;
            if (var1.width < var2.width) {
               var7 = var2.width - var1.width;
               if (var3 != 0) {
                  var1.x -= var7;
               }

               var1.width = var2.width;
            }

            if (var1.height < var2.height) {
               var7 = var2.height - var1.height;
               if (var4 != 0) {
                  var1.y -= var7;
               }

               var1.height = var2.height;
            }
         }

      }

      public void mouseDragged(MouseEvent var1) {
         Window var2 = (Window)var1.getSource();
         Point var3 = var1.getPoint();
         if (this.isMovingWindow) {
            Point var4 = var1.getLocationOnScreen();
            var2.setLocation(var4.x - this.dragOffsetX, var4.y - this.dragOffsetY);
         } else if (this.dragCursor != 0) {
            Rectangle var7 = var2.getBounds();
            Rectangle var5 = new Rectangle(var7);
            Dimension var6 = var2.getMinimumSize();
            switch(this.dragCursor) {
            case 4:
               this.adjust(var7, var6, var3.x - this.dragOffsetX, 0, -(var3.x - this.dragOffsetX), var3.y + (this.dragHeight - this.dragOffsetY) - var7.height);
               break;
            case 5:
               this.adjust(var7, var6, 0, 0, var3.x + (this.dragWidth - this.dragOffsetX) - var7.width, var3.y + (this.dragHeight - this.dragOffsetY) - var7.height);
               break;
            case 6:
               this.adjust(var7, var6, var3.x - this.dragOffsetX, var3.y - this.dragOffsetY, -(var3.x - this.dragOffsetX), -(var3.y - this.dragOffsetY));
               break;
            case 7:
               this.adjust(var7, var6, 0, var3.y - this.dragOffsetY, var3.x + (this.dragWidth - this.dragOffsetX) - var7.width, -(var3.y - this.dragOffsetY));
               break;
            case 8:
               this.adjust(var7, var6, 0, var3.y - this.dragOffsetY, 0, -(var3.y - this.dragOffsetY));
               break;
            case 9:
               this.adjust(var7, var6, 0, 0, 0, var3.y + (this.dragHeight - this.dragOffsetY) - var7.height);
               break;
            case 10:
               this.adjust(var7, var6, var3.x - this.dragOffsetX, 0, -(var3.x - this.dragOffsetX), 0);
               break;
            case 11:
               this.adjust(var7, var6, 0, 0, var3.x + (this.dragWidth - this.dragOffsetX) - var7.width, 0);
            }

            if (!var7.equals(var5)) {
               var2.setBounds(var7);
               if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
                  var2.validate();
                  MetalRootPaneUI.this.getRootPane().repaint();
               }
            }
         }

      }

      public void mouseEntered(MouseEvent var1) {
         Window var2 = (Window)var1.getSource();
         MetalRootPaneUI.this.lastCursor = var2.getCursor();
         this.mouseMoved(var1);
      }

      public void mouseExited(MouseEvent var1) {
         Window var2 = (Window)var1.getSource();
         var2.setCursor(MetalRootPaneUI.this.lastCursor);
      }

      public void mouseClicked(MouseEvent var1) {
         Window var2 = (Window)var1.getSource();
         Frame var3 = null;
         if (var2 instanceof Frame) {
            var3 = (Frame)var2;
            Point var4 = SwingUtilities.convertPoint(var2, var1.getPoint(), MetalRootPaneUI.this.getTitlePane());
            int var5 = var3.getExtendedState();
            if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(var4) && var1.getClickCount() % 2 == 0 && (var1.getModifiers() & 16) != 0 && var3.isResizable()) {
               if ((var5 & 6) != 0) {
                  var3.setExtendedState(var5 & -7);
               } else {
                  var3.setExtendedState(var5 | 6);
               }

            }
         }
      }

      private int calculateCorner(Window var1, int var2, int var3) {
         Insets var4 = var1.getInsets();
         int var5 = this.calculatePosition(var2 - var4.left, var1.getWidth() - var4.left - var4.right);
         int var6 = this.calculatePosition(var3 - var4.top, var1.getHeight() - var4.top - var4.bottom);
         return var5 != -1 && var6 != -1 ? var6 * 5 + var5 : -1;
      }

      private int getCursor(int var1) {
         return var1 == -1 ? 0 : MetalRootPaneUI.cursorMapping[var1];
      }

      private int calculatePosition(int var1, int var2) {
         if (var1 < 5) {
            return 0;
         } else if (var1 < 16) {
            return 1;
         } else if (var1 >= var2 - 5) {
            return 4;
         } else {
            return var1 >= var2 - 16 ? 3 : 2;
         }
      }

      // $FF: synthetic method
      MouseInputHandler(Object var2) {
         this();
      }
   }

   private static class MetalRootLayout implements LayoutManager2 {
      private MetalRootLayout() {
      }

      public Dimension preferredLayoutSize(Container var1) {
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         boolean var10 = false;
         Insets var11 = var1.getInsets();
         JRootPane var12 = (JRootPane)var1;
         Dimension var2;
         if (var12.getContentPane() != null) {
            var2 = var12.getContentPane().getPreferredSize();
         } else {
            var2 = var12.getSize();
         }

         if (var2 != null) {
            var5 = var2.width;
            var6 = var2.height;
         }

         if (var12.getMenuBar() != null) {
            Dimension var3 = var12.getMenuBar().getPreferredSize();
            if (var3 != null) {
               var7 = var3.width;
               var8 = var3.height;
            }
         }

         if (var12.getWindowDecorationStyle() != 0 && var12.getUI() instanceof MetalRootPaneUI) {
            JComponent var13 = ((MetalRootPaneUI)var12.getUI()).getTitlePane();
            if (var13 != null) {
               Dimension var4 = var13.getPreferredSize();
               if (var4 != null) {
                  var9 = var4.width;
                  int var14 = var4.height;
               }
            }
         }

         return new Dimension(Math.max(Math.max(var5, var7), var9) + var11.left + var11.right, var6 + var8 + var9 + var11.top + var11.bottom);
      }

      public Dimension minimumLayoutSize(Container var1) {
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         boolean var10 = false;
         Insets var11 = var1.getInsets();
         JRootPane var12 = (JRootPane)var1;
         Dimension var2;
         if (var12.getContentPane() != null) {
            var2 = var12.getContentPane().getMinimumSize();
         } else {
            var2 = var12.getSize();
         }

         if (var2 != null) {
            var5 = var2.width;
            var6 = var2.height;
         }

         if (var12.getMenuBar() != null) {
            Dimension var3 = var12.getMenuBar().getMinimumSize();
            if (var3 != null) {
               var7 = var3.width;
               var8 = var3.height;
            }
         }

         if (var12.getWindowDecorationStyle() != 0 && var12.getUI() instanceof MetalRootPaneUI) {
            JComponent var13 = ((MetalRootPaneUI)var12.getUI()).getTitlePane();
            if (var13 != null) {
               Dimension var4 = var13.getMinimumSize();
               if (var4 != null) {
                  var9 = var4.width;
                  int var14 = var4.height;
               }
            }
         }

         return new Dimension(Math.max(Math.max(var5, var7), var9) + var11.left + var11.right, var6 + var8 + var9 + var11.top + var11.bottom);
      }

      public Dimension maximumLayoutSize(Container var1) {
         int var5 = Integer.MAX_VALUE;
         int var6 = Integer.MAX_VALUE;
         int var7 = Integer.MAX_VALUE;
         int var8 = Integer.MAX_VALUE;
         int var9 = Integer.MAX_VALUE;
         int var10 = Integer.MAX_VALUE;
         Insets var11 = var1.getInsets();
         JRootPane var12 = (JRootPane)var1;
         if (var12.getContentPane() != null) {
            Dimension var2 = var12.getContentPane().getMaximumSize();
            if (var2 != null) {
               var5 = var2.width;
               var6 = var2.height;
            }
         }

         if (var12.getMenuBar() != null) {
            Dimension var3 = var12.getMenuBar().getMaximumSize();
            if (var3 != null) {
               var7 = var3.width;
               var8 = var3.height;
            }
         }

         if (var12.getWindowDecorationStyle() != 0 && var12.getUI() instanceof MetalRootPaneUI) {
            JComponent var13 = ((MetalRootPaneUI)var12.getUI()).getTitlePane();
            if (var13 != null) {
               Dimension var4 = var13.getMaximumSize();
               if (var4 != null) {
                  var9 = var4.width;
                  var10 = var4.height;
               }
            }
         }

         int var15 = Math.max(Math.max(var6, var8), var10);
         if (var15 != Integer.MAX_VALUE) {
            var15 = var6 + var8 + var10 + var11.top + var11.bottom;
         }

         int var14 = Math.max(Math.max(var5, var7), var9);
         if (var14 != Integer.MAX_VALUE) {
            var14 += var11.left + var11.right;
         }

         return new Dimension(var14, var15);
      }

      public void layoutContainer(Container var1) {
         JRootPane var2 = (JRootPane)var1;
         Rectangle var3 = var2.getBounds();
         Insets var4 = var2.getInsets();
         int var5 = 0;
         int var6 = var3.width - var4.right - var4.left;
         int var7 = var3.height - var4.top - var4.bottom;
         if (var2.getLayeredPane() != null) {
            var2.getLayeredPane().setBounds(var4.left, var4.top, var6, var7);
         }

         if (var2.getGlassPane() != null) {
            var2.getGlassPane().setBounds(var4.left, var4.top, var6, var7);
         }

         if (var2.getWindowDecorationStyle() != 0 && var2.getUI() instanceof MetalRootPaneUI) {
            JComponent var8 = ((MetalRootPaneUI)var2.getUI()).getTitlePane();
            if (var8 != null) {
               Dimension var9 = var8.getPreferredSize();
               if (var9 != null) {
                  int var10 = var9.height;
                  var8.setBounds(0, 0, var6, var10);
                  var5 += var10;
               }
            }
         }

         Dimension var11;
         if (var2.getMenuBar() != null) {
            var11 = var2.getMenuBar().getPreferredSize();
            var2.getMenuBar().setBounds(0, var5, var6, var11.height);
            var5 += var11.height;
         }

         if (var2.getContentPane() != null) {
            var11 = var2.getContentPane().getPreferredSize();
            var2.getContentPane().setBounds(0, var5, var6, var7 < var5 ? 0 : var7 - var5);
         }

      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public void addLayoutComponent(Component var1, Object var2) {
      }

      public float getLayoutAlignmentX(Container var1) {
         return 0.0F;
      }

      public float getLayoutAlignmentY(Container var1) {
         return 0.0F;
      }

      public void invalidateLayout(Container var1) {
      }

      // $FF: synthetic method
      MetalRootLayout(Object var1) {
         this();
      }
   }
}
