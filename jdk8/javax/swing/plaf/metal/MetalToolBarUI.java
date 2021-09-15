package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicToolBarUI;

public class MetalToolBarUI extends BasicToolBarUI {
   private static List<WeakReference<JComponent>> components = new ArrayList();
   protected ContainerListener contListener;
   protected PropertyChangeListener rolloverListener;
   private static Border nonRolloverBorder;
   private JMenuBar lastMenuBar;

   static synchronized void register(JComponent var0) {
      if (var0 == null) {
         throw new NullPointerException("JComponent must be non-null");
      } else {
         components.add(new WeakReference(var0));
      }
   }

   static synchronized void unregister(JComponent var0) {
      for(int var1 = components.size() - 1; var1 >= 0; --var1) {
         JComponent var2 = (JComponent)((WeakReference)components.get(var1)).get();
         if (var2 == var0 || var2 == null) {
            components.remove(var1);
         }
      }

   }

   static synchronized Object findRegisteredComponentOfType(JComponent var0, Class var1) {
      JRootPane var2 = SwingUtilities.getRootPane(var0);
      if (var2 != null) {
         for(int var3 = components.size() - 1; var3 >= 0; --var3) {
            Object var4 = ((WeakReference)components.get(var3)).get();
            if (var4 == null) {
               components.remove(var3);
            } else if (var1.isInstance(var4) && SwingUtilities.getRootPane((Component)var4) == var2) {
               return var4;
            }
         }
      }

      return null;
   }

   static boolean doesMenuBarBorderToolBar(JMenuBar var0) {
      JToolBar var1 = (JToolBar)findRegisteredComponentOfType(var0, JToolBar.class);
      if (var1 != null && var1.getOrientation() == 0) {
         JRootPane var2 = SwingUtilities.getRootPane(var0);
         Point var3 = new Point(0, 0);
         var3 = SwingUtilities.convertPoint(var0, var3, var2);
         int var4 = var3.x;
         int var5 = var3.y;
         var3.x = var3.y = 0;
         var3 = SwingUtilities.convertPoint(var1, var3, var2);
         return var3.x == var4 && var5 + var0.getHeight() == var3.y && var0.getWidth() == var1.getWidth();
      } else {
         return false;
      }
   }

   public static ComponentUI createUI(JComponent var0) {
      return new MetalToolBarUI();
   }

   public void installUI(JComponent var1) {
      super.installUI(var1);
      register(var1);
   }

   public void uninstallUI(JComponent var1) {
      super.uninstallUI(var1);
      nonRolloverBorder = null;
      unregister(var1);
   }

   protected void installListeners() {
      super.installListeners();
      this.contListener = this.createContainerListener();
      if (this.contListener != null) {
         this.toolBar.addContainerListener(this.contListener);
      }

      this.rolloverListener = this.createRolloverListener();
      if (this.rolloverListener != null) {
         this.toolBar.addPropertyChangeListener(this.rolloverListener);
      }

   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      if (this.contListener != null) {
         this.toolBar.removeContainerListener(this.contListener);
      }

      this.rolloverListener = this.createRolloverListener();
      if (this.rolloverListener != null) {
         this.toolBar.removePropertyChangeListener(this.rolloverListener);
      }

   }

   protected Border createRolloverBorder() {
      return super.createRolloverBorder();
   }

   protected Border createNonRolloverBorder() {
      return super.createNonRolloverBorder();
   }

   private Border createNonRolloverToggleBorder() {
      return this.createNonRolloverBorder();
   }

   protected void setBorderToNonRollover(Component var1) {
      if (var1 instanceof JToggleButton && !(var1 instanceof JCheckBox)) {
         JToggleButton var2 = (JToggleButton)var1;
         Border var3 = var2.getBorder();
         super.setBorderToNonRollover(var1);
         if (var3 instanceof UIResource) {
            if (nonRolloverBorder == null) {
               nonRolloverBorder = this.createNonRolloverToggleBorder();
            }

            var2.setBorder(nonRolloverBorder);
         }
      } else {
         super.setBorderToNonRollover(var1);
      }

   }

