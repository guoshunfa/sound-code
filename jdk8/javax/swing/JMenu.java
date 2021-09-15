package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.PopupMenuUI;

public class JMenu extends JMenuItem implements Accessible, MenuElement {
   private static final String uiClassID = "MenuUI";
   private JPopupMenu popupMenu;
   private ChangeListener menuChangeListener;
   private MenuEvent menuEvent;
   private int delay;
   private Point customMenuLocation;
   private static final boolean TRACE = false;
   private static final boolean VERBOSE = false;
   private static final boolean DEBUG = false;
   protected JMenu.WinListener popupListener;

   public JMenu() {
      this("");
   }

   public JMenu(String var1) {
      super(var1);
      this.menuChangeListener = null;
      this.menuEvent = null;
      this.customMenuLocation = null;
   }

   public JMenu(Action var1) {
      this();
      this.setAction(var1);
   }

   public JMenu(String var1, boolean var2) {
      this(var1);
   }

   void initFocusability() {
   }

   public void updateUI() {
      this.setUI((MenuItemUI)UIManager.getUI(this));
      if (this.popupMenu != null) {
         this.popupMenu.setUI((PopupMenuUI)UIManager.getUI(this.popupMenu));
      }

   }

   public String getUIClassID() {
      return "MenuUI";
   }

   public void setModel(ButtonModel var1) {
      ButtonModel var2 = this.getModel();
      super.setModel(var1);
      if (var2 != null && this.menuChangeListener != null) {
         var2.removeChangeListener(this.menuChangeListener);
         this.menuChangeListener = null;
      }

      this.model = var1;
      if (var1 != null) {
         this.menuChangeListener = this.createMenuChangeListener();
         var1.addChangeListener(this.menuChangeListener);
      }

   }

   public boolean isSelected() {
      return this.getModel().isSelected();
   }

   public void setSelected(boolean var1) {
      ButtonModel var2 = this.getModel();
      boolean var3 = var2.isSelected();
      if (var1 != var2.isSelected()) {
         this.getModel().setSelected(var1);
      }

   }

   public boolean isPopupMenuVisible() {
      this.ensurePopupMenuCreated();
      return this.popupMenu.isVisible();
   }

   public void setPopupMenuVisible(boolean var1) {
      boolean var2 = this.isPopupMenuVisible();
      if (var1 != var2 && (this.isEnabled() || !var1)) {
         this.ensurePopupMenuCreated();
         if (var1 && this.isShowing()) {
            Point var3 = this.getCustomMenuLocation();
            if (var3 == null) {
               var3 = this.getPopupMenuOrigin();
            }

            this.getPopupMenu().show(this, var3.x, var3.y);
         } else {
            this.getPopupMenu().setVisible(false);
         }
      }

   }

   protected Point getPopupMenuOrigin() {
      JPopupMenu var3 = this.getPopupMenu();
      Dimension var4 = this.getSize();
      Dimension var5 = var3.getSize();
      if (var5.width == 0) {
         var5 = var3.getPreferredSize();
      }

      Point var6 = this.getLocationOnScreen();
      Toolkit var7 = Toolkit.getDefaultToolkit();
      GraphicsConfiguration var8 = this.getGraphicsConfiguration();
      Rectangle var9 = new Rectangle(var7.getScreenSize());
      GraphicsEnvironment var10 = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice[] var11 = var10.getScreenDevices();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         if (var11[var12].getType() == 0) {
            GraphicsConfiguration var13 = var11[var12].getDefaultConfiguration();
            if (var13.getBounds().contains(var6)) {
               var8 = var13;
               break;
            }
         }
      }

      if (var8 != null) {
         var9 = var8.getBounds();
         Insets var15 = var7.getScreenInsets(var8);
         var9.width -= Math.abs(var15.left + var15.right);
         var9.height -= Math.abs(var15.top + var15.bottom);
         var6.x -= Math.abs(var15.left);
         var6.y -= Math.abs(var15.top);
      }

