package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.plaf.RootPaneUI;
import sun.awt.AWTAccessor;
import sun.security.action.GetBooleanAction;

public class JRootPane extends JComponent implements Accessible {
   private static final String uiClassID = "RootPaneUI";
   private static final boolean LOG_DISABLE_TRUE_DOUBLE_BUFFERING = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("swing.logDoubleBufferingDisable")));
   private static final boolean IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING = (Boolean)AccessController.doPrivileged((PrivilegedAction)(new GetBooleanAction("swing.ignoreDoubleBufferingDisable")));
   public static final int NONE = 0;
   public static final int FRAME = 1;
   public static final int PLAIN_DIALOG = 2;
   public static final int INFORMATION_DIALOG = 3;
   public static final int ERROR_DIALOG = 4;
   public static final int COLOR_CHOOSER_DIALOG = 5;
   public static final int FILE_CHOOSER_DIALOG = 6;
   public static final int QUESTION_DIALOG = 7;
   public static final int WARNING_DIALOG = 8;
   private int windowDecorationStyle;
   protected JMenuBar menuBar;
   protected Container contentPane;
   protected JLayeredPane layeredPane;
   protected Component glassPane;
   protected JButton defaultButton;
   /** @deprecated */
   @Deprecated
   protected JRootPane.DefaultAction defaultPressAction;
   /** @deprecated */
   @Deprecated
   protected JRootPane.DefaultAction defaultReleaseAction;
   boolean useTrueDoubleBuffering = true;

   public JRootPane() {
      this.setGlassPane(this.createGlassPane());
      this.setLayeredPane(this.createLayeredPane());
      this.setContentPane(this.createContentPane());
      this.setLayout(this.createRootLayout());
      this.setDoubleBuffered(true);
      this.updateUI();
   }

   public void setDoubleBuffered(boolean var1) {
      if (this.isDoubleBuffered() != var1) {
         super.setDoubleBuffered(var1);
         RepaintManager.currentManager((JComponent)this).doubleBufferingChanged(this);
      }

   }

   public int getWindowDecorationStyle() {
      return this.windowDecorationStyle;
   }

   public void setWindowDecorationStyle(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         int var2 = this.getWindowDecorationStyle();
         this.windowDecorationStyle = var1;
         this.firePropertyChange("windowDecorationStyle", var2, var1);
      } else {
         throw new IllegalArgumentException("Invalid decoration style");
      }
   }

   public RootPaneUI getUI() {
      return (RootPaneUI)this.ui;
   }

   public void setUI(RootPaneUI var1) {
      super.setUI(var1);
   }

   public void updateUI() {
      this.setUI((RootPaneUI)UIManager.getUI(this));
   }

   public String getUIClassID() {
      return "RootPaneUI";
   }

   protected JLayeredPane createLayeredPane() {
      JLayeredPane var1 = new JLayeredPane();
      var1.setName(this.getName() + ".layeredPane");
      return var1;
   }

   protected Container createContentPane() {
      JPanel var1 = new JPanel();
      var1.setName(this.getName() + ".contentPane");
      var1.setLayout(new BorderLayout() {
         public void addLayoutComponent(Component var1, Object var2) {
            if (var2 == null) {
               var2 = "Center";
            }

            super.addLayoutComponent(var1, var2);
         }
      });
      return var1;
   }

   protected Component createGlassPane() {
      JPanel var1 = new JPanel();
      var1.setName(this.getName() + ".glassPane");
      var1.setVisible(false);
      ((JPanel)var1).setOpaque(false);
      return var1;
   }

   protected LayoutManager createRootLayout() {
      return new JRootPane.RootLayout();
   }

   public void setJMenuBar(JMenuBar var1) {
      if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane) {
         this.layeredPane.remove(this.menuBar);
      }

      this.menuBar = var1;
      if (this.menuBar != null) {
         this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
      }

   }

   /** @deprecated */
   @Deprecated
   public void setMenuBar(JMenuBar var1) {
      if (this.menuBar != null && this.menuBar.getParent() == this.layeredPane) {
         this.layeredPane.remove(this.menuBar);
      }

      this.menuBar = var1;
      if (this.menuBar != null) {
         this.layeredPane.add(this.menuBar, JLayeredPane.FRAME_CONTENT_LAYER);
      }

   }

   public JMenuBar getJMenuBar() {
      return this.menuBar;
   }

   /** @deprecated */
   @Deprecated
   public JMenuBar getMenuBar() {
      return this.menuBar;
   }

   public void setContentPane(Container var1) {
      if (var1 == null) {
         throw new IllegalComponentStateException("contentPane cannot be set to null.");
      } else {
         if (this.contentPane != null && this.contentPane.getParent() == this.layeredPane) {
            this.layeredPane.remove(this.contentPane);
         }

         this.contentPane = var1;
         this.layeredPane.add(this.contentPane, JLayeredPane.FRAME_CONTENT_LAYER);
      }
   }

   public Container getContentPane() {
      return this.contentPane;
   }

   public void setLayeredPane(JLayeredPane var1) {
      if (var1 == null) {
         throw new IllegalComponentStateException("layeredPane cannot be set to null.");
      } else {
         if (this.layeredPane != null && this.layeredPane.getParent() == this) {
            this.remove(this.layeredPane);
         }

         this.layeredPane = var1;
         this.add(this.layeredPane, -1);
      }
   }

   public JLayeredPane getLayeredPane() {
      return this.layeredPane;
   }

   public void setGlassPane(Component var1) {
      if (var1 == null) {
         throw new NullPointerException("glassPane cannot be set to null.");
      } else {
         AWTAccessor.getComponentAccessor().setMixingCutoutShape(var1, new Rectangle());
         boolean var2 = false;
         if (this.glassPane != null && this.glassPane.getParent() == this) {
            this.remove(this.glassPane);
            var2 = this.glassPane.isVisible();
         }

         var1.setVisible(var2);
         this.glassPane = var1;
         this.add(this.glassPane, 0);
         if (var2) {
            this.repaint();
         }

      }
   }

   public Component getGlassPane() {
      return this.glassPane;
   }

   public boolean isValidateRoot() {
      return true;
   }

   public boolean isOptimizedDrawingEnabled() {
      return !this.glassPane.isVisible();
   }

   public void addNotify() {
      super.addNotify();
      this.enableEvents(8L);
   }

   public void removeNotify() {
      super.removeNotify();
   }

   public void setDefaultButton(JButton var1) {
      JButton var2 = this.defaultButton;
      if (var2 != var1) {
         this.defaultButton = var1;
         if (var2 != null) {
            var2.repaint();
         }

         if (var1 != null) {
            var1.repaint();
         }
      }

      this.firePropertyChange("defaultButton", var2, var1);
   }

   public JButton getDefaultButton() {
      return this.defaultButton;
   }

   final void setUseTrueDoubleBuffering(boolean var1) {
      this.useTrueDoubleBuffering = var1;
   }

   final boolean getUseTrueDoubleBuffering() {
      return this.useTrueDoubleBuffering;
   }

   final void disableTrueDoubleBuffering() {
      if (this.useTrueDoubleBuffering && !IGNORE_DISABLE_TRUE_DOUBLE_BUFFERING) {
         if (LOG_DISABLE_TRUE_DOUBLE_BUFFERING) {
            System.out.println("Disabling true double buffering for " + this);
            Thread.dumpStack();
         }

         this.useTrueDoubleBuffering = false;
         RepaintManager.currentManager((JComponent)this).doubleBufferingChanged(this);
      }

   }

   protected void addImpl(Component var1, Object var2, int var3) {
      super.addImpl(var1, var2, var3);
      if (this.glassPane != null && this.glassPane.getParent() == this && this.getComponent(0) != this.glassPane) {
         this.add(this.glassPane, 0);
      }

   }

   protected String paramString() {
      return super.paramString();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JRootPane.AccessibleJRootPane();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJRootPane extends JComponent.AccessibleJComponent {
      protected AccessibleJRootPane() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.ROOT_PANE;
      }

      public int getAccessibleChildrenCount() {
         return super.getAccessibleChildrenCount();
      }

      public Accessible getAccessibleChild(int var1) {
         return super.getAccessibleChild(var1);
      }
   }

   protected class RootLayout implements LayoutManager2, Serializable {
      public Dimension preferredLayoutSize(Container var1) {
         Insets var4 = JRootPane.this.getInsets();
         Dimension var2;
         if (JRootPane.this.contentPane != null) {
            var2 = JRootPane.this.contentPane.getPreferredSize();
         } else {
            var2 = var1.getSize();
         }

         Dimension var3;
         if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
            var3 = JRootPane.this.menuBar.getPreferredSize();
         } else {
            var3 = new Dimension(0, 0);
         }

         return new Dimension(Math.max(var2.width, var3.width) + var4.left + var4.right, var2.height + var3.height + var4.top + var4.bottom);
      }

      public Dimension minimumLayoutSize(Container var1) {
         Insets var4 = JRootPane.this.getInsets();
         Dimension var2;
         if (JRootPane.this.contentPane != null) {
            var2 = JRootPane.this.contentPane.getMinimumSize();
         } else {
            var2 = var1.getSize();
         }

         Dimension var3;
         if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
            var3 = JRootPane.this.menuBar.getMinimumSize();
         } else {
            var3 = new Dimension(0, 0);
         }

         return new Dimension(Math.max(var2.width, var3.width) + var4.left + var4.right, var2.height + var3.height + var4.top + var4.bottom);
      }

      public Dimension maximumLayoutSize(Container var1) {
         Insets var4 = JRootPane.this.getInsets();
         Dimension var3;
         if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
            var3 = JRootPane.this.menuBar.getMaximumSize();
         } else {
            var3 = new Dimension(0, 0);
         }

         Dimension var2;
         if (JRootPane.this.contentPane != null) {
            var2 = JRootPane.this.contentPane.getMaximumSize();
         } else {
            var2 = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE - var4.top - var4.bottom - var3.height - 1);
         }

         return new Dimension(Math.min(var2.width, var3.width) + var4.left + var4.right, var2.height + var3.height + var4.top + var4.bottom);
      }

      public void layoutContainer(Container var1) {
         Rectangle var2 = var1.getBounds();
         Insets var3 = JRootPane.this.getInsets();
         int var4 = 0;
         int var5 = var2.width - var3.right - var3.left;
         int var6 = var2.height - var3.top - var3.bottom;
         if (JRootPane.this.layeredPane != null) {
            JRootPane.this.layeredPane.setBounds(var3.left, var3.top, var5, var6);
         }

         if (JRootPane.this.glassPane != null) {
            JRootPane.this.glassPane.setBounds(var3.left, var3.top, var5, var6);
         }

         if (JRootPane.this.menuBar != null && JRootPane.this.menuBar.isVisible()) {
            Dimension var7 = JRootPane.this.menuBar.getPreferredSize();
            JRootPane.this.menuBar.setBounds(0, 0, var5, var7.height);
            var4 += var7.height;
         }

         if (JRootPane.this.contentPane != null) {
            JRootPane.this.contentPane.setBounds(0, var4, var5, var6 - var4);
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
   }

   static class DefaultAction extends AbstractAction {
      JButton owner;
      JRootPane root;
      boolean press;

      DefaultAction(JRootPane var1, boolean var2) {
         this.root = var1;
         this.press = var2;
      }

      public void setOwner(JButton var1) {
         this.owner = var1;
      }

      public void actionPerformed(ActionEvent var1) {
         if (this.owner != null && SwingUtilities.getRootPane(this.owner) == this.root) {
            ButtonModel var2 = this.owner.getModel();
            if (this.press) {
               var2.setArmed(true);
               var2.setPressed(true);
            } else {
               var2.setPressed(false);
            }
         }

      }

      public boolean isEnabled() {
         return this.owner.getModel().isEnabled();
      }
   }
}
