package javax.swing.plaf.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DesktopIconUI;

public class BasicDesktopIconUI extends DesktopIconUI {
   protected JInternalFrame.JDesktopIcon desktopIcon;
   protected JInternalFrame frame;
   protected JComponent iconPane;
   MouseInputListener mouseInputListener;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicDesktopIconUI();
   }

   public void installUI(JComponent var1) {
      this.desktopIcon = (JInternalFrame.JDesktopIcon)var1;
      this.frame = this.desktopIcon.getInternalFrame();
      this.installDefaults();
      this.installComponents();
      JInternalFrame var2 = this.desktopIcon.getInternalFrame();
      if (var2.isIcon() && var2.getParent() == null) {
         JDesktopPane var3 = this.desktopIcon.getDesktopPane();
         if (var3 != null) {
            DesktopManager var4 = var3.getDesktopManager();
            if (var4 instanceof DefaultDesktopManager) {
               var4.iconifyFrame(var2);
            }
         }
      }

      this.installListeners();
      JLayeredPane.putLayer(this.desktopIcon, JLayeredPane.getLayer((JComponent)this.frame));
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallComponents();
      JInternalFrame var2 = this.desktopIcon.getInternalFrame();
      if (var2.isIcon()) {
         JDesktopPane var3 = this.desktopIcon.getDesktopPane();
         if (var3 != null) {
            DesktopManager var4 = var3.getDesktopManager();
            if (var4 instanceof DefaultDesktopManager) {
               var2.putClientProperty("wasIconOnce", (Object)null);
               this.desktopIcon.setLocation(Integer.MIN_VALUE, 0);
            }
         }
      }

      this.uninstallListeners();
      this.frame = null;
      this.desktopIcon = null;
   }

   protected void installComponents() {
      this.iconPane = new BasicInternalFrameTitlePane(this.frame);
      this.desktopIcon.setLayout(new BorderLayout());
      this.desktopIcon.add(this.iconPane, "Center");
   }

   protected void uninstallComponents() {
      this.desktopIcon.remove(this.iconPane);
      this.desktopIcon.setLayout((LayoutManager)null);
      this.iconPane = null;
   }

   protected void installListeners() {
      this.mouseInputListener = this.createMouseInputListener();
      this.desktopIcon.addMouseMotionListener(this.mouseInputListener);
      this.desktopIcon.addMouseListener(this.mouseInputListener);
   }

   protected void uninstallListeners() {
      this.desktopIcon.removeMouseMotionListener(this.mouseInputListener);
      this.desktopIcon.removeMouseListener(this.mouseInputListener);
      this.mouseInputListener = null;
   }

   protected void installDefaults() {
      LookAndFeel.installBorder(this.desktopIcon, "DesktopIcon.border");
      LookAndFeel.installProperty(this.desktopIcon, "opaque", Boolean.TRUE);
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.desktopIcon);
   }

   protected MouseInputListener createMouseInputListener() {
      return new BasicDesktopIconUI.MouseInputHandler();
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.desktopIcon.getLayout().preferredLayoutSize(this.desktopIcon);
   }

   public Dimension getMinimumSize(JComponent var1) {
      Dimension var2 = new Dimension(this.iconPane.getMinimumSize());
      Border var3 = this.frame.getBorder();
      if (var3 != null) {
         var2.height += var3.getBorderInsets(this.frame).bottom + var3.getBorderInsets(this.frame).top;
      }

      return var2;
   }

   public Dimension getMaximumSize(JComponent var1) {
      return this.iconPane.getMaximumSize();
   }

   public Insets getInsets(JComponent var1) {
      JInternalFrame var2 = this.desktopIcon.getInternalFrame();
      Border var3 = var2.getBorder();
      return var3 != null ? var3.getBorderInsets(var2) : new Insets(0, 0, 0, 0);
   }

   public void deiconize() {
      try {
         this.frame.setIcon(false);
      } catch (PropertyVetoException var2) {
      }

   }

   public class MouseInputHandler extends MouseInputAdapter {
      int _x;
      int _y;
      int __x;
      int __y;
      Rectangle startingBounds;

      public void mouseReleased(MouseEvent var1) {
         this._x = 0;
         this._y = 0;
         this.__x = 0;
         this.__y = 0;
         this.startingBounds = null;
         JDesktopPane var2;
         if ((var2 = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
            DesktopManager var3 = var2.getDesktopManager();
            var3.endDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
         }

      }

      public void mousePressed(MouseEvent var1) {
         Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
         this.__x = var1.getX();
         this.__y = var1.getY();
         this._x = var2.x;
         this._y = var2.y;
         this.startingBounds = BasicDesktopIconUI.this.desktopIcon.getBounds();
         JDesktopPane var3;
         if ((var3 = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
            DesktopManager var4 = var3.getDesktopManager();
            var4.beginDraggingFrame(BasicDesktopIconUI.this.desktopIcon);
         }

         try {
            BasicDesktopIconUI.this.frame.setSelected(true);
         } catch (PropertyVetoException var5) {
         }

         if (BasicDesktopIconUI.this.desktopIcon.getParent() instanceof JLayeredPane) {
            ((JLayeredPane)BasicDesktopIconUI.this.desktopIcon.getParent()).moveToFront(BasicDesktopIconUI.this.desktopIcon);
         }

         if (var1.getClickCount() > 1 && BasicDesktopIconUI.this.frame.isIconifiable() && BasicDesktopIconUI.this.frame.isIcon()) {
            BasicDesktopIconUI.this.deiconize();
         }

      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseDragged(MouseEvent var1) {
         Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
         Insets var11 = BasicDesktopIconUI.this.desktopIcon.getInsets();
         int var12 = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getWidth();
         int var13 = ((JComponent)BasicDesktopIconUI.this.desktopIcon.getParent()).getHeight();
         if (this.startingBounds != null) {
            int var3 = this.startingBounds.x - (this._x - var2.x);
            int var4 = this.startingBounds.y - (this._y - var2.y);
            if (var3 + var11.left <= -this.__x) {
               var3 = -this.__x - var11.left;
            }

            if (var4 + var11.top <= -this.__y) {
               var4 = -this.__y - var11.top;
            }

            if (var3 + this.__x + var11.right > var12) {
               var3 = var12 - this.__x - var11.right;
            }

            if (var4 + this.__y + var11.bottom > var13) {
               var4 = var13 - this.__y - var11.bottom;
            }

            JDesktopPane var14;
            if ((var14 = BasicDesktopIconUI.this.desktopIcon.getDesktopPane()) != null) {
               DesktopManager var15 = var14.getDesktopManager();
               var15.dragFrame(BasicDesktopIconUI.this.desktopIcon, var3, var4);
            } else {
               this.moveAndRepaint(BasicDesktopIconUI.this.desktopIcon, var3, var4, BasicDesktopIconUI.this.desktopIcon.getWidth(), BasicDesktopIconUI.this.desktopIcon.getHeight());
            }

         }
      }

      public void moveAndRepaint(JComponent var1, int var2, int var3, int var4, int var5) {
         Rectangle var6 = var1.getBounds();
         var1.setBounds(var2, var3, var4, var5);
         SwingUtilities.computeUnion(var2, var3, var4, var5, var6);
         var1.getParent().repaint(var6.x, var6.y, var6.width, var6.height);
      }
   }
}
