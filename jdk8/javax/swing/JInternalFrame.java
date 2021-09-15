package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.DesktopIconUI;
import javax.swing.plaf.InternalFrameUI;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

public class JInternalFrame extends JComponent implements Accessible, WindowConstants, RootPaneContainer {
   private static final String uiClassID = "InternalFrameUI";
   protected JRootPane rootPane;
   protected boolean rootPaneCheckingEnabled;
   protected boolean closable;
   protected boolean isClosed;
   protected boolean maximizable;
   protected boolean isMaximum;
   protected boolean iconable;
   protected boolean isIcon;
   protected boolean resizable;
   protected boolean isSelected;
   protected Icon frameIcon;
   protected String title;
   protected JInternalFrame.JDesktopIcon desktopIcon;
   private Cursor lastCursor;
   private boolean opened;
   private Rectangle normalBounds;
   private int defaultCloseOperation;
   private Component lastFocusOwner;
   public static final String CONTENT_PANE_PROPERTY = "contentPane";
   public static final String MENU_BAR_PROPERTY = "JMenuBar";
   public static final String TITLE_PROPERTY = "title";
   public static final String LAYERED_PANE_PROPERTY = "layeredPane";
   public static final String ROOT_PANE_PROPERTY = "rootPane";
   public static final String GLASS_PANE_PROPERTY = "glassPane";
   public static final String FRAME_ICON_PROPERTY = "frameIcon";
   public static final String IS_SELECTED_PROPERTY = "selected";
   public static final String IS_CLOSED_PROPERTY = "closed";
   public static final String IS_MAXIMUM_PROPERTY = "maximum";
   public static final String IS_ICON_PROPERTY = "icon";
   private static final Object PROPERTY_CHANGE_LISTENER_KEY = new StringBuilder("InternalFramePropertyChangeListener");
   boolean isDragging;
   boolean danger;

   private static void addPropertyChangeListenerIfNecessary() {
      if (AppContext.getAppContext().get(PROPERTY_CHANGE_LISTENER_KEY) == null) {
         JInternalFrame.FocusPropertyChangeListener var0 = new JInternalFrame.FocusPropertyChangeListener();
         AppContext.getAppContext().put(PROPERTY_CHANGE_LISTENER_KEY, var0);
         KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(var0);
      }

   }

   private static void updateLastFocusOwner(Component var0) {
      if (var0 != null) {
         for(Object var1 = var0; var1 != null && !(var1 instanceof Window); var1 = ((Component)var1).getParent()) {
            if (var1 instanceof JInternalFrame) {
               ((JInternalFrame)var1).setLastFocusOwner(var0);
            }
         }
      }

   }

   public JInternalFrame() {
      this("", false, false, false, false);
   }

   public JInternalFrame(String var1) {
      this(var1, false, false, false, false);
   }

   public JInternalFrame(String var1, boolean var2) {
      this(var1, var2, false, false, false);
   }

   public JInternalFrame(String var1, boolean var2, boolean var3) {
      this(var1, var2, var3, false, false);
   }

   public JInternalFrame(String var1, boolean var2, boolean var3, boolean var4) {
      this(var1, var2, var3, var4, false);
   }

   public JInternalFrame(String var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      this.rootPaneCheckingEnabled = false;
      this.normalBounds = null;
      this.defaultCloseOperation = 2;
      this.isDragging = false;
      this.danger = false;
      this.setRootPane(this.createRootPane());
      this.setLayout(new BorderLayout());
      this.title = var1;
      this.resizable = var2;
      this.closable = var3;
      this.maximizable = var4;
      this.isMaximum = false;
      this.iconable = var5;
      this.isIcon = false;
      this.setVisible(false);
      this.setRootPaneCheckingEnabled(true);
      this.desktopIcon = new JInternalFrame.JDesktopIcon(this);
      this.updateUI();
      SunToolkit.checkAndSetPolicy(this);
      addPropertyChangeListenerIfNecessary();
   }

   protected JRootPane createRootPane() {
      return new JRootPane();
   }

   public InternalFrameUI getUI() {
      return (InternalFrameUI)this.ui;
   }

