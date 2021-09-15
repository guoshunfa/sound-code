package sun.awt.im;

import java.awt.CheckboxMenuItem;
import java.awt.Component;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

class AWTInputMethodPopupMenu extends InputMethodPopupMenu {
   static PopupMenu delegate = null;

   AWTInputMethodPopupMenu(String var1) {
      synchronized(this) {
         if (delegate == null) {
            delegate = new PopupMenu(var1);
         }

      }
   }

   void show(Component var1, int var2, int var3) {
      delegate.show(var1, var2, var3);
   }

   void removeAll() {
      delegate.removeAll();
   }

   void addSeparator() {
      delegate.addSeparator();
   }

   void addToComponent(Component var1) {
      var1.add(delegate);
   }

   Object createSubmenu(String var1) {
      return new Menu(var1);
   }

   void add(Object var1) {
      delegate.add((MenuItem)var1);
   }

   void addMenuItem(String var1, String var2, String var3) {
      this.addMenuItem(delegate, var1, var2, var3);
   }

   void addMenuItem(Object var1, String var2, String var3, String var4) {
      Object var5;
      if (isSelected(var3, var4)) {
         var5 = new CheckboxMenuItem(var2, true);
      } else {
         var5 = new MenuItem(var2);
      }

      ((MenuItem)var5).setActionCommand(var3);
      ((MenuItem)var5).addActionListener(this);
      ((MenuItem)var5).setEnabled(var3 != null);
      ((Menu)var1).add((MenuItem)var5);
   }
}
