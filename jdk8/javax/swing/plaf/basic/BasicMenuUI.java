package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuUI extends BasicMenuItemUI {
   protected ChangeListener changeListener;
   protected MenuListener menuListener;
   private int lastMnemonic = 0;
   private InputMap selectedWindowInputMap;
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;
   private static boolean crossMenuMnemonic = true;

   public static ComponentUI createUI(JComponent var0) {
      return new BasicMenuUI();
   }

   static void loadActionMap(LazyActionMap var0) {
      BasicMenuItemUI.loadActionMap(var0);
      var0.put(new BasicMenuUI.Actions("selectMenu", (JMenu)null, true));
   }

   protected void installDefaults() {
      super.installDefaults();
      this.updateDefaultBackgroundColor();
      ((JMenu)this.menuItem).setDelay(200);
      crossMenuMnemonic = UIManager.getBoolean("Menu.crossMenuMnemonic");
   }

   protected String getPropertyPrefix() {
      return "Menu";
   }

   protected void installListeners() {
      super.installListeners();
      if (this.changeListener == null) {
         this.changeListener = this.createChangeListener(this.menuItem);
      }

      if (this.changeListener != null) {
         this.menuItem.addChangeListener(this.changeListener);
      }

      if (this.menuListener == null) {
         this.menuListener = this.createMenuListener(this.menuItem);
      }

      if (this.menuListener != null) {
         ((JMenu)this.menuItem).addMenuListener(this.menuListener);
      }

   }

   protected void installKeyboardActions() {
      super.installKeyboardActions();
      this.updateMnemonicBinding();
   }

   void installLazyActionMap() {
      LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuUI.class, this.getPropertyPrefix() + ".actionMap");
   }

   void updateMnemonicBinding() {
      int var1 = this.menuItem.getModel().getMnemonic();
      int[] var2 = (int[])((int[])DefaultLookup.get(this.menuItem, this, "Menu.shortcutKeys"));
      if (var2 == null) {
         var2 = new int[]{8};
      }

      if (var1 != this.lastMnemonic) {
         InputMap var3 = SwingUtilities.getUIInputMap(this.menuItem, 2);
         int[] var4;
         int var5;
         int var6;
         int var7;
         if (this.lastMnemonic != 0 && var3 != null) {
            var4 = var2;
            var5 = var2.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               var3.remove(KeyStroke.getKeyStroke(this.lastMnemonic, var7, false));
            }
         }

         if (var1 != 0) {
            if (var3 == null) {
               var3 = this.createInputMap(2);
               SwingUtilities.replaceUIInputMap(this.menuItem, 2, var3);
            }

            var4 = var2;
            var5 = var2.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               var3.put(KeyStroke.getKeyStroke(var1, var7, false), "selectMenu");
            }
         }

         this.lastMnemonic = var1;
      }
   }

   protected void uninstallKeyboardActions() {
      super.uninstallKeyboardActions();
      this.lastMnemonic = 0;
   }

   protected MouseInputListener createMouseInputListener(JComponent var1) {
      return this.getHandler();
   }

   protected MenuListener createMenuListener(JComponent var1) {
      return null;
   }

   protected ChangeListener createChangeListener(JComponent var1) {
      return null;
   }

   protected PropertyChangeListener createPropertyChangeListener(JComponent var1) {
      return this.getHandler();
   }

   BasicMenuItemUI.Handler getHandler() {
      if (this.handler == null) {
         this.handler = new BasicMenuUI.Handler();
      }

      return this.handler;
   }

   protected void uninstallDefaults() {
      this.menuItem.setArmed(false);
      this.menuItem.setSelected(false);
      this.menuItem.resetKeyboardActions();
      super.uninstallDefaults();
   }

   protected void uninstallListeners() {
      super.uninstallListeners();
      if (this.changeListener != null) {
         this.menuItem.removeChangeListener(this.changeListener);
      }

      if (this.menuListener != null) {
         ((JMenu)this.menuItem).removeMenuListener(this.menuListener);
      }

      this.changeListener = null;
      this.menuListener = null;
      this.handler = null;
   }

   protected MenuDragMouseListener createMenuDragMouseListener(JComponent var1) {
      return this.getHandler();
   }

   protected MenuKeyListener createMenuKeyListener(JComponent var1) {
      return (MenuKeyListener)this.getHandler();
   }

   public Dimension getMaximumSize(JComponent var1) {
      if (((JMenu)this.menuItem).isTopLevelMenu()) {
         Dimension var2 = var1.getPreferredSize();
         return new Dimension(var2.width, 32767);
      } else {
         return null;
      }
   }

   protected void setupPostTimer(JMenu var1) {
      Timer var2 = new Timer(var1.getDelay(), new BasicMenuUI.Actions("selectMenu", var1, false));
      var2.setRepeats(false);
      var2.start();
   }

   private static void appendPath(MenuElement[] var0, MenuElement var1) {
      MenuElement[] var2 = new MenuElement[var0.length + 1];
      System.arraycopy(var0, 0, var2, 0, var0.length);
      var2[var0.length] = var1;
      MenuSelectionManager.defaultManager().setSelectedPath(var2);
   }

   private void updateDefaultBackgroundColor() {
      if (UIManager.getBoolean("Menu.useMenuBarBackgroundForTopLevel")) {
         JMenu var1 = (JMenu)this.menuItem;
         if (var1.getBackground() instanceof UIResource) {
            if (var1.isTopLevelMenu()) {
               var1.setBackground(UIManager.getColor("MenuBar.background"));
            } else {
               var1.setBackground(UIManager.getColor(this.getPropertyPrefix() + ".background"));
            }
         }

      }
   }

   private class Handler extends BasicMenuItemUI.Handler implements MenuKeyListener {
      private Handler() {
         super();
      }

      public void propertyChange(PropertyChangeEvent var1) {
         if (var1.getPropertyName() == "mnemonic") {
            BasicMenuUI.this.updateMnemonicBinding();
         } else {
            if (var1.getPropertyName().equals("ancestor")) {
               BasicMenuUI.this.updateDefaultBackgroundColor();
            }

            super.propertyChange(var1);
         }

      }

      public void mouseClicked(MouseEvent var1) {
      }

      public void mousePressed(MouseEvent var1) {
         JMenu var2 = (JMenu)BasicMenuUI.this.menuItem;
         if (var2.isEnabled()) {
            MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
            if (var2.isTopLevelMenu()) {
               if (var2.isSelected() && var2.getPopupMenu().isShowing()) {
                  var3.clearSelectedPath();
               } else {
                  Container var4 = var2.getParent();
                  if (var4 != null && var4 instanceof JMenuBar) {
                     MenuElement[] var5 = new MenuElement[]{(MenuElement)var4, var2};
                     var3.setSelectedPath(var5);
                  }
               }
            }

            MenuElement[] var6 = var3.getSelectedPath();
            if (var6.length > 0 && var6[var6.length - 1] != var2.getPopupMenu()) {
               if (!var2.isTopLevelMenu() && var2.getDelay() != 0) {
                  BasicMenuUI.this.setupPostTimer(var2);
               } else {
                  BasicMenuUI.appendPath(var6, var2.getPopupMenu());
               }
            }

         }
      }

      public void mouseReleased(MouseEvent var1) {
         JMenu var2 = (JMenu)BasicMenuUI.this.menuItem;
         if (var2.isEnabled()) {
            MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
            var3.processMouseEvent(var1);
            if (!var1.isConsumed()) {
               var3.clearSelectedPath();
            }

         }
      }

      public void mouseEntered(MouseEvent var1) {
         JMenu var2 = (JMenu)BasicMenuUI.this.menuItem;
         if (var2.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) {
            MenuSelectionManager var3 = MenuSelectionManager.defaultManager();
            MenuElement[] var4 = var3.getSelectedPath();
            if (!var2.isTopLevelMenu()) {
               if (var4.length <= 0 || var4[var4.length - 1] != var2.getPopupMenu()) {
                  if (var2.getDelay() == 0) {
                     BasicMenuUI.appendPath(BasicMenuUI.this.getPath(), var2.getPopupMenu());
                  } else {
                     var3.setSelectedPath(BasicMenuUI.this.getPath());
                     BasicMenuUI.this.setupPostTimer(var2);
                  }
               }
            } else if (var4.length > 0 && var4[0] == var2.getParent()) {
               MenuElement[] var5 = new MenuElement[]{(MenuElement)var2.getParent(), var2, null};
               if (BasicPopupMenuUI.getLastPopup() != null) {
                  var5[2] = var2.getPopupMenu();
               }

               var3.setSelectedPath(var5);
            }

         }
      }

      public void mouseExited(MouseEvent var1) {
      }

      public void mouseDragged(MouseEvent var1) {
         JMenu var2 = (JMenu)BasicMenuUI.this.menuItem;
         if (var2.isEnabled()) {
            MenuSelectionManager.defaultManager().processMouseEvent(var1);
         }
      }

      public void mouseMoved(MouseEvent var1) {
      }

      public void menuDragMouseEntered(MenuDragMouseEvent var1) {
      }

      public void menuDragMouseDragged(MenuDragMouseEvent var1) {
         if (BasicMenuUI.this.menuItem.isEnabled()) {
            MenuSelectionManager var2 = var1.getMenuSelectionManager();
            MenuElement[] var3 = var1.getPath();
            Point var4 = var1.getPoint();
            if (var4.x >= 0 && var4.x < BasicMenuUI.this.menuItem.getWidth() && var4.y >= 0 && var4.y < BasicMenuUI.this.menuItem.getHeight()) {
               JMenu var7 = (JMenu)BasicMenuUI.this.menuItem;
               MenuElement[] var6 = var2.getSelectedPath();
               if (var6.length <= 0 || var6[var6.length - 1] != var7.getPopupMenu()) {
                  if (!var7.isTopLevelMenu() && var7.getDelay() != 0 && var1.getID() != 506) {
                     var2.setSelectedPath(var3);
                     BasicMenuUI.this.setupPostTimer(var7);
                  } else {
                     BasicMenuUI.appendPath(var3, var7.getPopupMenu());
                  }
               }
            } else if (var1.getID() == 502) {
               Component var5 = var2.componentForPoint(var1.getComponent(), var1.getPoint());
               if (var5 == null) {
                  var2.clearSelectedPath();
               }
            }

         }
      }

      public void menuDragMouseExited(MenuDragMouseEvent var1) {
      }

      public void menuDragMouseReleased(MenuDragMouseEvent var1) {
      }

      public void menuKeyTyped(MenuKeyEvent var1) {
         if (BasicMenuUI.crossMenuMnemonic || BasicPopupMenuUI.getLastPopup() == null) {
            if (BasicPopupMenuUI.getPopups().size() == 0) {
               char var2 = Character.toLowerCase((char)BasicMenuUI.this.menuItem.getMnemonic());
               MenuElement[] var3 = var1.getPath();
               if (var2 == Character.toLowerCase(var1.getKeyChar())) {
                  JPopupMenu var4 = ((JMenu)BasicMenuUI.this.menuItem).getPopupMenu();
                  ArrayList var5 = new ArrayList(Arrays.asList(var3));
                  var5.add(var4);
                  MenuElement[] var6 = var4.getSubElements();
                  MenuElement var7 = BasicPopupMenuUI.findEnabledChild(var6, -1, true);
                  if (var7 != null) {
                     var5.add(var7);
                  }

                  MenuSelectionManager var8 = var1.getMenuSelectionManager();
                  MenuElement[] var9 = new MenuElement[0];
                  var9 = (MenuElement[])((MenuElement[])var5.toArray(var9));
                  var8.setSelectedPath(var9);
                  var1.consume();
               }

            }
         }
      }

      public void menuKeyPressed(MenuKeyEvent var1) {
      }

      public void menuKeyReleased(MenuKeyEvent var1) {
      }

      // $FF: synthetic method
      Handler(Object var2) {
         this();
      }
   }

   public class ChangeHandler implements ChangeListener {
      public JMenu menu;
      public BasicMenuUI ui;
      public boolean isSelected = false;
      public Component wasFocused;

      public ChangeHandler(JMenu var2, BasicMenuUI var3) {
         this.menu = var2;
         this.ui = var3;
      }

      public void stateChanged(ChangeEvent var1) {
      }
   }

   protected class MouseInputHandler implements MouseInputListener {
      public void mouseClicked(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseClicked(var1);
      }

      public void mousePressed(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mousePressed(var1);
      }

      public void mouseReleased(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseReleased(var1);
      }

      public void mouseEntered(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseEntered(var1);
      }

      public void mouseExited(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseExited(var1);
      }

      public void mouseDragged(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseDragged(var1);
      }

      public void mouseMoved(MouseEvent var1) {
         BasicMenuUI.this.getHandler().mouseMoved(var1);
      }
   }

   private static class Actions extends UIAction {
      private static final String SELECT = "selectMenu";
      private JMenu menu;
      private boolean force = false;

      Actions(String var1, JMenu var2, boolean var3) {
         super(var1);
         this.menu = var2;
         this.force = var3;
      }

      private JMenu getMenu(ActionEvent var1) {
         return var1.getSource() instanceof JMenu ? (JMenu)var1.getSource() : this.menu;
      }

      public void actionPerformed(ActionEvent var1) {
         JMenu var2 = this.getMenu(var1);
         if (!BasicMenuUI.crossMenuMnemonic) {
            JPopupMenu var3 = BasicPopupMenuUI.getLastPopup();
            if (var3 != null && var3 != var2.getParent()) {
               return;
            }
         }

         MenuSelectionManager var7 = MenuSelectionManager.defaultManager();
         if (this.force) {
            Container var4 = var2.getParent();
            if (var4 != null && var4 instanceof JMenuBar) {
               MenuElement[] var6 = var2.getPopupMenu().getSubElements();
               MenuElement[] var5;
               if (var6.length > 0) {
                  var5 = new MenuElement[]{(MenuElement)var4, var2, var2.getPopupMenu(), var6[0]};
               } else {
                  var5 = new MenuElement[]{(MenuElement)var4, var2, var2.getPopupMenu()};
               }

               var7.setSelectedPath(var5);
            }
         } else {
            MenuElement[] var8 = var7.getSelectedPath();
            if (var8.length > 0 && var8[var8.length - 1] == var2) {
               BasicMenuUI.appendPath(var8, var2.getPopupMenu());
            }
         }

      }

      public boolean isEnabled(Object var1) {
         return var1 instanceof JMenu ? ((JMenu)var1).isEnabled() : true;
      }
   }
}