   public void setUI(InternalFrameUI var1) {
      boolean var2 = this.isRootPaneCheckingEnabled();

      try {
         this.setRootPaneCheckingEnabled(false);
         super.setUI(var1);
      } finally {
         this.setRootPaneCheckingEnabled(var2);
      }

   }

   public void updateUI() {
      this.setUI((InternalFrameUI)UIManager.getUI(this));
      this.invalidate();
      if (this.desktopIcon != null) {
         this.desktopIcon.updateUIWhenHidden();
      }

   }

   void updateUIWhenHidden() {
      this.setUI((InternalFrameUI)UIManager.getUI(this));
      this.invalidate();
      Component[] var1 = this.getComponents();
      if (var1 != null) {
         Component[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            SwingUtilities.updateComponentTreeUI(var5);
         }
      }

   }

   public String getUIClassID() {
      return "InternalFrameUI";
   }

   protected boolean isRootPaneCheckingEnabled() {
      return this.rootPaneCheckingEnabled;
   }

   protected void setRootPaneCheckingEnabled(boolean var1) {
      this.rootPaneCheckingEnabled = var1;
   }

   protected void addImpl(Component var1, Object var2, int var3) {
      if (this.isRootPaneCheckingEnabled()) {
         this.getContentPane().add(var1, var2, var3);
      } else {
         super.addImpl(var1, var2, var3);
      }

   }

   public void remove(Component var1) {
      int var2 = this.getComponentCount();
      super.remove(var1);
      if (var2 == this.getComponentCount()) {
         this.getContentPane().remove(var1);
      }

   }

   public void setLayout(LayoutManager var1) {
      if (this.isRootPaneCheckingEnabled()) {
         this.getContentPane().setLayout(var1);
      } else {
         super.setLayout(var1);
      }

   }

   /** @deprecated */
   @Deprecated
   public JMenuBar getMenuBar() {
      return this.getRootPane().getMenuBar();
   }

   public JMenuBar getJMenuBar() {
      return this.getRootPane().getJMenuBar();
   }

   /** @deprecated */
   @Deprecated
   public void setMenuBar(JMenuBar var1) {
      JMenuBar var2 = this.getMenuBar();
      this.getRootPane().setJMenuBar(var1);
      this.firePropertyChange("JMenuBar", var2, var1);
   }

   public void setJMenuBar(JMenuBar var1) {
      JMenuBar var2 = this.getMenuBar();
      this.getRootPane().setJMenuBar(var1);
      this.firePropertyChange("JMenuBar", var2, var1);
   }

   public Container getContentPane() {
      return this.getRootPane().getContentPane();
   }

   public void setContentPane(Container var1) {
      Container var2 = this.getContentPane();
      this.getRootPane().setContentPane(var1);
      this.firePropertyChange("contentPane", var2, var1);
   }

   public JLayeredPane getLayeredPane() {
      return this.getRootPane().getLayeredPane();
   }

   public void setLayeredPane(JLayeredPane var1) {
      JLayeredPane var2 = this.getLayeredPane();
      this.getRootPane().setLayeredPane(var1);
      this.firePropertyChange("layeredPane", var2, var1);
   }

   public Component getGlassPane() {
      return this.getRootPane().getGlassPane();
   }

   public void setGlassPane(Component var1) {
      Component var2 = this.getGlassPane();
      this.getRootPane().setGlassPane(var1);
      this.firePropertyChange("glassPane", var2, var1);
   }

   public JRootPane getRootPane() {
      return this.rootPane;
   }

   protected void setRootPane(JRootPane var1) {
      if (this.rootPane != null) {
         this.remove(this.rootPane);
      }

      JRootPane var2 = this.getRootPane();
      this.rootPane = var1;
      if (this.rootPane != null) {
         boolean var3 = this.isRootPaneCheckingEnabled();

         try {
            this.setRootPaneCheckingEnabled(false);
            this.add(this.rootPane, "Center");
         } finally {
            this.setRootPaneCheckingEnabled(var3);
         }
      }

      this.firePropertyChange("rootPane", var2, var1);
   }