   protected ContainerListener createContainerListener() {
      return null;
   }

   protected PropertyChangeListener createRolloverListener() {
      return null;
   }

   protected MouseInputListener createDockingListener() {
      return new MetalToolBarUI.MetalDockingListener(this.toolBar);
   }

   protected void setDragOffset(Point var1) {
      if (!GraphicsEnvironment.isHeadless()) {
         if (this.dragWindow == null) {
            this.dragWindow = this.createDragWindow(this.toolBar);
         }

         this.dragWindow.setOffset(var1);
      }

   }

   public void update(Graphics var1, JComponent var2) {
      if (var1 == null) {
         throw new NullPointerException("graphics must be non-null");
      } else {
         if (var2.isOpaque() && var2.getBackground() instanceof UIResource && ((JToolBar)var2).getOrientation() == 0 && UIManager.get("MenuBar.gradient") != null) {
            JRootPane var3 = SwingUtilities.getRootPane(var2);
            JMenuBar var4 = (JMenuBar)findRegisteredComponentOfType(var2, JMenuBar.class);
            if (var4 != null && var4.isOpaque() && var4.getBackground() instanceof UIResource) {
               Point var5 = new Point(0, 0);
               var5 = SwingUtilities.convertPoint(var2, var5, var3);
               int var6 = var5.x;
               int var7 = var5.y;
               var5.x = var5.y = 0;
               var5 = SwingUtilities.convertPoint(var4, var5, var3);
               if (var5.x == var6 && var7 == var5.y + var4.getHeight() && var4.getWidth() == var2.getWidth() && MetalUtils.drawGradient(var2, var1, "MenuBar.gradient", 0, -var4.getHeight(), var2.getWidth(), var2.getHeight() + var4.getHeight(), true)) {
                  this.setLastMenuBar(var4);
                  this.paint(var1, var2);
                  return;
               }
            }

            if (MetalUtils.drawGradient(var2, var1, "MenuBar.gradient", 0, 0, var2.getWidth(), var2.getHeight(), true)) {
               this.setLastMenuBar((JMenuBar)null);
               this.paint(var1, var2);
               return;
            }
         }

         this.setLastMenuBar((JMenuBar)null);
         super.update(var1, var2);
      }
   }

   private void setLastMenuBar(JMenuBar var1) {
      if (MetalLookAndFeel.usingOcean() && this.lastMenuBar != var1) {
         if (this.lastMenuBar != null) {
            this.lastMenuBar.repaint();
         }

         if (var1 != null) {
            var1.repaint();
         }

         this.lastMenuBar = var1;
      }

   }

   protected class MetalDockingListener extends BasicToolBarUI.DockingListener {
      private boolean pressedInBumps = false;

      public MetalDockingListener(JToolBar var2) {
         super(var2);
      }

      public void mousePressed(MouseEvent var1) {
         super.mousePressed(var1);
         if (this.toolBar.isEnabled()) {
            this.pressedInBumps = false;
            Rectangle var2 = new Rectangle();
            if (this.toolBar.getOrientation() == 0) {
               int var3 = MetalUtils.isLeftToRight(this.toolBar) ? 0 : this.toolBar.getSize().width - 14;
               var2.setBounds(var3, 0, 14, this.toolBar.getSize().height);
            } else {
               var2.setBounds(0, 0, this.toolBar.getSize().width, 14);
            }

            if (var2.contains(var1.getPoint())) {
               this.pressedInBumps = true;
               Point var4 = var1.getPoint();
               if (!MetalUtils.isLeftToRight(this.toolBar)) {
                  var4.x -= this.toolBar.getSize().width - this.toolBar.getPreferredSize().width;
               }

               MetalToolBarUI.this.setDragOffset(var4);
            }

         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (this.pressedInBumps) {
            super.mouseDragged(var1);
         }

      }
   }

   protected class MetalRolloverListener extends BasicToolBarUI.PropertyListener {
      protected MetalRolloverListener() {
         super();
      }
   }

   protected class MetalContainerListener extends BasicToolBarUI.ToolBarContListener {
      protected MetalContainerListener() {
         super();
      }
   }
}
