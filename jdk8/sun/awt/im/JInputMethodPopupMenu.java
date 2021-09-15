package sun.awt.im;

import java.awt.Component;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class JInputMethodPopupMenu extends InputMethodPopupMenu {
   static JPopupMenu delegate = null;

   JInputMethodPopupMenu(String var1) {
      synchronized(this) {
         if (delegate == null) {
            delegate = new JPopupMenu(var1);
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
   }

   Object createSubmenu(String var1) {
      return new JMenu(var1);
   }

   void add(Object var1) {
      delegate.add((JMenuItem)var1);
   }

   void addMenuItem(String var1, String var2, String var3) {
      this.addMenuItem(delegate, var1, var2, var3);
   }

   void addMenuItem(Object var1, String var2, String var3, String var4) {
      Object var5;
      if (isSelected(var3, var4)) {
         var5 = new JCheckBoxMenuItem(var2, true);
      } else {
         var5 = new JMenuItem(var2);
      }

      ((JMenuItem)var5).setActionCommand(var3);
      ((JMenuItem)var5).addActionListener(this);
      ((JMenuItem)var5).setEnabled(var3 != null);
      if (var1 instanceof JMenu) {
         ((JMenu)var1).add((JMenuItem)var5);
      } else {
         ((JPopupMenu)var1).add((JMenuItem)var5);
      }

   }
}
