package com.apple.laf;

import java.awt.Component;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import sun.lwawt.macosx.CMenuBar;

public class ScreenMenuBar extends MenuBar implements ContainerListener, ScreenMenuPropertyHandler, ComponentListener {
   static boolean sJMenuBarHasHelpMenus = false;
   JMenuBar fSwingBar;
   Hashtable<JMenu, ScreenMenu> fSubmenus;
   ScreenMenuPropertyListener fPropertyListener;
   ScreenMenuPropertyListener fAccessibleListener;
   private static Field[] stolenFields = null;

   public ScreenMenuBar(JMenuBar var1) {
      this.fSwingBar = var1;
      this.fSubmenus = new Hashtable(this.fSwingBar.getMenuCount());
   }

   public void addNotify() {
      super.addNotify();
      this.fSwingBar.addContainerListener(this);
      this.fPropertyListener = new ScreenMenuPropertyListener(this);
      this.fSwingBar.addPropertyChangeListener(this.fPropertyListener);
      this.fAccessibleListener = new ScreenMenuPropertyListener(this);
      this.fSwingBar.getAccessibleContext().addPropertyChangeListener(this.fAccessibleListener);
      int var1 = this.fSwingBar.getMenuCount();

      JMenu var3;
      for(int var2 = 0; var2 < var1; ++var2) {
         var3 = this.fSwingBar.getMenu(var2);
         if (var3 != null) {
            this.addSubmenu(var3);
         }
      }

      Enumeration var4 = this.fSubmenus.keys();

      while(var4.hasMoreElements()) {
         var3 = (JMenu)var4.nextElement();
         if (this.fSwingBar.getComponentIndex(var3) == -1) {
            this.removeSubmenu(var3);
         }
      }

   }

   public void removeNotify() {
      if (this.fSwingBar != null) {
         this.fSwingBar.removePropertyChangeListener(this.fPropertyListener);
         this.fSwingBar.getAccessibleContext().removePropertyChangeListener(this.fAccessibleListener);
         this.fSwingBar.removeContainerListener(this);
      }

      this.fPropertyListener = null;
      this.fAccessibleListener = null;
      if (this.fSubmenus != null) {
         Enumeration var1 = this.fSubmenus.keys();

         while(var1.hasMoreElements()) {
            JMenu var2 = (JMenu)var1.nextElement();
            var2.removeComponentListener(this);
         }
      }

      super.removeNotify();
   }

   public void componentAdded(ContainerEvent var1) {
      Component var2 = var1.getChild();
      if (var2 instanceof JMenu) {
         this.addSubmenu((JMenu)var2);
      }
   }

   public void componentRemoved(ContainerEvent var1) {
      Component var2 = var1.getChild();
      if (var2 instanceof JMenu) {
         this.removeSubmenu((JMenu)var2);
      }
   }

   public void componentResized(ComponentEvent var1) {
   }

   public void componentMoved(ComponentEvent var1) {
   }

   public void componentShown(ComponentEvent var1) {
      Object var2 = var1.getSource();
      if (var2 instanceof JMenuItem) {
         this.setChildVisible((JMenuItem)var2, true);
      }
   }

   public void componentHidden(ComponentEvent var1) {
      Object var2 = var1.getSource();
      if (var2 instanceof JMenuItem) {
         this.setChildVisible((JMenuItem)var2, false);
      }
   }

   public void setChildVisible(JMenuItem var1, boolean var2) {
      if (var1 instanceof JMenu) {
         if (var2) {
            this.addSubmenu((JMenu)var1);
         } else {
            ScreenMenu var3 = (ScreenMenu)this.fSubmenus.get(var1);
            if (var3 != null) {
               this.remove(var3);
            }
         }
      }

   }

   public void removeAll() {
      synchronized(this.getTreeLock()) {
         int var2 = this.getMenuCount();

         for(int var3 = var2 - 1; var3 >= 0; --var3) {
            this.remove(var3);
         }

      }
   }

   public void setIcon(Icon var1) {
   }

   public void setLabel(String var1) {
   }

   public void setEnabled(boolean var1) {
      int var2 = this.fSwingBar.getMenuCount();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.fSwingBar.getMenu(var3).setEnabled(var1);
      }

   }

   public void setAccelerator(KeyStroke var1) {
   }

   public void setToolTipText(String var1) {
   }

   public void setIndeterminate(boolean var1) {
   }

   ScreenMenu addSubmenu(JMenu var1) {
      ScreenMenu var2 = (ScreenMenu)this.fSubmenus.get(var1);
      if (var2 == null) {
         var2 = new ScreenMenu(var1);
         var1.addComponentListener(this);
         this.fSubmenus.put(var1, var2);
      }

      var2.setEnabled(var1.isEnabled());
      if (var1.isVisible() && var2.getParent() == null) {
         int var3 = 0;
         int var4 = 0;
         JMenu var5 = null;
         int var6 = this.fSwingBar.getMenuCount();

         for(int var7 = 0; var7 < var6; ++var7) {
            var5 = this.fSwingBar.getMenu(var7);
            if (var5 == var1) {
               var3 = var4;
               break;
            }

            if (var5 != null && var5.isVisible()) {
               ++var4;
            }
         }

         this.add(var2, var3);
      }

      return var2;
   }

   private void removeSubmenu(JMenu var1) {
      ScreenMenu var2 = (ScreenMenu)this.fSubmenus.get(var1);
      if (var2 != null) {
         var1.removeComponentListener(this);
         this.remove(var2);
         this.fSubmenus.remove(var1);
      }
   }

   public Menu add(Menu var1, int var2) {
      synchronized(this.getTreeLock()) {
         if (var1.getParent() != null) {
            var1.getParent().remove(var1);
         }

         Menu var10000;
         try {
            if (stolenFields == null) {
               var10000 = var1;
               return var10000;
            }

            Vector var4 = (Vector)stolenFields[0].get(this);
            var4.insertElementAt(var1, var2);
            stolenFields[1].set(var1, this);
            CMenuBar var5 = (CMenuBar)this.getPeer();
            if (var5 != null) {
               var5.setNextInsertionIndex(var2);
               if (var1.getPeer() == null) {
                  var1.addNotify();
               }

               var5.setNextInsertionIndex(-1);
               return var1;
            }

            var10000 = var1;
         } catch (IllegalAccessException var7) {
            var7.printStackTrace(System.err);
            return var1;
         }

         return var10000;
      }
   }

   static {
      stolenFields = (Field[])AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
         public Field[] run() {
            try {
               Field[] var1 = new Field[]{MenuBar.class.getDeclaredField("menus"), MenuComponent.class.getDeclaredField("parent")};
               AccessibleObject.setAccessible(var1, true);
               return var1;
            } catch (NoSuchFieldException var2) {
               var2.printStackTrace(System.err);
               return null;
            }
         }
      });
   }
}
