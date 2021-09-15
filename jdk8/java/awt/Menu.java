package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class Menu extends MenuItem implements MenuContainer, Accessible {
   Vector<MenuComponent> items;
   boolean tearOff;
   boolean isHelpMenu;
   private static final String base = "menu";
   private static int nameCounter;
   private static final long serialVersionUID = -8809584163345499784L;
   private int menuSerializedDataVersion;

   public Menu() throws HeadlessException {
      this("", false);
   }

   public Menu(String var1) throws HeadlessException {
      this(var1, false);
   }

   public Menu(String var1, boolean var2) throws HeadlessException {
      super(var1);
      this.items = new Vector();
      this.menuSerializedDataVersion = 1;
      this.tearOff = var2;
   }

   String constructComponentName() {
      Class var1 = Menu.class;
      synchronized(Menu.class) {
         return "menu" + nameCounter++;
      }
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         if (this.peer == null) {
            this.peer = Toolkit.getDefaultToolkit().createMenu(this);
         }

         int var2 = this.getItemCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            MenuItem var4 = this.getItem(var3);
            var4.parent = this;
            var4.addNotify();
         }

      }
   }

   public void removeNotify() {
      synchronized(this.getTreeLock()) {
         int var2 = this.getItemCount();

         for(int var3 = 0; var3 < var2; ++var3) {
            this.getItem(var3).removeNotify();
         }

         super.removeNotify();
      }
   }

   public boolean isTearOff() {
      return this.tearOff;
   }

   public int getItemCount() {
      return this.countItems();
   }

   /** @deprecated */
   @Deprecated
   public int countItems() {
      return this.countItemsImpl();
   }

   final int countItemsImpl() {
      return this.items.size();
   }

   public MenuItem getItem(int var1) {
      return this.getItemImpl(var1);
   }

   final MenuItem getItemImpl(int var1) {
      return (MenuItem)this.items.elementAt(var1);
   }

   public MenuItem add(MenuItem var1) {
      synchronized(this.getTreeLock()) {
         if (var1.parent != null) {
            var1.parent.remove(var1);
         }

         this.items.addElement(var1);
         var1.parent = this;
         MenuPeer var3 = (MenuPeer)this.peer;
         if (var3 != null) {
            var1.addNotify();
            var3.addItem(var1);
         }

         return var1;
      }
   }

   public void add(String var1) {
      this.add(new MenuItem(var1));
   }

   public void insert(MenuItem var1, int var2) {
      synchronized(this.getTreeLock()) {
         if (var2 < 0) {
            throw new IllegalArgumentException("index less than zero.");
         } else {
            int var4 = this.getItemCount();
            Vector var5 = new Vector();

            int var6;
            for(var6 = var2; var6 < var4; ++var6) {
               var5.addElement(this.getItem(var2));
               this.remove(var2);
            }

            this.add(var1);

            for(var6 = 0; var6 < var5.size(); ++var6) {
               this.add((MenuItem)var5.elementAt(var6));
            }

         }
      }
   }

   public void insert(String var1, int var2) {
      this.insert(new MenuItem(var1), var2);
   }

   public void addSeparator() {
      this.add("-");
   }

   public void insertSeparator(int var1) {
      synchronized(this.getTreeLock()) {
         if (var1 < 0) {
            throw new IllegalArgumentException("index less than zero.");
         } else {
            int var3 = this.getItemCount();
            Vector var4 = new Vector();

            int var5;
            for(var5 = var1; var5 < var3; ++var5) {
               var4.addElement(this.getItem(var1));
               this.remove(var1);
            }

            this.addSeparator();

            for(var5 = 0; var5 < var4.size(); ++var5) {
               this.add((MenuItem)var4.elementAt(var5));
            }

         }
      }
   }

   public void remove(int var1) {
      synchronized(this.getTreeLock()) {
         MenuItem var3 = this.getItem(var1);
         this.items.removeElementAt(var1);
         MenuPeer var4 = (MenuPeer)this.peer;
         if (var4 != null) {
            var4.delItem(var1);
            var3.removeNotify();
            var3.parent = null;
         }

      }
   }

   public void remove(MenuComponent var1) {
      synchronized(this.getTreeLock()) {
         int var3 = this.items.indexOf(var1);
         if (var3 >= 0) {
            this.remove(var3);
         }

      }
   }

   public void removeAll() {
      synchronized(this.getTreeLock()) {
         int var2 = this.getItemCount();

         for(int var3 = var2 - 1; var3 >= 0; --var3) {
            this.remove(var3);
         }

      }
   }

   boolean handleShortcut(KeyEvent var1) {
      int var2 = this.getItemCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         MenuItem var4 = this.getItem(var3);
         if (var4.handleShortcut(var1)) {
            return true;
         }
      }

      return false;
   }

   MenuItem getShortcutMenuItem(MenuShortcut var1) {
      int var2 = this.getItemCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         MenuItem var4 = this.getItem(var3).getShortcutMenuItem(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   synchronized Enumeration<MenuShortcut> shortcuts() {
      Vector var1 = new Vector();
      int var2 = this.getItemCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         MenuItem var4 = this.getItem(var3);
         if (var4 instanceof Menu) {
            Enumeration var6 = ((Menu)var4).shortcuts();

            while(var6.hasMoreElements()) {
               var1.addElement(var6.nextElement());
            }
         } else {
            MenuShortcut var5 = var4.getShortcut();
            if (var5 != null) {
               var1.addElement(var5);
            }
         }
      }

      return var1.elements();
   }

   void deleteShortcut(MenuShortcut var1) {
      int var2 = this.getItemCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.getItem(var3).deleteShortcut(var1);
      }

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException, HeadlessException {
      var1.defaultReadObject();

      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         MenuItem var3 = (MenuItem)this.items.elementAt(var2);
         var3.parent = this;
      }

   }

   public String paramString() {
      String var1 = ",tearOff=" + this.tearOff + ",isHelpMenu=" + this.isHelpMenu;
      return super.paramString() + var1;
   }

   private static native void initIDs();

   public AccessibleContext getAccessibleContext() {
      if (this.accessibleContext == null) {
         this.accessibleContext = new Menu.AccessibleAWTMenu();
      }

      return this.accessibleContext;
   }

   int getAccessibleChildIndex(MenuComponent var1) {
      return this.items.indexOf(var1);
   }

   static {
      Toolkit.loadLibraries();
      if (!GraphicsEnvironment.isHeadless()) {
         initIDs();
      }

      AWTAccessor.setMenuAccessor(new AWTAccessor.MenuAccessor() {
         public Vector<MenuComponent> getItems(Menu var1) {
            return var1.items;
         }
      });
      nameCounter = 0;
   }

   protected class AccessibleAWTMenu extends MenuItem.AccessibleAWTMenuItem {
      private static final long serialVersionUID = 5228160894980069094L;

      protected AccessibleAWTMenu() {
         super();
      }

      public AccessibleRole getAccessibleRole() {
         return AccessibleRole.MENU;
      }
   }
}