      Container var16 = this.getParent();
      int var1;
      int var2;
      int var14;
      int var17;
      if (var16 instanceof JPopupMenu) {
         var17 = UIManager.getInt("Menu.submenuPopupOffsetX");
         var14 = UIManager.getInt("Menu.submenuPopupOffsetY");
         if (SwingUtilities.isLeftToRight(this)) {
            var1 = var4.width + var17;
            if (var6.x + var1 + var5.width >= var9.width + var9.x && var9.width - var4.width < 2 * (var6.x - var9.x)) {
               var1 = 0 - var17 - var5.width;
            }
         } else {
            var1 = 0 - var17 - var5.width;
            if (var6.x + var1 < var9.x && var9.width - var4.width > 2 * (var6.x - var9.x)) {
               var1 = var4.width + var17;
            }
         }

         var2 = var14;
         if (var6.y + var14 + var5.height >= var9.height + var9.y && var9.height - var4.height < 2 * (var6.y - var9.y)) {
            var2 = var4.height - var14 - var5.height;
         }
      } else {
         var17 = UIManager.getInt("Menu.menuPopupOffsetX");
         var14 = UIManager.getInt("Menu.menuPopupOffsetY");
         if (SwingUtilities.isLeftToRight(this)) {
            var1 = var17;
            if (var6.x + var17 + var5.width >= var9.width + var9.x && var9.width - var4.width < 2 * (var6.x - var9.x)) {
               var1 = var4.width - var17 - var5.width;
            }
         } else {
            var1 = var4.width - var17 - var5.width;
            if (var6.x + var1 < var9.x && var9.width - var4.width > 2 * (var6.x - var9.x)) {
               var1 = var17;
            }
         }

         var2 = var4.height + var14;
         if (var6.y + var2 + var5.height >= var9.height + var9.y && var9.height - var4.height < 2 * (var6.y - var9.y)) {
            var2 = 0 - var14 - var5.height;
         }
      }