   public void setClosable(boolean var1) {
      Boolean var2 = this.closable ? Boolean.TRUE : Boolean.FALSE;
      Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
      this.closable = var1;
      this.firePropertyChange("closable", var2, var3);
   }

   public boolean isClosable() {
      return this.closable;
   }

   public boolean isClosed() {
      return this.isClosed;
   }

   public void setClosed(boolean var1) throws PropertyVetoException {
      if (this.isClosed != var1) {
         Boolean var2 = this.isClosed ? Boolean.TRUE : Boolean.FALSE;
         Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
         if (var1) {
            this.fireInternalFrameEvent(25550);
         }

         this.fireVetoableChange("closed", var2, var3);
         this.isClosed = var1;
         if (this.isClosed) {
            this.setVisible(false);
         }

         this.firePropertyChange("closed", var2, var3);
         if (this.isClosed) {
            this.dispose();
         } else if (!this.opened) {
         }

      }
   }

   public void setResizable(boolean var1) {
      Boolean var2 = this.resizable ? Boolean.TRUE : Boolean.FALSE;
      Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
      this.resizable = var1;
      this.firePropertyChange("resizable", var2, var3);
   }

   public boolean isResizable() {
      return this.isMaximum ? false : this.resizable;
   }

   public void setIconifiable(boolean var1) {
      Boolean var2 = this.iconable ? Boolean.TRUE : Boolean.FALSE;
      Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
      this.iconable = var1;
      this.firePropertyChange("iconable", var2, var3);
   }

   public boolean isIconifiable() {
      return this.iconable;
   }

   public boolean isIcon() {
      return this.isIcon;
   }

   public void setIcon(boolean var1) throws PropertyVetoException {
      if (this.isIcon != var1) {
         this.firePropertyChange("ancestor", (Object)null, this.getParent());
         Boolean var2 = this.isIcon ? Boolean.TRUE : Boolean.FALSE;
         Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
         this.fireVetoableChange("icon", var2, var3);
         this.isIcon = var1;
         this.firePropertyChange("icon", var2, var3);
         if (var1) {
            this.fireInternalFrameEvent(25552);
         } else {
            this.fireInternalFrameEvent(25553);
         }

      }
   }

   public void setMaximizable(boolean var1) {
      Boolean var2 = this.maximizable ? Boolean.TRUE : Boolean.FALSE;
      Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
      this.maximizable = var1;
      this.firePropertyChange("maximizable", var2, var3);
   }

   public boolean isMaximizable() {
      return this.maximizable;
   }

   public boolean isMaximum() {
      return this.isMaximum;
   }

   public void setMaximum(boolean var1) throws PropertyVetoException {
      if (this.isMaximum != var1) {
         Boolean var2 = this.isMaximum ? Boolean.TRUE : Boolean.FALSE;
         Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
         this.fireVetoableChange("maximum", var2, var3);
         this.isMaximum = var1;
         this.firePropertyChange("maximum", var2, var3);
      }
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String var1) {
      String var2 = this.title;
      this.title = var1;
      this.firePropertyChange("title", var2, var1);
   }

   public void setSelected(boolean var1) throws PropertyVetoException {
      if (var1 && this.isSelected) {
         this.restoreSubcomponentFocus();
      } else {
         label44: {
            if (this.isSelected != var1) {
               if (!var1) {
                  break label44;
               }

               if (this.isIcon) {
                  if (this.desktopIcon.isShowing()) {
                     break label44;
                  }
               } else if (this.isShowing()) {
                  break label44;
               }
            }

            return;
         }

         Boolean var2 = this.isSelected ? Boolean.TRUE : Boolean.FALSE;
         Boolean var3 = var1 ? Boolean.TRUE : Boolean.FALSE;
         this.fireVetoableChange("selected", var2, var3);
         if (var1) {
            this.restoreSubcomponentFocus();
         }

         this.isSelected = var1;
         this.firePropertyChange("selected", var2, var3);
         if (this.isSelected) {
            this.fireInternalFrameEvent(25554);
         } else {
            this.fireInternalFrameEvent(25555);
         }

         this.repaint();
      }
   }

   public boolean isSelected() {
      return this.isSelected;
   }

