package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.ActionMap;
import javax.swing.DefaultDesktopManager;
import javax.swing.DesktopManager;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicInternalFrameUI extends InternalFrameUI {
   protected JInternalFrame frame;
   private BasicInternalFrameUI.Handler handler;
   protected MouseInputAdapter borderListener;
   protected PropertyChangeListener propertyChangeListener;
   protected LayoutManager internalFrameLayout;
   protected ComponentListener componentListener;
   protected MouseInputListener glassPaneDispatcher;
   private InternalFrameListener internalFrameListener;
   protected JComponent northPane;
   protected JComponent southPane;
   protected JComponent westPane;
   protected JComponent eastPane;
   protected BasicInternalFrameTitlePane titlePane;
   private static DesktopManager sharedDesktopManager;
   private boolean componentListenerAdded = false;
   private Rectangle parentBounds;
   private boolean dragging = false;
   private boolean resizing = false;
   /** @deprecated */
   @Deprecated
   protected KeyStroke openMenuKey;
   private boolean keyBindingRegistered = false;
   private boolean keyBindingActive = false;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicInternalFrameUI((JInternalFrame)var0);
   }

   public BasicInternalFrameUI(JInternalFrame var1) {
      LookAndFeel var2 = UIManager.getLookAndFeel();
      if (var2 instanceof BasicLookAndFeel) {
         ((BasicLookAndFeel)var2).installAWTEventListener();
      }

   }

   public void installUI(JComponent var1) {
      this.frame = (JInternalFrame)var1;
      this.installDefaults();
      this.installListeners();
      this.installComponents();
      this.installKeyboardActions();
      LookAndFeel.installProperty(this.frame, "opaque", Boolean.TRUE);
   }

   public void uninstallUI(JComponent var1) {
      if (var1 != this.frame) {
         throw new IllegalComponentStateException(this + " was asked to deinstall() " + var1 + " when it only knows about " + this.frame + ".");
      } else {
         this.uninstallKeyboardActions();
         this.uninstallComponents();
         this.uninstallListeners();
         this.uninstallDefaults();
         this.updateFrameCursor();
         this.handler = null;
         this.frame = null;
      }
   }

   protected void installDefaults() {
      Icon var1 = this.frame.getFrameIcon();
      if (var1 == null || var1 instanceof UIResource) {
         this.frame.setFrameIcon(UIManager.getIcon("InternalFrame.icon"));
      }

      Container var2 = this.frame.getContentPane();
      if (var2 != null) {
         Color var3 = var2.getBackground();
         if (var3 instanceof UIResource) {
            var2.setBackground((Color)null);
         }
      }

      this.frame.setLayout(this.internalFrameLayout = this.createLayoutManager());
      this.frame.setBackground(UIManager.getLookAndFeelDefaults().getColor("control"));
      LookAndFeel.installBorder(this.frame, "InternalFrame.border");
   }

   protected void installKeyboardActions() {
      this.createInternalFrameListener();
      if (this.internalFrameListener != null) {
         this.frame.addInternalFrameListener(this.internalFrameListener);
      }

      LazyActionMap.installLazyActionMap(this.frame, BasicInternalFrameUI.class, "InternalFrame.actionMap");
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new UIAction("showSystemMenu") {
         public void actionPerformed(ActionEvent var1) {
            JInternalFrame var2 = (JInternalFrame)var1.getSource();
            if (var2.getUI() instanceof BasicInternalFrameUI) {
               JComponent var3 = ((BasicInternalFrameUI)var2.getUI()).getNorthPane();
               if (var3 instanceof BasicInternalFrameTitlePane) {
                  ((BasicInternalFrameTitlePane)var3).showSystemMenu();
               }
            }

         }

         public boolean isEnabled(Object var1) {
            if (var1 instanceof JInternalFrame) {
               JInternalFrame var2 = (JInternalFrame)var1;
               if (var2.getUI() instanceof BasicInternalFrameUI) {
                  return ((BasicInternalFrameUI)var2.getUI()).isKeyBindingActive();
               }
            }

            return false;
         }
      });
      BasicLookAndFeel.installAudioActionMap(var0);
   }

   protected void installComponents() {
      this.setNorthPane(this.createNorthPane(this.frame));
      this.setSouthPane(this.createSouthPane(this.frame));
      this.setEastPane(this.createEastPane(this.frame));
      this.setWestPane(this.createWestPane(this.frame));
   }

   protected void installListeners() {
      this.borderListener = this.createBorderListener(this.frame);
      this.propertyChangeListener = this.createPropertyChangeListener();
      this.frame.addPropertyChangeListener(this.propertyChangeListener);
      this.installMouseHandlers(this.frame);
      this.glassPaneDispatcher = this.createGlassPaneDispatcher();
      if (this.glassPaneDispatcher != null) {
         this.frame.getGlassPane().addMouseListener(this.glassPaneDispatcher);
         this.frame.getGlassPane().addMouseMotionListener(this.glassPaneDispatcher);
      }

      this.componentListener = this.createComponentListener();
      if (this.frame.getParent() != null) {
         this.parentBounds = this.frame.getParent().getBounds();
      }

      if (this.frame.getParent() != null && !this.componentListenerAdded) {
         this.frame.getParent().addComponentListener(this.componentListener);
         this.componentListenerAdded = true;
      }

   }

   private WindowFocusListener getWindowFocusListener() {
      return this.getHandler();
   }

   private void cancelResize() {
      if (this.resizing && this.borderListener instanceof BasicInternalFrameUI.BorderListener) {
         ((BasicInternalFrameUI.BorderListener)this.borderListener).finishMouseReleased();
      }

   }

   private BasicInternalFrameUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicInternalFrameUI.Handler();
      }

      return this.handler;
   }

   InputMap getInputMap(int var1) {
      return var1 == 2 ? this.createInputMap(var1) : null;
   }

   InputMap createInputMap(int var1) {
      if (var1 == 2) {
         Object[] var2 = (Object[])((Object[])DefaultLookup.get(this.frame, this, "InternalFrame.windowBindings"));
         if (var2 != null) {
            return LookAndFeel.makeComponentInputMap(this.frame, var2);
         }
      }

      return null;
   }

   protected void uninstallDefaults() {
      Icon var1 = this.frame.getFrameIcon();
      if (var1 instanceof UIResource) {
         this.frame.setFrameIcon((Icon)null);
      }

      this.internalFrameLayout = null;
      this.frame.setLayout((LayoutManager)null);
      LookAndFeel.uninstallBorder(this.frame);
   }

   protected void uninstallComponents() {
      this.setNorthPane((JComponent)null);
      this.setSouthPane((JComponent)null);
      this.setEastPane((JComponent)null);
      this.setWestPane((JComponent)null);
      if (this.titlePane != null) {
         this.titlePane.uninstallDefaults();
      }

      this.titlePane = null;
   }

   protected void uninstallListeners() {
      if (this.frame.getParent() != null && this.componentListenerAdded) {
         this.frame.getParent().removeComponentListener(this.componentListener);
         this.componentListenerAdded = false;
      }

      this.componentListener = null;
      if (this.glassPaneDispatcher != null) {
         this.frame.getGlassPane().removeMouseListener(this.glassPaneDispatcher);
         this.frame.getGlassPane().removeMouseMotionListener(this.glassPaneDispatcher);
         this.glassPaneDispatcher = null;
      }

      this.deinstallMouseHandlers(this.frame);
      this.frame.removePropertyChangeListener(this.propertyChangeListener);
      this.propertyChangeListener = null;
      this.borderListener = null;
   }

   protected void uninstallKeyboardActions() {
      if (this.internalFrameListener != null) {
         this.frame.removeInternalFrameListener(this.internalFrameListener);
      }

      this.internalFrameListener = null;
      SwingUtilities.replaceUIInputMap(this.frame, 2, (InputMap)null);
      SwingUtilities.replaceUIActionMap(this.frame, (ActionMap)null);
   }

   void updateFrameCursor() {
      if (!this.resizing) {
         Cursor var1 = this.frame.getLastCursor();
         if (var1 == null) {
            var1 = Cursor.getPredefinedCursor(0);
         }

         this.frame.setCursor(var1);
      }
   }

   protected LayoutManager createLayoutManager() {
      return this.getHandler();
   }

   protected PropertyChangeListener createPropertyChangeListener() {
      return this.getHandler();
   }

   public Dimension getPreferredSize(JComponent var1) {
      return this.frame == var1 ? this.frame.getLayout().preferredLayoutSize(var1) : new Dimension(100, 100);
   }

   public Dimension getMinimumSize(JComponent var1) {
      return this.frame == var1 ? this.frame.getLayout().minimumLayoutSize(var1) : new Dimension(0, 0);
   }

   public Dimension getMaximumSize(JComponent var1) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   protected void replacePane(JComponent var1, JComponent var2) {
      if (var1 != null) {
         this.deinstallMouseHandlers(var1);
         this.frame.remove(var1);
      }

      if (var2 != null) {
         this.frame.add(var2);
         this.installMouseHandlers(var2);
      }

   }

   protected void deinstallMouseHandlers(JComponent var1) {
      var1.removeMouseListener(this.borderListener);
      var1.removeMouseMotionListener(this.borderListener);
   }

   protected void installMouseHandlers(JComponent var1) {
      var1.addMouseListener(this.borderListener);
      var1.addMouseMotionListener(this.borderListener);
   }

   protected JComponent createNorthPane(JInternalFrame var1) {
      this.titlePane = new BasicInternalFrameTitlePane(var1);
      return this.titlePane;
   }

   protected JComponent createSouthPane(JInternalFrame var1) {
      return null;
   }

   protected JComponent createWestPane(JInternalFrame var1) {
      return null;
   }

   protected JComponent createEastPane(JInternalFrame var1) {
      return null;
   }

   protected MouseInputAdapter createBorderListener(JInternalFrame var1) {
      return new BasicInternalFrameUI.BorderListener();
   }

   protected void createInternalFrameListener() {
      this.internalFrameListener = this.getHandler();
   }

   protected final boolean isKeyBindingRegistered() {
      return this.keyBindingRegistered;
   }

   protected final void setKeyBindingRegistered(boolean var1) {
      this.keyBindingRegistered = var1;
   }

   public final boolean isKeyBindingActive() {
      return this.keyBindingActive;
   }

   protected final void setKeyBindingActive(boolean var1) {
      this.keyBindingActive = var1;
   }

   protected void setupMenuOpenKey() {
      InputMap var1 = this.getInputMap(2);
      SwingUtilities.replaceUIInputMap(this.frame, 2, var1);
   }

   protected void setupMenuCloseKey() {
   }

   public JComponent getNorthPane() {
      return this.northPane;
   }

   public void setNorthPane(JComponent var1) {
      if (this.northPane != null && this.northPane instanceof BasicInternalFrameTitlePane) {
         ((BasicInternalFrameTitlePane)this.northPane).uninstallListeners();
      }

      this.replacePane(this.northPane, var1);
      this.northPane = var1;
      if (var1 instanceof BasicInternalFrameTitlePane) {
         this.titlePane = (BasicInternalFrameTitlePane)var1;
      }

   }

   public JComponent getSouthPane() {
      return this.southPane;
   }

   public void setSouthPane(JComponent var1) {
      this.southPane = var1;
   }

   public JComponent getWestPane() {
      return this.westPane;
   }

   public void setWestPane(JComponent var1) {
      this.westPane = var1;
   }

   public JComponent getEastPane() {
      return this.eastPane;
   }

   public void setEastPane(JComponent var1) {
      this.eastPane = var1;
   }

   protected DesktopManager getDesktopManager() {
      if (this.frame.getDesktopPane() != null && this.frame.getDesktopPane().getDesktopManager() != null) {
         return this.frame.getDesktopPane().getDesktopManager();
      } else {
         if (sharedDesktopManager == null) {
            sharedDesktopManager = this.createDesktopManager();
         }

         return sharedDesktopManager;
      }
   }

   protected DesktopManager createDesktopManager() {
      return new DefaultDesktopManager();
   }

   protected void closeFrame(JInternalFrame var1) {
      BasicLookAndFeel.playSound(this.frame, "InternalFrame.closeSound");
      this.getDesktopManager().closeFrame(var1);
   }

   protected void maximizeFrame(JInternalFrame var1) {
      BasicLookAndFeel.playSound(this.frame, "InternalFrame.maximizeSound");
      this.getDesktopManager().maximizeFrame(var1);
   }

   protected void minimizeFrame(JInternalFrame var1) {
      if (!var1.isIcon()) {
         BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreDownSound");
      }

      this.getDesktopManager().minimizeFrame(var1);
   }

   protected void iconifyFrame(JInternalFrame var1) {
      BasicLookAndFeel.playSound(this.frame, "InternalFrame.minimizeSound");
      this.getDesktopManager().iconifyFrame(var1);
   }

   protected void deiconifyFrame(JInternalFrame var1) {
      if (!var1.isMaximum()) {
         BasicLookAndFeel.playSound(this.frame, "InternalFrame.restoreUpSound");
      }

      this.getDesktopManager().deiconifyFrame(var1);
   }

   protected void activateFrame(JInternalFrame var1) {
      this.getDesktopManager().activateFrame(var1);
   }

   protected void deactivateFrame(JInternalFrame var1) {
      this.getDesktopManager().deactivateFrame(var1);
   }

   protected ComponentListener createComponentListener() {
      return this.getHandler();
   }

   protected MouseInputListener createGlassPaneDispatcher() {
      return null;
   }

   private class Handler implements ComponentListener, InternalFrameListener, LayoutManager, MouseInputListener, PropertyChangeListener, WindowFocusListener, SwingConstants {
      private Handler() {
      }

      public void windowGainedFocus(WindowEvent var1) {
      }

      public void windowLostFocus(WindowEvent var1) {
         BasicInternalFrameUI.this.cancelResize();
      }

      public void componentResized(ComponentEvent var1) {
         Rectangle var2 = ((Component)var1.getSource()).getBounds();
         JInternalFrame.JDesktopIcon var3 = null;
         if (BasicInternalFrameUI.this.frame != null) {
            var3 = BasicInternalFrameUI.this.frame.getDesktopIcon();
            if (BasicInternalFrameUI.this.frame.isMaximum()) {
               BasicInternalFrameUI.this.frame.setBounds(0, 0, var2.width, var2.height);
            }
         }

         if (var3 != null) {
            Rectangle var4 = var3.getBounds();
            int var5 = var4.y + (var2.height - BasicInternalFrameUI.this.parentBounds.height);
            var3.setBounds(var4.x, var5, var4.width, var4.height);
         }

         if (!BasicInternalFrameUI.this.parentBounds.equals(var2)) {
            BasicInternalFrameUI.this.parentBounds = var2;
         }

         if (BasicInternalFrameUI.this.frame != null) {
            BasicInternalFrameUI.this.frame.validate();
         }

      }

      public void componentMoved(ComponentEvent var1) {
      }

      public void componentShown(ComponentEvent var1) {
      }

      public void componentHidden(ComponentEvent var1) {
      }

      public void internalFrameClosed(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.frame.removeInternalFrameListener(BasicInternalFrameUI.this.getHandler());
      }

      public void internalFrameActivated(InternalFrameEvent var1) {
         if (!BasicInternalFrameUI.this.isKeyBindingRegistered()) {
            BasicInternalFrameUI.this.setKeyBindingRegistered(true);
            BasicInternalFrameUI.this.setupMenuOpenKey();
            BasicInternalFrameUI.this.setupMenuCloseKey();
         }

         if (BasicInternalFrameUI.this.isKeyBindingRegistered()) {
            BasicInternalFrameUI.this.setKeyBindingActive(true);
         }

      }

      public void internalFrameDeactivated(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.setKeyBindingActive(false);
      }

      public void internalFrameClosing(InternalFrameEvent var1) {
      }

      public void internalFrameOpened(InternalFrameEvent var1) {
      }

      public void internalFrameIconified(InternalFrameEvent var1) {
      }

      public void internalFrameDeiconified(InternalFrameEvent var1) {
      }

      public void addLayoutComponent(String var1, Component var2) {
      }

      public void removeLayoutComponent(Component var1) {
      }

      public Dimension preferredLayoutSize(Container var1) {
         Insets var3 = BasicInternalFrameUI.this.frame.getInsets();
         Dimension var2 = new Dimension(BasicInternalFrameUI.this.frame.getRootPane().getPreferredSize());
         var2.width += var3.left + var3.right;
         var2.height += var3.top + var3.bottom;
         Dimension var4;
         if (BasicInternalFrameUI.this.getNorthPane() != null) {
            var4 = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
            var2.width = Math.max(var4.width, var2.width);
            var2.height += var4.height;
         }

         if (BasicInternalFrameUI.this.getSouthPane() != null) {
            var4 = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
            var2.width = Math.max(var4.width, var2.width);
            var2.height += var4.height;
         }

         if (BasicInternalFrameUI.this.getEastPane() != null) {
            var4 = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
            var2.width += var4.width;
            var2.height = Math.max(var4.height, var2.height);
         }

         if (BasicInternalFrameUI.this.getWestPane() != null) {
            var4 = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
            var2.width += var4.width;
            var2.height = Math.max(var4.height, var2.height);
         }

         return var2;
      }

      public Dimension minimumLayoutSize(Container var1) {
         Dimension var2 = new Dimension();
         if (BasicInternalFrameUI.this.getNorthPane() != null && BasicInternalFrameUI.this.getNorthPane() instanceof BasicInternalFrameTitlePane) {
            var2 = new Dimension(BasicInternalFrameUI.this.getNorthPane().getMinimumSize());
         }

         Insets var3 = BasicInternalFrameUI.this.frame.getInsets();
         var2.width += var3.left + var3.right;
         var2.height += var3.top + var3.bottom;
         return var2;
      }

      public void layoutContainer(Container var1) {
         Insets var2 = BasicInternalFrameUI.this.frame.getInsets();
         int var3 = var2.left;
         int var4 = var2.top;
         int var5 = BasicInternalFrameUI.this.frame.getWidth() - var2.left - var2.right;
         int var6 = BasicInternalFrameUI.this.frame.getHeight() - var2.top - var2.bottom;
         Dimension var7;
         if (BasicInternalFrameUI.this.getNorthPane() != null) {
            var7 = BasicInternalFrameUI.this.getNorthPane().getPreferredSize();
            if (DefaultLookup.getBoolean(BasicInternalFrameUI.this.frame, BasicInternalFrameUI.this, "InternalFrame.layoutTitlePaneAtOrigin", false)) {
               var4 = 0;
               var6 += var2.top;
               BasicInternalFrameUI.this.getNorthPane().setBounds(0, 0, BasicInternalFrameUI.this.frame.getWidth(), var7.height);
            } else {
               BasicInternalFrameUI.this.getNorthPane().setBounds(var3, var4, var5, var7.height);
            }

            var4 += var7.height;
            var6 -= var7.height;
         }

         if (BasicInternalFrameUI.this.getSouthPane() != null) {
            var7 = BasicInternalFrameUI.this.getSouthPane().getPreferredSize();
            BasicInternalFrameUI.this.getSouthPane().setBounds(var3, BasicInternalFrameUI.this.frame.getHeight() - var2.bottom - var7.height, var5, var7.height);
            var6 -= var7.height;
         }

         if (BasicInternalFrameUI.this.getWestPane() != null) {
            var7 = BasicInternalFrameUI.this.getWestPane().getPreferredSize();
            BasicInternalFrameUI.this.getWestPane().setBounds(var3, var4, var7.width, var6);
            var5 -= var7.width;
            var3 += var7.width;
         }

         if (BasicInternalFrameUI.this.getEastPane() != null) {
            var7 = BasicInternalFrameUI.this.getEastPane().getPreferredSize();
            BasicInternalFrameUI.this.getEastPane().setBounds(var5 - var7.width, var4, var7.width, var6);
            var5 -= var7.width;
         }

         if (BasicInternalFrameUI.this.frame.getRootPane() != null) {
            BasicInternalFrameUI.this.frame.getRootPane().setBounds(var3, var4, var5, var6);
         }

      }

      public void mousePressed(MouseEvent var1) {
      }

      public void mouseEntered(MouseEvent var1) {
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mouseReleased(MouseEvent var1) {
      }

      public void mouseDragged(MouseEvent var1) {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         String var2 = var1.getPropertyName();
         JInternalFrame var3 = (JInternalFrame)var1.getSource();
         Object var4 = var1.getNewValue();
         Object var5 = var1.getOldValue();
         if ("closed" == var2) {
            if (var4 == Boolean.TRUE) {
               BasicInternalFrameUI.this.cancelResize();
               if (BasicInternalFrameUI.this.frame.getParent() != null && BasicInternalFrameUI.this.componentListenerAdded) {
                  BasicInternalFrameUI.this.frame.getParent().removeComponentListener(BasicInternalFrameUI.this.componentListener);
               }

               BasicInternalFrameUI.this.closeFrame(var3);
            }
         } else if ("maximum" == var2) {
            if (var4 == Boolean.TRUE) {
               BasicInternalFrameUI.this.maximizeFrame(var3);
            } else {
               BasicInternalFrameUI.this.minimizeFrame(var3);
            }
         } else if ("icon" == var2) {
            if (var4 == Boolean.TRUE) {
               BasicInternalFrameUI.this.iconifyFrame(var3);
            } else {
               BasicInternalFrameUI.this.deiconifyFrame(var3);
            }
         } else if ("selected" == var2) {
            if (var4 == Boolean.TRUE && var5 == Boolean.FALSE) {
               BasicInternalFrameUI.this.activateFrame(var3);
            } else if (var4 == Boolean.FALSE && var5 == Boolean.TRUE) {
               BasicInternalFrameUI.this.deactivateFrame(var3);
            }
         } else if (var2 == "ancestor") {
            if (var4 == null) {
               BasicInternalFrameUI.this.cancelResize();
            }

            if (BasicInternalFrameUI.this.frame.getParent() != null) {
               BasicInternalFrameUI.this.parentBounds = var3.getParent().getBounds();
            } else {
               BasicInternalFrameUI.this.parentBounds = null;
            }

            if (BasicInternalFrameUI.this.frame.getParent() != null && !BasicInternalFrameUI.this.componentListenerAdded) {
               var3.getParent().addComponentListener(BasicInternalFrameUI.this.componentListener);
               BasicInternalFrameUI.this.componentListenerAdded = true;
            }
         } else if ("title" == var2 || var2 == "closable" || var2 == "iconable" || var2 == "maximizable") {
            Dimension var6 = BasicInternalFrameUI.this.frame.getMinimumSize();
            Dimension var7 = BasicInternalFrameUI.this.frame.getSize();
            if (var6.width > var7.width) {
               BasicInternalFrameUI.this.frame.setSize(var6.width, var7.height);
            }
         }

      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   protected class BasicInternalFrameListener implements InternalFrameListener {
      public void internalFrameClosing(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameClosing(var1);
      }

      public void internalFrameClosed(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameClosed(var1);
      }

      public void internalFrameOpened(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameOpened(var1);
      }

      public void internalFrameIconified(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameIconified(var1);
      }

      public void internalFrameDeiconified(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameDeiconified(var1);
      }

      public void internalFrameActivated(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameActivated(var1);
      }

      public void internalFrameDeactivated(InternalFrameEvent var1) {
         BasicInternalFrameUI.this.getHandler().internalFrameDeactivated(var1);
      }
   }

   protected class GlassPaneDispatcher implements MouseInputListener {
      public void mousePressed(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mousePressed(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseMoved(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseExited(var1);
      }

      public void mouseClicked(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseClicked(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseReleased(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicInternalFrameUI.this.getHandler().mouseDragged(var1);
      }
   }

   protected class ComponentHandler implements ComponentListener {
      public void componentResized(ComponentEvent var1) {
         BasicInternalFrameUI.this.getHandler().componentResized(var1);
      }

      public void componentMoved(ComponentEvent var1) {
         BasicInternalFrameUI.this.getHandler().componentMoved(var1);
      }

      public void componentShown(ComponentEvent var1) {
         BasicInternalFrameUI.this.getHandler().componentShown(var1);
      }

      public void componentHidden(ComponentEvent var1) {
         BasicInternalFrameUI.this.getHandler().componentHidden(var1);
      }
   }

   protected class BorderListener extends MouseInputAdapter implements SwingConstants {
      int _x;
      int _y;
      int __x;
      int __y;
      Rectangle startingBounds;
      int resizeDir;
      protected final int RESIZE_NONE = 0;
      private boolean discardRelease = false;
      int resizeCornerSize = 16;

      public void mouseClicked(MouseEvent var1) {
         if (var1.getClickCount() > 1 && var1.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
            if (BasicInternalFrameUI.this.frame.isIconifiable() && BasicInternalFrameUI.this.frame.isIcon()) {
               try {
                  BasicInternalFrameUI.this.frame.setIcon(false);
               } catch (PropertyVetoException var5) {
               }
            } else if (BasicInternalFrameUI.this.frame.isMaximizable()) {
               if (!BasicInternalFrameUI.this.frame.isMaximum()) {
                  try {
                     BasicInternalFrameUI.this.frame.setMaximum(true);
                  } catch (PropertyVetoException var4) {
                  }
               } else {
                  try {
                     BasicInternalFrameUI.this.frame.setMaximum(false);
                  } catch (PropertyVetoException var3) {
                  }
               }
            }
         }

      }

      void finishMouseReleased() {
         if (this.discardRelease) {
            this.discardRelease = false;
         } else {
            if (this.resizeDir == 0) {
               BasicInternalFrameUI.this.getDesktopManager().endDraggingFrame(BasicInternalFrameUI.this.frame);
               BasicInternalFrameUI.this.dragging = false;
            } else {
               Window var1 = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
               if (var1 != null) {
                  var1.removeWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
               }

               Container var2 = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
               if (var2 instanceof RootPaneContainer) {
                  Component var3 = ((RootPaneContainer)var2).getGlassPane();
                  var3.setCursor(Cursor.getPredefinedCursor(0));
                  var3.setVisible(false);
               }

               BasicInternalFrameUI.this.getDesktopManager().endResizingFrame(BasicInternalFrameUI.this.frame);
               BasicInternalFrameUI.this.resizing = false;
               BasicInternalFrameUI.this.updateFrameCursor();
            }

            this._x = 0;
            this._y = 0;
            this.__x = 0;
            this.__y = 0;
            this.startingBounds = null;
            this.resizeDir = 0;
            this.discardRelease = true;
         }
      }

      public void mouseReleased(MouseEvent var1) {
         this.finishMouseReleased();
      }

      public void mousePressed(MouseEvent var1) {
         Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
         this.__x = var1.getX();
         this.__y = var1.getY();
         this._x = var2.x;
         this._y = var2.y;
         this.startingBounds = BasicInternalFrameUI.this.frame.getBounds();
         this.resizeDir = 0;
         this.discardRelease = false;

         try {
            BasicInternalFrameUI.this.frame.setSelected(true);
         } catch (PropertyVetoException var8) {
         }

         Insets var3 = BasicInternalFrameUI.this.frame.getInsets();
         Point var4 = new Point(this.__x, this.__y);
         if (var1.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
            Point var5 = BasicInternalFrameUI.this.getNorthPane().getLocation();
            var4.x += var5.x;
            var4.y += var5.y;
         }

         if (var1.getSource() == BasicInternalFrameUI.this.getNorthPane() && var4.x > var3.left && var4.y > var3.top && var4.x < BasicInternalFrameUI.this.frame.getWidth() - var3.right) {
            BasicInternalFrameUI.this.getDesktopManager().beginDraggingFrame(BasicInternalFrameUI.this.frame);
            BasicInternalFrameUI.this.dragging = true;
         } else if (BasicInternalFrameUI.this.frame.isResizable()) {
            if (var1.getSource() == BasicInternalFrameUI.this.frame || var1.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
               if (var4.x <= var3.left) {
                  if (var4.y < this.resizeCornerSize + var3.top) {
                     this.resizeDir = 8;
                  } else if (var4.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - var3.bottom) {
                     this.resizeDir = 6;
                  } else {
                     this.resizeDir = 7;
                  }
               } else if (var4.x >= BasicInternalFrameUI.this.frame.getWidth() - var3.right) {
                  if (var4.y < this.resizeCornerSize + var3.top) {
                     this.resizeDir = 2;
                  } else if (var4.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - var3.bottom) {
                     this.resizeDir = 4;
                  } else {
                     this.resizeDir = 3;
                  }
               } else if (var4.y <= var3.top) {
                  if (var4.x < this.resizeCornerSize + var3.left) {
                     this.resizeDir = 8;
                  } else if (var4.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - var3.right) {
                     this.resizeDir = 2;
                  } else {
                     this.resizeDir = 1;
                  }
               } else {
                  if (var4.y < BasicInternalFrameUI.this.frame.getHeight() - var3.bottom) {
                     this.discardRelease = true;
                     return;
                  }

                  if (var4.x < this.resizeCornerSize + var3.left) {
                     this.resizeDir = 6;
                  } else if (var4.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - var3.right) {
                     this.resizeDir = 4;
                  } else {
                     this.resizeDir = 5;
                  }
               }

               Cursor var9 = Cursor.getPredefinedCursor(0);
               switch(this.resizeDir) {
               case 1:
                  var9 = Cursor.getPredefinedCursor(8);
                  break;
               case 2:
                  var9 = Cursor.getPredefinedCursor(7);
                  break;
               case 3:
                  var9 = Cursor.getPredefinedCursor(11);
                  break;
               case 4:
                  var9 = Cursor.getPredefinedCursor(5);
                  break;
               case 5:
                  var9 = Cursor.getPredefinedCursor(9);
                  break;
               case 6:
                  var9 = Cursor.getPredefinedCursor(4);
                  break;
               case 7:
                  var9 = Cursor.getPredefinedCursor(10);
                  break;
               case 8:
                  var9 = Cursor.getPredefinedCursor(6);
               }

               Container var6 = BasicInternalFrameUI.this.frame.getTopLevelAncestor();
               if (var6 instanceof RootPaneContainer) {
                  Component var7 = ((RootPaneContainer)var6).getGlassPane();
                  var7.setVisible(true);
                  var7.setCursor(var9);
               }

               BasicInternalFrameUI.this.getDesktopManager().beginResizingFrame(BasicInternalFrameUI.this.frame, this.resizeDir);
               BasicInternalFrameUI.this.resizing = true;
               Window var10 = SwingUtilities.getWindowAncestor(BasicInternalFrameUI.this.frame);
               if (var10 != null) {
                  var10.addWindowFocusListener(BasicInternalFrameUI.this.getWindowFocusListener());
               }

            }
         }
      }

      public void mouseDragged(MouseEvent var1) {
         if (this.startingBounds != null) {
            Point var2 = SwingUtilities.convertPoint((Component)var1.getSource(), var1.getX(), var1.getY(), (Component)null);
            int var3 = this._x - var2.x;
            int var4 = this._y - var2.y;
            Dimension var5 = BasicInternalFrameUI.this.frame.getMinimumSize();
            Dimension var6 = BasicInternalFrameUI.this.frame.getMaximumSize();
            Insets var11 = BasicInternalFrameUI.this.frame.getInsets();
            int var7;
            int var8;
            if (BasicInternalFrameUI.this.dragging) {
               if (!BasicInternalFrameUI.this.frame.isMaximum() && (var1.getModifiers() & 16) == 16) {
                  Dimension var14 = BasicInternalFrameUI.this.frame.getParent().getSize();
                  int var12 = var14.width;
                  int var13 = var14.height;
                  var7 = this.startingBounds.x - var3;
                  var8 = this.startingBounds.y - var4;
                  if (var7 + var11.left <= -this.__x) {
                     var7 = -this.__x - var11.left + 1;
                  }

                  if (var8 + var11.top <= -this.__y) {
                     var8 = -this.__y - var11.top + 1;
                  }

                  if (var7 + this.__x + var11.right >= var12) {
                     var7 = var12 - this.__x - var11.right - 1;
                  }

                  if (var8 + this.__y + var11.bottom >= var13) {
                     var8 = var13 - this.__y - var11.bottom - 1;
                  }

                  BasicInternalFrameUI.this.getDesktopManager().dragFrame(BasicInternalFrameUI.this.frame, var7, var8);
               }
            } else if (BasicInternalFrameUI.this.frame.isResizable()) {
               var7 = BasicInternalFrameUI.this.frame.getX();
               var8 = BasicInternalFrameUI.this.frame.getY();
               int var9 = BasicInternalFrameUI.this.frame.getWidth();
               int var10 = BasicInternalFrameUI.this.frame.getHeight();
               BasicInternalFrameUI.this.parentBounds = BasicInternalFrameUI.this.frame.getParent().getBounds();
               switch(this.resizeDir) {
               case 0:
                  return;
               case 1:
                  if (this.startingBounds.height + var4 < var5.height) {
                     var4 = -(this.startingBounds.height - var5.height);
                  } else if (this.startingBounds.height + var4 > var6.height) {
                     var4 = var6.height - this.startingBounds.height;
                  }

                  if (this.startingBounds.y - var4 < 0) {
                     var4 = this.startingBounds.y;
                  }

                  var7 = this.startingBounds.x;
                  var8 = this.startingBounds.y - var4;
                  var9 = this.startingBounds.width;
                  var10 = this.startingBounds.height + var4;
                  break;
               case 2:
                  if (this.startingBounds.height + var4 < var5.height) {
                     var4 = -(this.startingBounds.height - var5.height);
                  } else if (this.startingBounds.height + var4 > var6.height) {
                     var4 = var6.height - this.startingBounds.height;
                  }

                  if (this.startingBounds.y - var4 < 0) {
                     var4 = this.startingBounds.y;
                  }

                  if (this.startingBounds.width - var3 < var5.width) {
                     var3 = this.startingBounds.width - var5.width;
                  } else if (this.startingBounds.width - var3 > var6.width) {
                     var3 = -(var6.width - this.startingBounds.width);
                  }

                  if (this.startingBounds.x + this.startingBounds.width - var3 > BasicInternalFrameUI.this.parentBounds.width) {
                     var3 = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                  }

                  var7 = this.startingBounds.x;
                  var8 = this.startingBounds.y - var4;
                  var9 = this.startingBounds.width - var3;
                  var10 = this.startingBounds.height + var4;
                  break;
               case 3:
                  if (this.startingBounds.width - var3 < var5.width) {
                     var3 = this.startingBounds.width - var5.width;
                  } else if (this.startingBounds.width - var3 > var6.width) {
                     var3 = -(var6.width - this.startingBounds.width);
                  }

                  if (this.startingBounds.x + this.startingBounds.width - var3 > BasicInternalFrameUI.this.parentBounds.width) {
                     var3 = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                  }

                  var9 = this.startingBounds.width - var3;
                  var10 = this.startingBounds.height;
                  break;
               case 4:
                  if (this.startingBounds.width - var3 < var5.width) {
                     var3 = this.startingBounds.width - var5.width;
                  } else if (this.startingBounds.width - var3 > var6.width) {
                     var3 = -(var6.width - this.startingBounds.width);
                  }

                  if (this.startingBounds.x + this.startingBounds.width - var3 > BasicInternalFrameUI.this.parentBounds.width) {
                     var3 = this.startingBounds.x + this.startingBounds.width - BasicInternalFrameUI.this.parentBounds.width;
                  }

                  if (this.startingBounds.height - var4 < var5.height) {
                     var4 = this.startingBounds.height - var5.height;
                  } else if (this.startingBounds.height - var4 > var6.height) {
                     var4 = -(var6.height - this.startingBounds.height);
                  }

                  if (this.startingBounds.y + this.startingBounds.height - var4 > BasicInternalFrameUI.this.parentBounds.height) {
                     var4 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                  }

                  var9 = this.startingBounds.width - var3;
                  var10 = this.startingBounds.height - var4;
                  break;
               case 5:
                  if (this.startingBounds.height - var4 < var5.height) {
                     var4 = this.startingBounds.height - var5.height;
                  } else if (this.startingBounds.height - var4 > var6.height) {
                     var4 = -(var6.height - this.startingBounds.height);
                  }

                  if (this.startingBounds.y + this.startingBounds.height - var4 > BasicInternalFrameUI.this.parentBounds.height) {
                     var4 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                  }

                  var9 = this.startingBounds.width;
                  var10 = this.startingBounds.height - var4;
                  break;
               case 6:
                  if (this.startingBounds.height - var4 < var5.height) {
                     var4 = this.startingBounds.height - var5.height;
                  } else if (this.startingBounds.height - var4 > var6.height) {
                     var4 = -(var6.height - this.startingBounds.height);
                  }

                  if (this.startingBounds.y + this.startingBounds.height - var4 > BasicInternalFrameUI.this.parentBounds.height) {
                     var4 = this.startingBounds.y + this.startingBounds.height - BasicInternalFrameUI.this.parentBounds.height;
                  }

                  if (this.startingBounds.width + var3 < var5.width) {
                     var3 = -(this.startingBounds.width - var5.width);
                  } else if (this.startingBounds.width + var3 > var6.width) {
                     var3 = var6.width - this.startingBounds.width;
                  }

                  if (this.startingBounds.x - var3 < 0) {
                     var3 = this.startingBounds.x;
                  }

                  var7 = this.startingBounds.x - var3;
                  var8 = this.startingBounds.y;
                  var9 = this.startingBounds.width + var3;
                  var10 = this.startingBounds.height - var4;
                  break;
               case 7:
                  if (this.startingBounds.width + var3 < var5.width) {
                     var3 = -(this.startingBounds.width - var5.width);
                  } else if (this.startingBounds.width + var3 > var6.width) {
                     var3 = var6.width - this.startingBounds.width;
                  }

                  if (this.startingBounds.x - var3 < 0) {
                     var3 = this.startingBounds.x;
                  }

                  var7 = this.startingBounds.x - var3;
                  var8 = this.startingBounds.y;
                  var9 = this.startingBounds.width + var3;
                  var10 = this.startingBounds.height;
                  break;
               case 8:
                  if (this.startingBounds.width + var3 < var5.width) {
                     var3 = -(this.startingBounds.width - var5.width);
                  } else if (this.startingBounds.width + var3 > var6.width) {
                     var3 = var6.width - this.startingBounds.width;
                  }

                  if (this.startingBounds.x - var3 < 0) {
                     var3 = this.startingBounds.x;
                  }

                  if (this.startingBounds.height + var4 < var5.height) {
                     var4 = -(this.startingBounds.height - var5.height);
                  } else if (this.startingBounds.height + var4 > var6.height) {
                     var4 = var6.height - this.startingBounds.height;
                  }

                  if (this.startingBounds.y - var4 < 0) {
                     var4 = this.startingBounds.y;
                  }

                  var7 = this.startingBounds.x - var3;
                  var8 = this.startingBounds.y - var4;
                  var9 = this.startingBounds.width + var3;
                  var10 = this.startingBounds.height + var4;
                  break;
               default:
                  return;
               }

               BasicInternalFrameUI.this.getDesktopManager().resizeFrame(BasicInternalFrameUI.this.frame, var7, var8, var9, var10);
            }
         }
      }

      public void mouseMoved(MouseEvent var1) {
         if (BasicInternalFrameUI.this.frame.isResizable()) {
            if (var1.getSource() != BasicInternalFrameUI.this.frame && var1.getSource() != BasicInternalFrameUI.this.getNorthPane()) {
               BasicInternalFrameUI.this.updateFrameCursor();
            } else {
               Insets var2 = BasicInternalFrameUI.this.frame.getInsets();
               Point var3 = new Point(var1.getX(), var1.getY());
               if (var1.getSource() == BasicInternalFrameUI.this.getNorthPane()) {
                  Point var4 = BasicInternalFrameUI.this.getNorthPane().getLocation();
                  var3.x += var4.x;
                  var3.y += var4.y;
               }

               if (var3.x <= var2.left) {
                  if (var3.y < this.resizeCornerSize + var2.top) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
                  } else if (var3.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - var2.bottom) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
                  } else {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(10));
                  }
               } else if (var3.x >= BasicInternalFrameUI.this.frame.getWidth() - var2.right) {
                  if (var1.getY() < this.resizeCornerSize + var2.top) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
                  } else if (var3.y > BasicInternalFrameUI.this.frame.getHeight() - this.resizeCornerSize - var2.bottom) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
                  } else {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(11));
                  }
               } else if (var3.y <= var2.top) {
                  if (var3.x < this.resizeCornerSize + var2.left) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(6));
                  } else if (var3.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - var2.right) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(7));
                  } else {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(8));
                  }
               } else if (var3.y >= BasicInternalFrameUI.this.frame.getHeight() - var2.bottom) {
                  if (var3.x < this.resizeCornerSize + var2.left) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(4));
                  } else if (var3.x > BasicInternalFrameUI.this.frame.getWidth() - this.resizeCornerSize - var2.right) {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(5));
                  } else {
                     BasicInternalFrameUI.this.frame.setCursor(Cursor.getPredefinedCursor(9));
                  }
               } else {
                  BasicInternalFrameUI.this.updateFrameCursor();
               }

            }
         }
      }

      public void mouseEntered(MouseEvent var1) {
         BasicInternalFrameUI.this.updateFrameCursor();
      }

      public void mouseExited(MouseEvent var1) {
         BasicInternalFrameUI.this.updateFrameCursor();
      }
   }

   public class InternalFrameLayout implements LayoutManager {
      public void addLayoutComponent(String var1, Component var2) {
         BasicInternalFrameUI.this.getHandler().addLayoutComponent(var1, var2);
      }

      public void removeLayoutComponent(Component var1) {
         BasicInternalFrameUI.this.getHandler().removeLayoutComponent(var1);
      }

      public Dimension preferredLayoutSize(Container var1) {
         return BasicInternalFrameUI.this.getHandler().preferredLayoutSize(var1);
      }

      public Dimension minimumLayoutSize(Container var1) {
         return BasicInternalFrameUI.this.getHandler().minimumLayoutSize(var1);
      }

      public void layoutContainer(Container var1) {
         BasicInternalFrameUI.this.getHandler().layoutContainer(var1);
      }
   }

   public class InternalFramePropertyChangeListener implements PropertyChangeListener {
      public void propertyChange(PropertyChangeEvent var1) {
         BasicInternalFrameUI.this.getHandler().propertyChange(var1);
      }
   }
}