      return new Point(var1, var2);
   }

   public int getDelay() {
      return this.delay;
   }

   public void setDelay(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Delay must be a positive integer");
      } else {
         this.delay = var1;
      }
   }

   private void ensurePopupMenuCreated() {
      if (this.popupMenu == null) {
         this.popupMenu = new JPopupMenu();
         this.popupMenu.setInvoker(this);
         this.popupListener = this.createWinListener(this.popupMenu);
      }

   }

   private Point getCustomMenuLocation() {
      return this.customMenuLocation;
   }

   public void setMenuLocation(int var1, int var2) {
      this.customMenuLocation = new Point(var1, var2);
      if (this.popupMenu != null) {
         this.popupMenu.setLocation(var1, var2);
      }

   }

   public JMenuItem add(JMenuItem var1) {
      this.ensurePopupMenuCreated();
      return this.popupMenu.add(var1);
   }

   public Component add(Component var1) {
      this.ensurePopupMenuCreated();
      this.popupMenu.add((Component)var1);
      return var1;
   }

   public Component add(Component var1, int var2) {
      this.ensurePopupMenuCreated();
      this.popupMenu.add(var1, var2);
      return var1;
   }

   public JMenuItem add(String var1) {
      return this.add(new JMenuItem(var1));
   }

   public JMenuItem add(Action var1) {
      JMenuItem var2 = this.createActionComponent(var1);
      var2.setAction(var1);
      this.add(var2);
      return var2;
   }

   protected JMenuItem createActionComponent(Action var1) {
      JMenuItem var2 = new JMenuItem() {
         protected PropertyChangeListener createActionPropertyChangeListener(Action var1) {
            PropertyChangeListener var2 = JMenu.this.createActionChangeListener(this);
            if (var2 == null) {
               var2 = super.createActionPropertyChangeListener(var1);
            }

            return var2;
         }
      };
      var2.setHorizontalTextPosition(11);
      var2.setVerticalTextPosition(0);
      return var2;
   }

   protected PropertyChangeListener createActionChangeListener(JMenuItem var1) {
      return var1.createActionPropertyChangeListener0(var1.getAction());
   }

   public void addSeparator() {
      this.ensurePopupMenuCreated();
      this.popupMenu.addSeparator();
   }

   public void insert(String var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         this.ensurePopupMenuCreated();
         this.popupMenu.insert((Component)(new JMenuItem(var1)), var2);
      }
   }

   public JMenuItem insert(JMenuItem var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         this.ensurePopupMenuCreated();
         this.popupMenu.insert((Component)var1, var2);
         return var1;
      }
   }

   public JMenuItem insert(Action var1, int var2) {
      if (var2 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         this.ensurePopupMenuCreated();
         JMenuItem var3 = new JMenuItem(var1);
         var3.setHorizontalTextPosition(11);
         var3.setVerticalTextPosition(0);
         this.popupMenu.insert((Component)var3, var2);
         return var3;
      }
   }

   public void insertSeparator(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         this.ensurePopupMenuCreated();
         this.popupMenu.insert((Component)(new JPopupMenu.Separator()), var1);
      }
   }

   public JMenuItem getItem(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else {
         Component var2 = this.getMenuComponent(var1);
         if (var2 instanceof JMenuItem) {
            JMenuItem var3 = (JMenuItem)var2;
            return var3;
         } else {
            return null;
         }
      }
   }

   public int getItemCount() {
      return this.getMenuComponentCount();
   }

   public boolean isTearOff() {
      throw new Error("boolean isTearOff() {} not yet implemented");
   }

   public void remove(JMenuItem var1) {
      if (this.popupMenu != null) {
         this.popupMenu.remove(var1);
      }

   }

   public void remove(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("index less than zero.");
      } else if (var1 > this.getItemCount()) {
         throw new IllegalArgumentException("index greater than the number of items.");
      } else {
         if (this.popupMenu != null) {
            this.popupMenu.remove(var1);
         }

      }
   }

   public void remove(Component var1) {
      if (this.popupMenu != null) {
         this.popupMenu.remove(var1);
      }

   }

   public void removeAll() {
      if (this.popupMenu != null) {
         this.popupMenu.removeAll();
      }

   }

   public int getMenuComponentCount() {
      int var1 = 0;
      if (this.popupMenu != null) {
         var1 = this.popupMenu.getComponentCount();
      }

      return var1;
   }

   public Component getMenuComponent(int var1) {
      return this.popupMenu != null ? this.popupMenu.getComponent(var1) : null;
   }

   public Component[] getMenuComponents() {
      return this.popupMenu != null ? this.popupMenu.getComponents() : new Component[0];
   }

   public boolean isTopLevelMenu() {
      return this.getParent() instanceof JMenuBar;
   }

   public boolean isMenuComponent(Component var1) {
      if (var1 == this) {
         return true;
      } else {
         if (var1 instanceof JPopupMenu) {
            JPopupMenu var2 = (JPopupMenu)var1;
            if (var2 == this.getPopupMenu()) {
               return true;
            }
         }

         int var7 = this.getMenuComponentCount();
         Component[] var3 = this.getMenuComponents();

         for(int var4 = 0; var4 < var7; ++var4) {
            Component var5 = var3[var4];
            if (var5 == var1) {
               return true;
            }

            if (var5 instanceof JMenu) {
               JMenu var6 = (JMenu)var5;
               if (var6.isMenuComponent(var1)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private Point translateToPopupMenu(Point var1) {
      return this.translateToPopupMenu(var1.x, var1.y);
   }

   private Point translateToPopupMenu(int var1, int var2) {
      int var3;
      int var4;
      if (this.getParent() instanceof JPopupMenu) {
         var3 = var1 - this.getSize().width;
         var4 = var2;
      } else {
         var3 = var1;
         var4 = var2 - this.getSize().height;
      }

      return new Point(var3, var4);
   }

   public JPopupMenu getPopupMenu() {
      this.ensurePopupMenuCreated();
      return this.popupMenu;
   }

   public void addMenuListener(MenuListener var1) {
      this.listenerList.add(MenuListener.class, var1);
   }

   public void removeMenuListener(MenuListener var1) {
      this.listenerList.remove(MenuListener.class, var1);
   }

   public MenuListener[] getMenuListeners() {
      return (MenuListener[])this.listenerList.getListeners(MenuListener.class);
   }

   protected void fireMenuSelected() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == MenuListener.class) {
            if (var1[var2 + 1] == null) {
               throw new Error(this.getText() + " has a NULL Listener!! " + var2);
            }

            if (this.menuEvent == null) {
               this.menuEvent = new MenuEvent(this);
            }

            ((MenuListener)var1[var2 + 1]).menuSelected(this.menuEvent);
         }
      }

   }

   protected void fireMenuDeselected() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == MenuListener.class) {
            if (var1[var2 + 1] == null) {
               throw new Error(this.getText() + " has a NULL Listener!! " + var2);
            }

            if (this.menuEvent == null) {
               this.menuEvent = new MenuEvent(this);
            }

            ((MenuListener)var1[var2 + 1]).menuDeselected(this.menuEvent);
         }
      }

   }

   protected void fireMenuCanceled() {
      Object[] var1 = this.listenerList.getListenerList();

      for(int var2 = var1.length - 2; var2 >= 0; var2 -= 2) {
         if (var1[var2] == MenuListener.class) {
            if (var1[var2 + 1] == null) {
               throw new Error(this.getText() + " has a NULL Listener!! " + var2);
            }

            if (this.menuEvent == null) {
               this.menuEvent = new MenuEvent(this);
            }

            ((MenuListener)var1[var2 + 1]).menuCanceled(this.menuEvent);
         }
      }

   }

   void configureAcceleratorFromAction(Action var1) {
   }

   private ChangeListener createMenuChangeListener() {
      return new JMenu.MenuChangeListener();
   }

   protected JMenu.WinListener createWinListener(JPopupMenu var1) {
      return new JMenu.WinListener(var1);
   }

   public void menuSelectionChanged(boolean var1) {
      this.setSelected(var1);
   }

   public MenuElement[] getSubElements() {
      if (this.popupMenu == null) {
         return new MenuElement[0];
      } else {
         MenuElement[] var1 = new MenuElement[]{this.popupMenu};
         return var1;
      }
   }

   public Component getComponent() {
      return this;
   }

   public void applyComponentOrientation(ComponentOrientation var1) {
      super.applyComponentOrientation(var1);
      if (this.popupMenu != null) {
         int var2 = this.getMenuComponentCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.getMenuComponent(var3).applyComponentOrientation(var1);
         }

         this.popupMenu.setComponentOrientation(var1);
      }

   }

   public void setComponentOrientation(ComponentOrientation var1) {
      super.setComponentOrientation(var1);
      if (this.popupMenu != null) {
         this.popupMenu.setComponentOrientation(var1);
      }

   }

   public void setAccelerator(KeyStroke var1) {
      throw new Error("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead.");
   }

   protected void processKeyEvent(KeyEvent var1) {
      MenuSelectionManager.defaultManager().processKeyEvent(var1);
      if (!var1.isConsumed()) {
         super.processKeyEvent(var1);
      }
   }

   public void doClick(int var1) {
      MenuElement[] var2 = this.buildMenuElementArray(this);
      MenuSelectionManager.defaultManager().setSelectedPath(var2);
   }

   private MenuElement[] buildMenuElementArray(JMenu var1) {
      Vector var2 = new Vector();
      Object var3 = var1.getPopupMenu();

      while(true) {
         while(!(var3 instanceof JPopupMenu)) {
            if (var3 instanceof JMenu) {
               JMenu var5 = (JMenu)var3;
               var2.insertElementAt(var5, 0);
               var3 = var5.getParent();
            } else if (var3 instanceof JMenuBar) {
               JMenuBar var6 = (JMenuBar)var3;
               var2.insertElementAt(var6, 0);
               MenuElement[] var7 = new MenuElement[var2.size()];
               var2.copyInto(var7);
               return var7;
            }
         }

         JPopupMenu var4 = (JPopupMenu)var3;
         var2.insertElementAt(var4, 0);
         var3 = var4.getInvoker();
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      if (this.getUIClassID().equals("MenuUI")) {
         byte var2 = JComponent.getWriteObjCounter(this);
         --var2;
         JComponent.setWriteObjCounter(this, var2);
         if (var2 == 0 && this.ui != null) {
            this.ui.installUI(this);
         }
      }

   }

   protected String paramString() {
      return super.paramString();
   }

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new JMenu.AccessibleJMenu();
      }

      return this.accessibleContext;
   }

   protected class AccessibleJMenu extends JMenuItem.AccessibleJMenuItem implements AccessibleSelection {
      protected AccessibleJMenu() {
         super();
      }

      public int getAccessibleChildrenCount() {
         Component[] var1 = JMenu.this.getMenuComponents();
         int var2 = 0;
         Component[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Component var6 = var3[var5];
            if (var6 instanceof Accessible) {
               ++var2;
            }
         }

         return var2;
      }

      public Accessible getAccessibleChild(int var1) {
         Component[] var2 = JMenu.this.getMenuComponents();
         int var3 = 0;
         Component[] var4 = var2;
         int var5 = var2.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Component var7 = var4[var6];
            if (var7 instanceof Accessible) {
               if (var3 == var1) {
                  if (var7 instanceof JComponent) {
                     AccessibleContext var8 = var7.getAccessibleContext();
                     var8.setAccessibleParent(JMenu.this);
                  }

                  return (Accessible)var7;
               }

               ++var3;
            }
         }

         return null;
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU;
      }

      public AccessibleSelection getAccessibleSelection() {
         return this;
      }

      public int getAccessibleSelectionCount() {
         MenuElement[] var1 = MenuSelectionManager.defaultManager().getSelectedPath();
         if (var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
               if (var1[var2] == JMenu.this && var2 + 1 < var1.length) {
                  return 1;
               }
            }
         }

         return 0;
      }

      public Accessible getAccessibleSelection(int var1) {
         if (var1 >= 0 && var1 < JMenu.this.getItemCount()) {
            MenuElement[] var2 = MenuSelectionManager.defaultManager().getSelectedPath();
            if (var2 != null) {
               label27:
               for(int var3 = 0; var3 < var2.length; ++var3) {
                  if (var2[var3] == JMenu.this) {
                     do {
                        ++var3;
                        if (var3 >= var2.length) {
                           continue label27;
                        }
                     } while(!(var2[var3] instanceof JMenuItem));

                     return (Accessible)var2[var3];
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      }

      public boolean isAccessibleChildSelected(int var1) {
         MenuElement[] var2 = MenuSelectionManager.defaultManager().getSelectedPath();
         if (var2 != null) {
            JMenuItem var3 = JMenu.this.getItem(var1);

            for(int var4 = 0; var4 < var2.length; ++var4) {
               if (var2[var4] == var3) {
                  return true;
               }
            }
         }

         return false;
      }

      public void addAccessibleSelection(int var1) {
         if (var1 >= 0 && var1 < JMenu.this.getItemCount()) {
            JMenuItem var2 = JMenu.this.getItem(var1);
            if (var2 != null) {
               if (var2 instanceof JMenu) {
                  MenuElement[] var3 = JMenu.this.buildMenuElementArray((JMenu)var2);
                  MenuSelectionManager.defaultManager().setSelectedPath(var3);
               } else {
                  MenuSelectionManager.defaultManager().setSelectedPath((MenuElement[])null);
               }
            }

         }
      }

      public void removeAccessibleSelection(int var1) {
         if (var1 >= 0 && var1 < JMenu.this.getItemCount()) {
            JMenuItem var2 = JMenu.this.getItem(var1);
            if (var2 != null && var2 instanceof JMenu && var2.isSelected()) {
               MenuElement[] var3 = MenuSelectionManager.defaultManager().getSelectedPath();
               MenuElement[] var4 = new MenuElement[var3.length - 2];

               for(int var5 = 0; var5 < var3.length - 2; ++var5) {
                  var4[var5] = var3[var5];
               }

               MenuSelectionManager.defaultManager().setSelectedPath(var4);
            }

         }
      }

      public void clearAccessibleSelection() {
         MenuElement[] var1 = MenuSelectionManager.defaultManager().getSelectedPath();
         if (var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
               if (var1[var2] == JMenu.this) {
                  MenuElement[] var3 = new MenuElement[var2 + 1];
                  System.arraycopy(var1, 0, var3, 0, var2);
                  var3[var2] = JMenu.this.getPopupMenu();
                  MenuSelectionManager.defaultManager().setSelectedPath(var3);
               }
            }
         }

      }

      public void selectAllAccessibleSelection() {
      }
   }

   protected class WinListener extends WindowAdapter implements Serializable {
      JPopupMenu popupMenu;

      public WinListener(JPopupMenu var2) {
         this.popupMenu = var2;
      }

      public void windowClosing(WindowEvent var1) {
         JMenu.this.setSelected(false);
      }
   }

   class MenuChangeListener implements ChangeListener, Serializable {
      boolean isSelected = false;

      public void stateChanged(ChangeEvent var1) {
         ButtonModel var2 = (ButtonModel)var1.getSource();
         boolean var3 = var2.isSelected();
         if (var3 != this.isSelected) {
            if (var3) {
               JMenu.this.fireMenuSelected();
            } else {
               JMenu.this.fireMenuDeselected();
            }

            this.isSelected = var3;
         }

      }
   }
}