   public void setFrameIcon(Icon var1) {
      Icon var2 = this.frameIcon;
      this.frameIcon = var1;
      this.firePropertyChange("frameIcon", var2, var1);
   }

   public Icon getFrameIcon() {
      return this.frameIcon;
   }

   public void moveToFront() {
      if (this.isIcon()) {
         if (this.getDesktopIcon().getParent() instanceof JLayeredPane) {
            ((JLayeredPane)this.getDesktopIcon().getParent()).moveToFront(this.getDesktopIcon());
         }
      } else if (this.getParent() instanceof JLayeredPane) {
         ((JLayeredPane)this.getParent()).moveToFront(this);
      }

   }

   public void moveToBack() {
      if (this.isIcon()) {
         if (this.getDesktopIcon().getParent() instanceof JLayeredPane) {
            ((JLayeredPane)this.getDesktopIcon().getParent()).moveToBack(this.getDesktopIcon());
         }
      } else if (this.getParent() instanceof JLayeredPane) {
         ((JLayeredPane)this.getParent()).moveToBack(this);
      }

   }

   public Cursor getLastCursor() {
      return this.lastCursor;
   }

   public void setCursor(Cursor var1) {
      if (var1 == null) {
         this.lastCursor = null;
         super.setCursor(var1);
      } else {
         int var2 = var1.getType();
         if (var2 != 4 && var2 != 5 && var2 != 6 && var2 != 7 && var2 != 8 && var2 != 9 && var2 != 10 && var2 != 11) {
            this.lastCursor = var1;
         }

         super.setCursor(var1);
      }
   }

   public void setLayer(Integer var1) {
      if (this.getParent() != null && this.getParent() instanceof JLayeredPane) {
         JLayeredPane var2 = (JLayeredPane)this.getParent();
         var2.setLayer(this, var1, var2.getPosition(this));
      } else {
         JLayeredPane.putLayer(this, var1);
         if (this.getParent() != null) {
            this.getParent().repaint(this.getX(), this.getY(), this.getWidth(), this.getHeight());
         }
      }

   }

   public void setLayer(int var1) {
      this.setLayer(var1);
   }

   public int getLayer() {
      return JLayeredPane.getLayer((JComponent)this);
   }

   public JDesktopPane getDesktopPane() {
      Container var1;
      for(var1 = this.getParent(); var1 != null && !(var1 instanceof JDesktopPane); var1 = var1.getParent()) {
      }

      if (var1 == null) {
         for(var1 = this.getDesktopIcon().getParent(); var1 != null && !(var1 instanceof JDesktopPane); var1 = var1.getParent()) {
         }
      }

      return (JDesktopPane)var1;
   }

   public void setDesktopIcon(JInternalFrame.JDesktopIcon var1) {
      JInternalFrame.JDesktopIcon var2 = this.getDesktopIcon();
      this.desktopIcon = var1;
      this.firePropertyChange("desktopIcon", var2, var1);
   }

   public JInternalFrame.JDesktopIcon getDesktopIcon() {
      return this.desktopIcon;
   }

   public Rectangle getNormalBounds() {
      return this.normalBounds != null ? this.normalBounds : this.getBounds();
   }

   public void setNormalBounds(Rectangle var1) {
      this.normalBounds = var1;
   }

   public Component getFocusOwner() {
      return this.isSelected() ? this.lastFocusOwner : null;
   }

   public Component getMostRecentFocusOwner() {
      if (this.isSelected()) {
         return this.getFocusOwner();
      } else if (this.lastFocusOwner != null) {
         return this.lastFocusOwner;
      } else {
         FocusTraversalPolicy var1 = this.getFocusTraversalPolicy();
         if (var1 instanceof InternalFrameFocusTraversalPolicy) {
            return ((InternalFrameFocusTraversalPolicy)var1).getInitialComponent(this);
         } else {
            Component var2 = var1.getDefaultComponent(this);
            return (Component)(var2 != null ? var2 : this.getContentPane());
         }
      }
   }

