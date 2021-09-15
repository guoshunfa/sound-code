package javax.swing.plaf.basic;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import javax.swing.plaf.UIResource;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.UngrabEvent;
import sun.swing.UIAction;

public class BasicPopupMenuUI extends PopupMenuUI {
   static final StringBuilder MOUSE_GRABBER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MouseGrabber");
   static final StringBuilder MENU_KEYBOARD_HELPER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MenuKeyboardHelper");
   protected JPopupMenu popupMenu = null;
   private transient PopupMenuListener popupMenuListener = null;
   private MenuKeyListener menuKeyListener = null;
   private static boolean checkedUnpostPopup;
   private static boolean unpostPopup;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicPopupMenuUI();
   }

   public BasicPopupMenuUI() {
      BasicLookAndFeel.needsEventHelper = true;
      LookAndFeel var1 = UIManager.getLookAndFeel();
      if (var1 instanceof BasicLookAndFeel) {
         ((BasicLookAndFeel)var1).installAWTEventListener();
      }

   }

   public void installUI(JComponent var1) {
      this.popupMenu = (JPopupMenu)var1;
      this.installDefaults();
      this.installListeners();
      this.installKeyboardActions();
   }

   public void installDefaults() {
      if (this.popupMenu.getLayout() == null || this.popupMenu.getLayout() instanceof UIResource) {
         this.popupMenu.setLayout(new DefaultMenuLayout(this.popupMenu, 1));
      }

      LookAndFeel.installProperty(this.popupMenu, "opaque", Boolean.TRUE);
      LookAndFeel.installBorder(this.popupMenu, "PopupMenu.border");
      LookAndFeel.installColorsAndFont(this.popupMenu, "PopupMenu.background", "PopupMenu.foreground", "PopupMenu.font");
   }

   protected void installListeners() {
      if (this.popupMenuListener == null) {
         this.popupMenuListener = new BasicPopupMenuUI.BasicPopupMenuListener();
      }

      this.popupMenu.addPopupMenuListener(this.popupMenuListener);
      if (this.menuKeyListener == null) {
         this.menuKeyListener = new BasicPopupMenuUI.BasicMenuKeyListener();
      }

      this.popupMenu.addMenuKeyListener(this.menuKeyListener);
      AppContext var1 = AppContext.getAppContext();
      synchronized(MOUSE_GRABBER_KEY) {
         BasicPopupMenuUI.MouseGrabber var3 = (BasicPopupMenuUI.MouseGrabber)var1.get(MOUSE_GRABBER_KEY);
         if (var3 == null) {
            var3 = new BasicPopupMenuUI.MouseGrabber();
            var1.put(MOUSE_GRABBER_KEY, var3);
         }
      }

      synchronized(MENU_KEYBOARD_HELPER_KEY) {
         BasicPopupMenuUI.MenuKeyboardHelper var8 = (BasicPopupMenuUI.MenuKeyboardHelper)var1.get(MENU_KEYBOARD_HELPER_KEY);
         if (var8 == null) {
            var8 = new BasicPopupMenuUI.MenuKeyboardHelper();
            var1.put(MENU_KEYBOARD_HELPER_KEY, var8);
            MenuSelectionManager var4 = MenuSelectionManager.defaultManager();
            var4.addChangeListener(var8);
         }

      }
   }

   protected void installKeyboardActions() {
   }

   static InputMap getInputMap(JPopupMenu var0, JComponent var1) {
      ComponentInputMap var2 = null;
      Object[] var3 = (Object[])((Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings"));
      if (var3 != null) {
         var2 = LookAndFeel.makeComponentInputMap(var1, var3);
         if (!var0.getComponentOrientation().isLeftToRight()) {
            Object[] var4 = (Object[])((Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft"));
            if (var4 != null) {
               ComponentInputMap var5 = LookAndFeel.makeComponentInputMap(var1, var4);
               var5.setParent(var2);
               var2 = var5;
            }
         }
      }

      return var2;
   }

   static ActionMap getActionMap() {
      return LazyActionMap.getActionMap(BasicPopupMenuUI.class, "PopupMenu.actionMap");
   }

   static void loadActionMap(LazyActionMap var0) {
      var0.put(new BasicPopupMenuUI.Actions("cancel"));
      var0.put(new BasicPopupMenuUI.Actions("selectNext"));
      var0.put(new BasicPopupMenuUI.Actions("selectPrevious"));
      var0.put(new BasicPopupMenuUI.Actions("selectParent"));
      var0.put(new BasicPopupMenuUI.Actions("selectChild"));
      var0.put(new BasicPopupMenuUI.Actions("return"));
      BasicLookAndFeel.installAudioActionMap(var0);
   }

   public void uninstallUI(JComponent var1) {
      this.uninstallDefaults();
      this.uninstallListeners();
      this.uninstallKeyboardActions();
      this.popupMenu = null;
   }

   protected void uninstallDefaults() {
      LookAndFeel.uninstallBorder(this.popupMenu);
   }

   protected void uninstallListeners() {
      if (this.popupMenuListener != null) {
         this.popupMenu.removePopupMenuListener(this.popupMenuListener);
      }

      if (this.menuKeyListener != null) {
         this.popupMenu.removeMenuKeyListener(this.menuKeyListener);
      }

   }

   protected void uninstallKeyboardActions() {
      SwingUtilities.replaceUIActionMap(this.popupMenu, (ActionMap)null);
      SwingUtilities.replaceUIInputMap(this.popupMenu, 2, (InputMap)null);
   }

   static MenuElement getFirstPopup() {
      MenuSelectionManager var0 = MenuSelectionManager.defaultManager();
      MenuElement[] var1 = var0.getSelectedPath();
      MenuElement var2 = null;

      for(int var3 = 0; var2 == null && var3 < var1.length; ++var3) {
         if (var1[var3] instanceof JPopupMenu) {
            var2 = var1[var3];
         }
      }

      return var2;
   }

   static JPopupMenu getLastPopup() {
      MenuSelectionManager var0 = MenuSelectionManager.defaultManager();
      MenuElement[] var1 = var0.getSelectedPath();
      JPopupMenu var2 = null;

      for(int var3 = var1.length - 1; var2 == null && var3 >= 0; --var3) {
         if (var1[var3] instanceof JPopupMenu) {
            var2 = (JPopupMenu)var1[var3];
         }
      }

      return var2;
   }

   static List<JPopupMenu> getPopups() {
      MenuSelectionManager var0 = MenuSelectionManager.defaultManager();
      MenuElement[] var1 = var0.getSelectedPath();
      ArrayList var2 = new ArrayList(var1.length);
      MenuElement[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         MenuElement var6 = var3[var5];
         if (var6 instanceof JPopupMenu) {
            var2.add((JPopupMenu)var6);
         }
      }

      return var2;
   }

   public boolean isPopupTrigger(MouseEvent var1) {
      return var1.getID() == 502 && (var1.getModifiers() & 4) != 0;
   }

   private static boolean checkInvokerEqual(MenuElement var0, MenuElement var1) {
      Component var2 = var0.getComponent();
      Component var3 = var1.getComponent();
      if (var2 instanceof JPopupMenu) {
         var2 = ((JPopupMenu)var2).getInvoker();
      }

      if (var3 instanceof JPopupMenu) {
         var3 = ((JPopupMenu)var3).getInvoker();
      }

      return var2 == var3;
   }

   private static MenuElement nextEnabledChild(MenuElement[] var0, int var1, int var2) {
      for(int var3 = var1; var3 <= var2; ++var3) {
         if (var0[var3] != null) {
            Component var4 = var0[var3].getComponent();
            if (var4 != null && (var4.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && var4.isVisible()) {
               return var0[var3];
            }
         }
      }

      return null;
   }

   private static MenuElement previousEnabledChild(MenuElement[] var0, int var1, int var2) {
      for(int var3 = var1; var3 >= var2; --var3) {
         if (var0[var3] != null) {
            Component var4 = var0[var3].getComponent();
            if (var4 != null && (var4.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && var4.isVisible()) {
               return var0[var3];
            }
         }
      }

      return null;
   }

   static MenuElement findEnabledChild(MenuElement[] var0, int var1, boolean var2) {
      MenuElement var3;
      if (var2) {
         var3 = nextEnabledChild(var0, var1 + 1, var0.length - 1);
         if (var3 == null) {
            var3 = nextEnabledChild(var0, 0, var1 - 1);
         }
      } else {
         var3 = previousEnabledChild(var0, var1 - 1, 0);
         if (var3 == null) {
            var3 = previousEnabledChild(var0, var0.length - 1, var1 + 1);
         }
      }

      return var3;
   }

   static MenuElement findEnabledChild(MenuElement[] var0, MenuElement var1, boolean var2) {
      for(int var3 = 0; var3 < var0.length; ++var3) {
         if (var0[var3] == var1) {
            return findEnabledChild(var0, var3, var2);
         }
      }

      return null;
   }

   static class MenuKeyboardHelper implements ChangeListener, KeyListener {
      private Component lastFocused = null;
      private MenuElement[] lastPathSelected = new MenuElement[0];
      private JPopupMenu lastPopup;
      private JRootPane invokerRootPane;
      private ActionMap menuActionMap = BasicPopupMenuUI.getActionMap();
      private InputMap menuInputMap;
      private boolean focusTraversalKeysEnabled;
      private boolean receivedKeyPressed = false;
      private FocusListener rootPaneFocusListener = new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
            Component var2 = var1.getOppositeComponent();
            if (var2 != null) {
               MenuKeyboardHelper.this.lastFocused = var2;
            }

            var1.getComponent().removeFocusListener(this);
         }
      };

      void removeItems() {
         if (this.lastFocused != null) {
            if (!this.lastFocused.requestFocusInWindow()) {
               Window var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
               if (var1 != null && "###focusableSwingPopup###".equals(var1.getName())) {
                  this.lastFocused.requestFocus();
               }
            }

            this.lastFocused = null;
         }

         if (this.invokerRootPane != null) {
            this.invokerRootPane.removeKeyListener(this);
            this.invokerRootPane.setFocusTraversalKeysEnabled(this.focusTraversalKeysEnabled);
            this.removeUIInputMap(this.invokerRootPane, this.menuInputMap);
            this.removeUIActionMap(this.invokerRootPane, this.menuActionMap);
            this.invokerRootPane = null;
         }

         this.receivedKeyPressed = false;
      }

      JPopupMenu getActivePopup(MenuElement[] var1) {
         for(int var2 = var1.length - 1; var2 >= 0; --var2) {
            MenuElement var3 = var1[var2];
            if (var3 instanceof JPopupMenu) {
               return (JPopupMenu)var3;
            }
         }

         return null;
      }

      void addUIInputMap(JComponent var1, InputMap var2) {
         InputMap var3 = null;

         InputMap var4;
         for(var4 = var1.getInputMap(2); var4 != null && !(var4 instanceof UIResource); var4 = var4.getParent()) {
            var3 = var4;
         }

         if (var3 == null) {
            var1.setInputMap(2, var2);
         } else {
            var3.setParent(var2);
         }

         var2.setParent(var4);
      }

      void addUIActionMap(JComponent var1, ActionMap var2) {
         ActionMap var3 = null;

         ActionMap var4;
         for(var4 = var1.getActionMap(); var4 != null && !(var4 instanceof UIResource); var4 = var4.getParent()) {
            var3 = var4;
         }

         if (var3 == null) {
            var1.setActionMap(var2);
         } else {
            var3.setParent(var2);
         }

         var2.setParent(var4);
      }

      void removeUIInputMap(JComponent var1, InputMap var2) {
         InputMap var3 = null;

         for(InputMap var4 = var1.getInputMap(2); var4 != null; var4 = var4.getParent()) {
            if (var4 == var2) {
               if (var3 == null) {
                  var1.setInputMap(2, var2.getParent());
               } else {
                  var3.setParent(var2.getParent());
               }
               break;
            }

            var3 = var4;
         }

      }

      void removeUIActionMap(JComponent var1, ActionMap var2) {
         ActionMap var3 = null;

         for(ActionMap var4 = var1.getActionMap(); var4 != null; var4 = var4.getParent()) {
            if (var4 == var2) {
               if (var3 == null) {
                  var1.setActionMap(var2.getParent());
               } else {
                  var3.setParent(var2.getParent());
               }
               break;
            }

            var3 = var4;
         }

      }

      public void stateChanged(ChangeEvent var1) {
         if (!(UIManager.getLookAndFeel() instanceof BasicLookAndFeel)) {
            this.uninstall();
         } else {
            MenuSelectionManager var2 = (MenuSelectionManager)var1.getSource();
            MenuElement[] var3 = var2.getSelectedPath();
            JPopupMenu var4 = this.getActivePopup(var3);
            if (var4 == null || var4.isFocusable()) {
               if (this.lastPathSelected.length != 0 && var3.length != 0 && !BasicPopupMenuUI.checkInvokerEqual(var3[0], this.lastPathSelected[0])) {
                  this.removeItems();
                  this.lastPathSelected = new MenuElement[0];
               }

               if (this.lastPathSelected.length == 0 && var3.length > 0) {
                  Object var5;
                  if (var4 == null) {
                     if (var3.length != 2 || !(var3[0] instanceof JMenuBar) || !(var3[1] instanceof JMenu)) {
                        return;
                     }

                     var5 = (JComponent)var3[1];
                     var4 = ((JMenu)var5).getPopupMenu();
                  } else {
                     Object var6 = var4.getInvoker();
                     if (var6 instanceof JFrame) {
                        var5 = ((JFrame)var6).getRootPane();
                     } else if (var6 instanceof JDialog) {
                        var5 = ((JDialog)var6).getRootPane();
                     } else if (var6 instanceof JApplet) {
                        var5 = ((JApplet)var6).getRootPane();
                     } else {
                        while(!(var6 instanceof JComponent)) {
                           if (var6 == null) {
                              return;
                           }

                           var6 = ((Component)var6).getParent();
                        }

                        var5 = (JComponent)var6;
                     }
                  }

                  this.lastFocused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                  this.invokerRootPane = SwingUtilities.getRootPane((Component)var5);
                  if (this.invokerRootPane != null) {
                     this.invokerRootPane.addFocusListener(this.rootPaneFocusListener);
                     this.invokerRootPane.requestFocus(true);
                     this.invokerRootPane.addKeyListener(this);
                     this.focusTraversalKeysEnabled = this.invokerRootPane.getFocusTraversalKeysEnabled();
                     this.invokerRootPane.setFocusTraversalKeysEnabled(false);
                     this.menuInputMap = BasicPopupMenuUI.getInputMap(var4, this.invokerRootPane);
                     this.addUIInputMap(this.invokerRootPane, this.menuInputMap);
                     this.addUIActionMap(this.invokerRootPane, this.menuActionMap);
                  }
               } else if (this.lastPathSelected.length != 0 && var3.length == 0) {
                  this.removeItems();
               } else if (var4 != this.lastPopup) {
                  this.receivedKeyPressed = false;
               }

               this.lastPathSelected = var3;
               this.lastPopup = var4;
            }
         }
      }

      public void keyPressed(KeyEvent var1) {
         this.receivedKeyPressed = true;
         MenuSelectionManager.defaultManager().processKeyEvent(var1);
      }

      public void keyReleased(KeyEvent var1) {
         if (this.receivedKeyPressed) {
            this.receivedKeyPressed = false;
            MenuSelectionManager.defaultManager().processKeyEvent(var1);
         }

      }

      public void keyTyped(KeyEvent var1) {
         if (this.receivedKeyPressed) {
            MenuSelectionManager.defaultManager().processKeyEvent(var1);
         }

      }

      void uninstall() {
         synchronized(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
            MenuSelectionManager.defaultManager().removeChangeListener(this);
            AppContext.getAppContext().remove(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
         }
      }
   }

   static class MouseGrabber implements ChangeListener, AWTEventListener, ComponentListener, WindowListener {
      Window grabbedWindow;
      MenuElement[] lastPathSelected;

      public MouseGrabber() {
         MenuSelectionManager var1 = MenuSelectionManager.defaultManager();
         var1.addChangeListener(this);
         this.lastPathSelected = var1.getSelectedPath();
         if (this.lastPathSelected.length != 0) {
            this.grabWindow(this.lastPathSelected);
         }

      }

      void uninstall() {
         synchronized(BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
            MenuSelectionManager.defaultManager().removeChangeListener(this);
            this.ungrabWindow();
            AppContext.getAppContext().remove(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
         }
      }

      void grabWindow(MenuElement[] var1) {
         final Toolkit var2 = Toolkit.getDefaultToolkit();
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               var2.addAWTEventListener(MouseGrabber.this, -2147352464L);
               return null;
            }
         });
         Component var3 = var1[0].getComponent();
         if (var3 instanceof JPopupMenu) {
            var3 = ((JPopupMenu)var3).getInvoker();
         }

         this.grabbedWindow = var3 instanceof Window ? (Window)var3 : SwingUtilities.getWindowAncestor(var3);
         if (this.grabbedWindow != null) {
            if (var2 instanceof SunToolkit) {
               ((SunToolkit)var2).grab(this.grabbedWindow);
            } else {
               this.grabbedWindow.addComponentListener(this);
               this.grabbedWindow.addWindowListener(this);
            }
         }

      }

      void ungrabWindow() {
         final Toolkit var1 = Toolkit.getDefaultToolkit();
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               var1.removeAWTEventListener(MouseGrabber.this);
               return null;
            }
         });
         this.realUngrabWindow();
      }

      void realUngrabWindow() {
         Toolkit var1 = Toolkit.getDefaultToolkit();
         if (this.grabbedWindow != null) {
            if (var1 instanceof SunToolkit) {
               ((SunToolkit)var1).ungrab(this.grabbedWindow);
            } else {
               this.grabbedWindow.removeComponentListener(this);
               this.grabbedWindow.removeWindowListener(this);
            }

            this.grabbedWindow = null;
         }

      }

      public void stateChanged(ChangeEvent var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         MenuElement[] var3 = var2.getSelectedPath();
         if (this.lastPathSelected.length == 0 && var3.length != 0) {
            this.grabWindow(var3);
         }

         if (this.lastPathSelected.length != 0 && var3.length == 0) {
            this.ungrabWindow();
         }

         this.lastPathSelected = var3;
      }

      public void eventDispatched(AWTEvent var1) {
         if (var1 instanceof UngrabEvent) {
            this.cancelPopupMenu();
         } else if (var1 instanceof MouseEvent) {
            MouseEvent var2 = (MouseEvent)var1;
            Component var3 = var2.getComponent();
            switch(var2.getID()) {
            case 501:
               if (this.isInPopup(var3) || var3 instanceof JMenu && ((JMenu)var3).isSelected()) {
                  return;
               }

               if (!(var3 instanceof JComponent) || ((JComponent)var3).getClientProperty("doNotCancelPopup") != BasicComboBoxUI.HIDE_POPUP_KEY) {
                  this.cancelPopupMenu();
                  boolean var4 = UIManager.getBoolean("PopupMenu.consumeEventOnClose");
                  if (var4 && !(var3 instanceof MenuElement)) {
                     var2.consume();
                  }
               }
               break;
            case 502:
               if ((var3 instanceof MenuElement || !this.isInPopup(var3)) && (var3 instanceof JMenu || !(var3 instanceof JMenuItem))) {
                  MenuSelectionManager.defaultManager().processMouseEvent(var2);
               }
            case 503:
            case 504:
            case 505:
            default:
               break;
            case 506:
               if (var3 instanceof MenuElement || !this.isInPopup(var3)) {
                  MenuSelectionManager.defaultManager().processMouseEvent(var2);
               }
               break;
            case 507:
               if (this.isInPopup(var3) || var3 instanceof JComboBox && ((JComboBox)var3).isPopupVisible()) {
                  return;
               }

               this.cancelPopupMenu();
            }

         }
      }

      boolean isInPopup(Component var1) {
         for(Object var2 = var1; var2 != null && !(var2 instanceof Applet) && !(var2 instanceof Window); var2 = ((Component)var2).getParent()) {
            if (var2 instanceof JPopupMenu) {
               return true;
            }
         }

         return false;
      }

      void cancelPopupMenu() {
         try {
            List var1 = BasicPopupMenuUI.getPopups();
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               JPopupMenu var3 = (JPopupMenu)var2.next();
               var3.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
            }

            MenuSelectionManager.defaultManager().clearSelectedPath();
         } catch (RuntimeException var4) {
            this.realUngrabWindow();
            throw var4;
         } catch (Error var5) {
            this.realUngrabWindow();
            throw var5;
         }
      }

      public void componentResized(ComponentEvent var1) {
         this.cancelPopupMenu();
      }

      public void componentMoved(ComponentEvent var1) {
         this.cancelPopupMenu();
      }

      public void componentShown(ComponentEvent var1) {
         this.cancelPopupMenu();
      }

      public void componentHidden(ComponentEvent var1) {
         this.cancelPopupMenu();
      }

      public void windowClosing(WindowEvent var1) {
         this.cancelPopupMenu();
      }

      public void windowClosed(WindowEvent var1) {
         this.cancelPopupMenu();
      }

      public void windowIconified(WindowEvent var1) {
         this.cancelPopupMenu();
      }

      public void windowDeactivated(WindowEvent var1) {
         this.cancelPopupMenu();
      }

      public void windowOpened(WindowEvent var1) {
      }

      public void windowDeiconified(WindowEvent var1) {
      }

      public void windowActivated(WindowEvent var1) {
      }
   }

   private static class Actions extends UIAction {
      private static final String CANCEL = "cancel";
      private static final String SELECT_NEXT = "selectNext";
      private static final String SELECT_PREVIOUS = "selectPrevious";
      private static final String SELECT_PARENT = "selectParent";
      private static final String SELECT_CHILD = "selectChild";
      private static final String RETURN = "return";
      private static final boolean FORWARD = true;
      private static final boolean BACKWARD = false;
      private static final boolean PARENT = false;
      private static final boolean CHILD = true;

      Actions(String var1) {
         super(var1);
      }

      public void actionPerformed(ActionEvent var1) {
         String var2 = this.getName();
         if (var2 == "cancel") {
            this.cancel();
         } else if (var2 == "selectNext") {
            this.selectItem(true);
         } else if (var2 == "selectPrevious") {
            this.selectItem(false);
         } else if (var2 == "selectParent") {
            this.selectParentChild(false);
         } else if (var2 == "selectChild") {
            this.selectParentChild(true);
         } else if (var2 == "return") {
            this.doReturn();
         }

      }

      private void doReturn() {
         KeyboardFocusManager var1 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
         Component var2 = var1.getFocusOwner();
         if (var2 == null || var2 instanceof JRootPane) {
            MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
            MenuElement[] var4 = var3.getSelectedPath();
            if (var4.length > 0) {
               MenuElement var5 = var4[var4.length - 1];
               if (var5 instanceof JMenu) {
                  MenuElement[] var6 = new MenuElement[var4.length + 1];
                  System.arraycopy(var4, 0, var6, 0, var4.length);
                  var6[var4.length] = ((JMenu)var5).getPopupMenu();
                  var3.setSelectedPath(var6);
               } else if (var5 instanceof JMenuItem) {
                  JMenuItem var7 = (JMenuItem)var5;
                  if (var7.getUI() instanceof BasicMenuItemUI) {
                     ((BasicMenuItemUI)var7.getUI()).doClick(var3);
                  } else {
                     var3.clearSelectedPath();
                     var7.doClick(0);
                  }
               }
            }

         }
      }

      private void selectParentChild(boolean var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         MenuElement[] var3 = var2.getSelectedPath();
         int var4 = var3.length;
         MenuElement[] var7;
         if (!var1) {
            int var5 = var4 - 1;
            if (var4 > 2) {
               label43: {
                  if (!(var3[var5] instanceof JPopupMenu)) {
                     --var5;
                     if (!(var3[var5] instanceof JPopupMenu)) {
                        break label43;
                     }
                  }

                  if (!((JMenu)var3[var5 - 1]).isTopLevelMenu()) {
                     MenuElement[] var6 = new MenuElement[var5];
                     System.arraycopy(var3, 0, var6, 0, var5);
                     var2.setSelectedPath(var6);
                     return;
                  }
               }
            }
         } else if (var4 > 0 && var3[var4 - 1] instanceof JMenu && !((JMenu)var3[var4 - 1]).isTopLevelMenu()) {
            JMenu var11 = (JMenu)var3[var4 - 1];
            JPopupMenu var13 = var11.getPopupMenu();
            var7 = var13.getSubElements();
            MenuElement var8 = BasicPopupMenuUI.findEnabledChild(var7, -1, true);
            MenuElement[] var9;
            if (var8 == null) {
               var9 = new MenuElement[var4 + 1];
            } else {
               var9 = new MenuElement[var4 + 2];
               var9[var4 + 1] = var8;
            }

            System.arraycopy(var3, 0, var9, 0, var4);
            var9[var4] = var13;
            var2.setSelectedPath(var9);
            return;
         }

         if (var4 > 1 && var3[0] instanceof JMenuBar) {
            MenuElement var10 = var3[1];
            MenuElement var12 = BasicPopupMenuUI.findEnabledChild(var3[0].getSubElements(), var10, var1);
            if (var12 != null && var12 != var10) {
               if (var4 == 2) {
                  var7 = new MenuElement[]{var3[0], var12};
               } else {
                  var7 = new MenuElement[]{var3[0], var12, ((JMenu)var12).getPopupMenu()};
               }

               var2.setSelectedPath(var7);
            }
         }

      }

      private void selectItem(boolean var1) {
         MenuSelectionManager var2 = MenuSelectionManager.defaultManager();
         MenuElement[] var3 = var2.getSelectedPath();
         if (var3.length != 0) {
            int var4 = var3.length;
            JPopupMenu var10;
            if (var4 == 1 && var3[0] instanceof JPopupMenu) {
               var10 = (JPopupMenu)var3[0];
               MenuElement[] var13 = new MenuElement[]{var10, BasicPopupMenuUI.findEnabledChild(var10.getSubElements(), -1, var1)};
               var2.setSelectedPath(var13);
            } else {
               MenuElement var6;
               if (var4 == 2 && var3[0] instanceof JMenuBar && var3[1] instanceof JMenu) {
                  var10 = ((JMenu)var3[1]).getPopupMenu();
                  var6 = BasicPopupMenuUI.findEnabledChild(var10.getSubElements(), -1, true);
                  MenuElement[] var12;
                  if (var6 != null) {
                     var12 = new MenuElement[4];
                     var12[3] = var6;
                  } else {
                     var12 = new MenuElement[3];
                  }

                  System.arraycopy(var3, 0, var12, 0, 2);
                  var12[2] = var10;
                  var2.setSelectedPath(var12);
               } else if (var3[var4 - 1] instanceof JPopupMenu && var3[var4 - 2] instanceof JMenu) {
                  JMenu var9 = (JMenu)var3[var4 - 2];
                  JPopupMenu var11 = var9.getPopupMenu();
                  MenuElement var7 = BasicPopupMenuUI.findEnabledChild(var11.getSubElements(), -1, var1);
                  MenuElement[] var8;
                  if (var7 != null) {
                     var8 = new MenuElement[var4 + 1];
                     System.arraycopy(var3, 0, var8, 0, var4);
                     var8[var4] = var7;
                     var2.setSelectedPath(var8);
                  } else if (var4 > 2 && var3[var4 - 3] instanceof JPopupMenu) {
                     var11 = (JPopupMenu)var3[var4 - 3];
                     var7 = BasicPopupMenuUI.findEnabledChild(var11.getSubElements(), var9, var1);
                     if (var7 != null && var7 != var9) {
                        var8 = new MenuElement[var4 - 1];
                        System.arraycopy(var3, 0, var8, 0, var4 - 2);
                        var8[var4 - 2] = var7;
                        var2.setSelectedPath(var8);
                     }
                  }
               } else {
                  MenuElement[] var5 = var3[var4 - 2].getSubElements();
                  var6 = BasicPopupMenuUI.findEnabledChild(var5, var3[var4 - 1], var1);
                  if (var6 == null) {
                     var6 = BasicPopupMenuUI.findEnabledChild(var5, -1, var1);
                  }

                  if (var6 != null) {
                     var3[var4 - 1] = var6;
                     var2.setSelectedPath(var3);
                  }
               }
            }

         }
      }

      private void cancel() {
         JPopupMenu var1 = BasicPopupMenuUI.getLastPopup();
         if (var1 != null) {
            var1.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE);
         }

         String var2 = UIManager.getString("Menu.cancelMode");
         if ("hideMenuTree".equals(var2)) {
            MenuSelectionManager.defaultManager().clearSelectedPath();
         } else {
            this.shortenSelectedPath();
         }

      }

      private void shortenSelectedPath() {
         MenuElement[] var1 = MenuSelectionManager.defaultManager().getSelectedPath();
         if (var1.length <= 2) {
            MenuSelectionManager.defaultManager().clearSelectedPath();
         } else {
            int var2 = 2;
            MenuElement var3 = var1[var1.length - 1];
            JPopupMenu var4 = BasicPopupMenuUI.getLastPopup();
            if (var3 == var4) {
               MenuElement var5 = var1[var1.length - 2];
               if (var5 instanceof JMenu) {
                  JMenu var6 = (JMenu)var5;
                  if (var6.isEnabled() && var4.getComponentCount() > 0) {
                     var2 = 1;
                  } else {
                     var2 = 3;
                  }
               }
            }

            if (var1.length - var2 <= 2 && !UIManager.getBoolean("Menu.preserveTopLevelSelection")) {
               var2 = var1.length;
            }

            MenuElement[] var7 = new MenuElement[var1.length - var2];
            System.arraycopy(var1, 0, var7, 0, var1.length - var2);
            MenuSelectionManager.defaultManager().setSelectedPath(var7);
         }
      }
   }

   private class BasicMenuKeyListener implements MenuKeyListener {
      MenuElement menuToOpen;

      private BasicMenuKeyListener() {
         this.menuToOpen = null;
      }

      public void menuKeyTyped(MenuKeyEvent var1) {
         if (this.menuToOpen != null) {
            JPopupMenu var2 = ((JMenu)this.menuToOpen).getPopupMenu();
            MenuElement var3 = BasicPopupMenuUI.findEnabledChild(var2.getSubElements(), -1, true);
            ArrayList var4 = new ArrayList(Arrays.asList(var1.getPath()));
            var4.add(this.menuToOpen);
            var4.add(var2);
            if (var3 != null) {
               var4.add(var3);
            }

            MenuElement[] var5 = new MenuElement[0];
            var5 = (MenuElement[])var4.toArray(var5);
            MenuSelectionManager.defaultManager().setSelectedPath(var5);
            var1.consume();
         }

         this.menuToOpen = null;
      }

      public void menuKeyPressed(MenuKeyEvent var1) {
         char var2 = var1.getKeyChar();
         if (Character.isLetterOrDigit(var2)) {
            MenuSelectionManager var3 = var1.getMenuSelectionManager();
            MenuElement[] var4 = var1.getPath();
            MenuElement[] var5 = BasicPopupMenuUI.this.popupMenu.getSubElements();
            int var6 = -1;
            int var7 = 0;
            int var8 = -1;
            int[] var9 = null;

            for(int var10 = 0; var10 < var5.length; ++var10) {
               if (var5[var10] instanceof JMenuItem) {
                  JMenuItem var11 = (JMenuItem)var5[var10];
                  int var12 = var11.getMnemonic();
                  if (var11.isEnabled() && var11.isVisible() && this.lower(var2) == this.lower(var12)) {
                     if (var7 == 0) {
                        var8 = var10;
                        ++var7;
                     } else {
                        if (var9 == null) {
                           var9 = new int[var5.length];
                           var9[0] = var8;
                        }

                        var9[var7++] = var10;
                     }
                  }

                  if (var11.isArmed() || var11.isSelected()) {
                     var6 = var7 - 1;
                  }
               }
            }

            if (var7 != 0) {
               if (var7 == 1) {
                  JMenuItem var13 = (JMenuItem)var5[var8];
                  if (var13 instanceof JMenu) {
                     this.menuToOpen = var13;
                  } else if (var13.isEnabled()) {
                     var3.clearSelectedPath();
                     var13.doClick();
                  }

                  var1.consume();
               } else {
                  MenuElement var14 = var5[var9[(var6 + 1) % var7]];
                  MenuElement[] var15 = new MenuElement[var4.length + 1];
                  System.arraycopy(var4, 0, var15, 0, var4.length);
                  var15[var4.length] = var14;
                  var3.setSelectedPath(var15);
                  var1.consume();
               }
            }

         }
      }

      public void menuKeyReleased(MenuKeyEvent var1) {
      }

      private char lower(char var1) {
         return Character.toLowerCase(var1);
      }

      private char lower(int var1) {
         return Character.toLowerCase((char)var1);
      }

      // $FF: synthetic method
      BasicMenuKeyListener(Object var2) {
         this();
      }
   }

   private class BasicPopupMenuListener implements PopupMenuListener {
      private BasicPopupMenuListener() {
      }

      public void popupMenuCanceled(PopupMenuEvent var1) {
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent var1) {
      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent var1) {
         BasicLookAndFeel.playSound((JPopupMenu)var1.getSource(), "PopupMenu.popupSound");
      }

      // $FF: synthetic method
      BasicPopupMenuListener(Object var2) {
         this();
      }
   }
}
