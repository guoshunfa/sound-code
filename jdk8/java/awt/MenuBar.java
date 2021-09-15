package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuBarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class MenuBar extends MenuComponent implements MenuContainer, Accessible {
   Vector<Menu> menus = new Vector();
   Menu helpMenu;
   private static final String base = "menubar";
   private static int nameCounter;
   private static final long serialVersionUID = -4930327919388951260L;
   private int menuBarSerializedDataVersion = 1;

   public MenuBar() throws HeadlessException {
   }

   String constructComponentName() {
      Class var1 = MenuBar.class;
      synchronized(MenuBar.class) {
         return "menubar" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = Toolkit.getDefaultToolkit().createMenuBar(this);
         }

         int var2 = this.getMenuCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.getMenu(var3).addNotify();
         }

      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         int var2 = this.getMenuCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.getMenu(var3).removeNotify();
         }

         super.removeNotify();
      }
   }

   public Menu getHelpMenu() {
      return this.helpMenu;
   }

   public void setHelpMenu(Menu var1) {
      synchronized(this.getTreeLock()) {
         if (this.helpMenu != var1) {
            if (this.helpMenu != null) {
               this.remove(this.helpMenu);
            }

            this.helpMenu = var1;
            if (var1 != null) {
               if (var1.parent != this) {
                  this.add(var1);
               }

               var1.isHelpMenu = true;
               var1.parent = this;
               MenuBarPeer var3 = (MenuBarPeer)this.peer;
               if (var3 != null) {
                  if (var1.peer == null) {
                     var1.addNotify();
                  }

                  var3.addHelpMenu(var1);
               }
            }

         }
      }
   }

   public Menu add(Menu var1) {
      synchronized(this.getTreeLock()) {
         if (var1.parent != null) {
            var1.parent.remove(var1);
         }

         var1.parent = this;
         MenuBarPeer var3 = (MenuBarPeer)this.peer;
         if (var3 != null) {
            if (var1.peer == null) {
               var1.addNotify();
            }

            this.menus.addElement(var1);
            var3.addMenu(var1);
         } else {
            this.menus.addElement(var1);
         }

         return var1;
      }
   }

   public void remove(int var1) {
      synchronized(this.getTreeLock()) {
         Menu var3 = this.getMenu(var1);
         this.menus.removeElementAt(var1);
         MenuBarPeer var4 = (MenuBarPeer)this.peer;
         if (var4 != null) {
            var4.delMenu(var1);
            var3.removeNotify();
            var3.parent = null;
         }

         if (this.helpMenu == var3) {
            this.helpMenu = null;
            var3.isHelpMenu = false;
         }

      }
   }

   public void remove(MenuComponent var1) {
      synchronized(this.getTreeLock()) {
         int var3 = this.menus.indexOf(var1);
         if (var3 >= 0) {
            this.remove(var3);
         }

      }
   }

   public int getMenuCount() {
      return this.countMenus();
   }

   /** @deprecated */
   @Deprecated
   public int countMenus() {
      return this.getMenuCountImpl();
   }

   final int getMenuCountImpl() {
      return this.menus.size();
   }

   public Menu getMenu(int var1) {
      return this.getMenuImpl(var1);
   }

   final Menu getMenuImpl(int var1) {
      return (Menu)this.menus.elementAt(var1);
   }

   public synchronized Enumeration<MenuShortcut> shortcuts() {
      Vector var1 = new Vector();
      int var2 = this.getMenuCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         Enumeration var4 = this.getMenu(var3).shortcuts();

         while(var4.hasMoreElements()) {
            var1.addElement(var4.nextElement());
         }
      }

      return var1.elements();
   }

   public MenuItem getShortcutMenuItem(MenuShortcut var1) {
      int var2 = this.getMenuCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         MenuItem var4 = this.getMenu(var3).getShortcutMenuItem(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   boolean handleShortcut(KeyEvent var1) {
      int var2 = var1.getID();
      if (var2 != 401 && var2 != 402) {
         return false;
      } else {
         int var3 = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
         if ((var1.getModifiers() & var3) == 0) {
            return false;
         } else {
            int var4 = this.getMenuCount();

            for(int var5 = 0; var5 < var4; ++var5) {
               Menu var6 = this.getMenu(var5);
               if (var6.handleShortcut(var1)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public void deleteShortcut(MenuShortcut var1) {
      int var2 = this.getMenuCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.getMenu(var3).deleteShortcut(var1);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws ClassNotFoundException, IOException {
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws ClassNotFoundException, IOException, HeadlessException {
      var1.defaultReadObject();

      for(int var2 = 0; var2 < this.menus.size(); ++var2) {
         Menu var3 = (Menu)this.menus.elementAt(var2);
         var3.parent = this;
      }

   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new MenuBar.AccessibleAWTMenuBar();
      }

      return this.accessibleContext;
   }

   int getAccessibleChildIndex(MenuComponent var1) {
      return this.menus.indexOf(var1);
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setMenuBarAccessor(new AWTAccessor.MenuBarAccessor() {
         public Menu getHelpMenu(MenuBar var1) {
            return var1.helpMenu;
         }

         public Vector<Menu> getMenus(MenuBar var1) {
            return var1.menus;
         }
      });
      nameCounter = 0;
   }

   protected class AccessibleAWTMenuBar extends MenuComponent.AccessibleAWTMenuComponent {
      private static final long serialVersionUID = -8577604491830083815L;

      protected AccessibleAWTMenuBar() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU_BAR;
      }
   }
}