   public void restoreSubcomponentFocus() {
      if (this.isIcon()) {
         SwingUtilities2.compositeRequestFocus(this.getDesktopIcon());
      } else {
         Component var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
         if (var1 == null || !SwingUtilities.isDescendingFrom(var1, this)) {
            this.setLastFocusOwner(this.getMostRecentFocusOwner());
            if (this.lastFocusOwner == null) {
               this.setLastFocusOwner(this.getContentPane());
            }

            this.lastFocusOwner.requestFocus();
         }
      }

   }

   private void setLastFocusOwner(Component var1) {
      this.lastFocusOwner = var1;
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      super.reshape(var1, var2, var3, var4);
      this.validate();
      this.repaint();
   }

   public void addInternalFrameListener(InternalFrameListener var1) {
      this.listenerList.add(InternalFrameListener.class, var1);
      this.enableEvents(0L);
   }

   public void removeInternalFrameListener(InternalFrameListener var1) {
      this.listenerList.remove(InternalFrameListener.class, var1);
   }

   public InternalFrameListener[] getInternalFrameListeners() {
      return (InternalFrameListener[])this.listenerList.getListeners(InternalFrameListener.class);
   }

   protected void fireInternalFrameEvent(int var1) {
      Object[] var2 = this.listenerList.getListenerList();
      InternalFrameEvent var3 = null;

      for(int var4 = var2.length - 2; var4 >= 0; var4 -= 2) {
         if (var2[var4] == InternalFrameListener.class) {
            if (var3 == null) {
               var3 = new InternalFrameEvent(this, var1);
            }

            switch(var3.getID()) {
            case 25549:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameOpened(var3);
               break;
            case 25550:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameClosing(var3);
               break;
            case 25551:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameClosed(var3);
               break;
            case 25552:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameIconified(var3);
               break;
            case 25553:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameDeiconified(var3);
               break;
            case 25554:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameActivated(var3);
               break;
            case 25555:
               ((InternalFrameListener)var2[var4 + 1]).internalFrameDeactivated(var3);
            }
         }
      }

   }

   public void doDefaultCloseAction() {
      this.fireInternalFrameEvent(25550);
      switch(this.defaultCloseOperation) {
      case 0:
      default:
         break;
      case 1:
         this.setVisible(false);
         if (this.isSelected()) {
            try {
               this.setSelected(false);
            } catch (PropertyVetoException var3) {
            }
         }
         break;
      case 2:
         try {
            this.fireVetoableChange("closed", Boolean.FALSE, Boolean.TRUE);
            this.isClosed = true;
            this.setVisible(false);
            this.firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
            this.dispose();
         } catch (PropertyVetoException var2) {
         }
      }

   }

   public void setDefaultCloseOperation(int var1) {
      this.defaultCloseOperation = var1;
   }

   public int getDefaultCloseOperation() {
      return this.defaultCloseOperation;
   }

   public void pack() {
      try {
         if (this.isIcon()) {
            this.setIcon(false);
         } else if (this.isMaximum()) {
            this.setMaximum(false);
         }
      } catch (PropertyVetoException var2) {
         return;
      }

      this.setSize(this.getPreferredSize());
      this.validate();
   }

   public void show() {
      if (!this.isVisible()) {
         if (!this.opened) {
            this.fireInternalFrameEvent(25549);
            this.opened = true;
         }

         this.getDesktopIcon().setVisible(true);
         this.toFront();
         super.show();
         if (!this.isIcon) {
            if (!this.isSelected()) {
               try {
                  this.setSelected(true);
               } catch (PropertyVetoException var2) {
               }
            }

         }
      }
   }

   public void hide() {
      if (this.isIcon()) {
         this.getDesktopIcon().setVisible(false);
      }

      super.hide();
   }

   public void dispose() {
      if (this.isVisible()) {
         this.setVisible(false);
      }

      if (this.isSelected()) {
         try {
            this.setSelected(false);
         } catch (PropertyVetoException var2) {
         }
      }

      if (!this.isClosed) {
         this.firePropertyChange("closed", Boolean.FALSE, Boolean.TRUE);
         this.isClosed = true;
      }

      this.fireInternalFrameEvent(25551);
   }

   public void toFront() {
      this.moveToFront();
   }

   public void toBack() {
      this.moveToBack();
   }

   public final void setFocusCycleRoot(boolean var1) {
   }

   public final boolean isFocusCycleRoot() {
      return true;
   }

   public final Container getFocusCycleRootAncestor() {
      return null;
   }

   public final String getWarningString() {
      return null;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("InternalFrameUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            boolean var3 = this.isRootPaneCheckingEnabled();

            try {
               this.setRootPaneCheckingEnabled(false);
               this.ui.installUI(this);
            } finally {
               this.setRootPaneCheckingEnabled(var3);
            }
         }
      }

   }

   void compWriteObjectNotify() {
      boolean var1 = this.isRootPaneCheckingEnabled();

      try {
         this.setRootPaneCheckingEnabled(false);
         super.compWriteObjectNotify();
      } finally {
         this.setRootPaneCheckingEnabled(var1);
      }

   }

   protected String paramString() {
      String var1 = this.rootPane != null ? this.rootPane.toString() : "";
      String var2 = this.rootPaneCheckingEnabled ? "true" : "false";
      String var3 = this.closable ? "true" : "false";
      String var4 = this.isClosed ? "true" : "false";
      String var5 = this.maximizable ? "true" : "false";
      String var6 = this.isMaximum ? "true" : "false";
      String var7 = this.iconable ? "true" : "false";
      String var8 = this.isIcon ? "true" : "false";
      String var9 = this.resizable ? "true" : "false";
      String var10 = this.isSelected ? "true" : "false";
      String var11 = this.frameIcon != null ? this.frameIcon.toString() : "";
      String var12 = this.title != null ? this.title : "";
      String var13 = this.desktopIcon != null ? this.desktopIcon.toString() : "";
      String var14 = this.opened ? "true" : "false";
      String var15;
      if (this.defaultCloseOperation == 1) {
         var15 = "HIDE_ON_CLOSE";
      } else if (this.defaultCloseOperation == 2) {
         var15 = "DISPOSE_ON_CLOSE";
      } else if (this.defaultCloseOperation == 0) {
         var15 = "DO_NOTHING_ON_CLOSE";
      } else {
         var15 = "";
      }

      return super.paramString() + ",closable=" + var3 + ",defaultCloseOperation=" + var15 + ",desktopIcon=" + var13 + ",frameIcon=" + var11 + ",iconable=" + var7 + ",isClosed=" + var4 + ",isIcon=" + var8 + ",isMaximum=" + var6 + ",isSelected=" + var10 + ",maximizable=" + var5 + ",opened=" + var14 + ",resizable=" + var9 + ",rootPane=" + var1 + ",rootPaneCheckingEnabled=" + var2 + ",title=" + var12;
   }

   protected void paintComponent(Graphics var1) {
      if (this.isDragging) {
         this.danger = true;
      }

      super.paintComponent(var1);
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JInternalFrame.AccessibleJInternalFrame();
      }

      return this.accessibleContext;
   }

   public static class JDesktopIcon extends JComponent implements Accessible {
      JInternalFrame internalFrame;

      public JDesktopIcon(JInternalFrame var1) {
         this.setVisible(false);
         this.setInternalFrame(var1);
         this.updateUI();
      }

      public DesktopIconUI getUI() {
         return (DesktopIconUI)this.ui;
      }

      public void setUI(DesktopIconUI var1) {
         super.setUI(var1);
      }

      public JInternalFrame getInternalFrame() {
         return this.internalFrame;
      }

      public void setInternalFrame(JInternalFrame var1) {
         this.internalFrame = var1;
      }

      public JDesktopPane getDesktopPane() {
         return this.getInternalFrame() != null ? this.getInternalFrame().getDesktopPane() : null;
      }

      public void updateUI() {
         boolean var1 = this.ui != null;
         this.setUI((DesktopIconUI)UIManager.getUI(this));
         this.invalidate();
         Dimension var2 = this.getPreferredSize();
         this.setSize(var2.width, var2.height);
         if (this.internalFrame != null && this.internalFrame.getUI() != null) {
            SwingUtilities.updateComponentTreeUI(this.internalFrame);
         }

      }

      void updateUIWhenHidden() {
         this.setUI((DesktopIconUI)UIManager.getUI(this));
         Dimension var1 = this.getPreferredSize();
         this.setSize(var1.width, var1.height);
         this.invalidate();
         Component[] var2 = this.getComponents();
         if (var2 != null) {
            Component[] var3 = var2;
            int var4 = var2.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Component var6 = var3[var5];
               SwingUtilities.updateComponentTreeUI(var6);
            }
         }

      }

      public String getUIClassID() {
         return "DesktopIconUI";
      }

      private void writeObject(ObjectOutputStream var1) throws IOException {
         var1.defaultWriteObject();
         if (this.getUIClassID().equals("DesktopIconUI")) {
            byte var2 = JComponent.getWriteObjCounter(this);
            --var2;
            JComponent.setWriteObjCounter(this, var2);
            if (var2 == 0 && this.ui != null) {
               this.ui.installUI(this);
            }
         }

      }

      public AccessibleContext getAccessibleContext() {
         if (this.accessibleContext == null) {
            this.accessibleContext = new JInternalFrame.JDesktopIcon.AccessibleJDesktopIcon();
         }

         return this.accessibleContext;
      }

      protected class AccessibleJDesktopIcon extends JComponent.AccessibleJComponent implements AccessibleValue {
         protected AccessibleJDesktopIcon() {
            super();
         }

         public AccessibleRole getAccessibleRole() {
            return AccessibleRole.DESKTOP_ICON;
         }

         public AccessibleValue getAccessibleValue() {
            return this;
         }

         public Number getCurrentAccessibleValue() {
            AccessibleContext var1 = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
            AccessibleValue var2 = var1.getAccessibleValue();
            return var2 != null ? var2.getCurrentAccessibleValue() : null;
         }

         public boolean setCurrentAccessibleValue(Number var1) {
            if (var1 == null) {
               return false;
            } else {
               AccessibleContext var2 = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
               AccessibleValue var3 = var2.getAccessibleValue();
               return var3 != null ? var3.setCurrentAccessibleValue(var1) : false;
            }
         }

         public Number getMinimumAccessibleValue() {
            AccessibleContext var1 = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
            return var1 instanceof AccessibleValue ? ((AccessibleValue)var1).getMinimumAccessibleValue() : null;
         }

         public Number getMaximumAccessibleValue() {
            AccessibleContext var1 = JDesktopIcon.this.getInternalFrame().getAccessibleContext();
            return var1 instanceof AccessibleValue ? ((AccessibleValue)var1).getMaximumAccessibleValue() : null;
         }
      }
   }

   protected class AccessibleJInternalFrame extends JComponent.AccessibleJComponent implements AccessibleValue {
      protected AccessibleJInternalFrame() {
         super();
      }

      public String getAccessibleName() {
         String var1 = this.accessibleName;
         if (var1 == null) {
            var1 = (String)JInternalFrame.this.getClientProperty("AccessibleName");
         }

         if (var1 == null) {
            var1 = JInternalFrame.this.getTitle();
         }

         return var1;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.INTERNAL_FRAME;
      }

      public AccessibleValue getAccessibleValue() {
         return this;
      }

      public Number getCurrentAccessibleValue() {
         return JInternalFrame.this.getLayer();
      }

      public boolean setCurrentAccessibleValue(Number var1) {
         if (var1 == null) {
            return false;
         } else {
            JInternalFrame.this.setLayer(new Integer(var1.intValue()));
            return true;
         }
      }

      public Number getMinimumAccessibleValue() {
         return Integer.MIN_VALUE;
      }

      public Number getMaximumAccessibleValue() {
         return Integer.MAX_VALUE;
      }
   }

   private static class FocusPropertyChangeListener implements PropertyChangeListener {
      private FocusPropertyChangeListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getPropertyName() == "permanentFocusOwner") {
            JInternalFrame.updateLastFocusOwner((Component)var1.getNewValue());
         }

      }

      // $FF: synthetic method
      FocusPropertyChangeListener(Object var1) {
         this();
      }
   }
}
