package sun.awt.im;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;
import javax.swing.JDialog;
import javax.swing.JFrame;

abstract class InputMethodPopupMenu implements ActionListener {
   static InputMethodPopupMenu getInstance(Component var0, String var1) {
      return (InputMethodPopupMenu)(!(var0 instanceof JFrame) && !(var0 instanceof JDialog) ? new AWTInputMethodPopupMenu(var1) : new JInputMethodPopupMenu(var1));
   }

   abstract void show(Component var1, int var2, int var3);

   abstract void removeAll();

   abstract void addSeparator();

   abstract void addToComponent(Component var1);

   abstract Object createSubmenu(String var1);

   abstract void add(Object var1);

   abstract void addMenuItem(String var1, String var2, String var3);

   abstract void addMenuItem(Object var1, String var2, String var3, String var4);

   void addOneInputMethodToMenu(InputMethodLocator var1, String var2) {
      InputMethodDescriptor var3 = var1.getDescriptor();
      String var4 = var3.getInputMethodDisplayName((Locale)null, Locale.getDefault());
      String var5 = var1.getActionCommandString();
      Locale[] var6 = null;

      int var7;
      try {
         var6 = var3.getAvailableLocales();
         var7 = var6.length;
      } catch (AWTException var13) {
         var7 = 0;
      }

      if (var7 == 0) {
         this.addMenuItem(var4, (String)null, var2);
      } else if (var7 == 1) {
         if (var3.hasDynamicLocaleList()) {
            var4 = var3.getInputMethodDisplayName(var6[0], Locale.getDefault());
            var5 = var1.deriveLocator(var6[0]).getActionCommandString();
         }

         this.addMenuItem(var4, var5, var2);
      } else {
         Object var8 = this.createSubmenu(var4);
         this.add(var8);

         for(int var9 = 0; var9 < var7; ++var9) {
            Locale var10 = var6[var9];
            String var11 = this.getLocaleName(var10);
            String var12 = var1.deriveLocator(var10).getActionCommandString();
            this.addMenuItem(var8, var11, var12, var2);
         }
      }

   }

   static boolean isSelected(String var0, String var1) {
      if (var0 != null && var1 != null) {
         if (var0.equals(var1)) {
            return true;
         } else {
            int var2 = var1.indexOf(10);
            return var2 != -1 && var1.substring(0, var2).equals(var0);
         }
      } else {
         return false;
      }
   }

   String getLocaleName(Locale var1) {
      String var2 = var1.toString();
      String var3 = Toolkit.getProperty("AWT.InputMethodLanguage." + var2, (String)null);
      if (var3 == null) {
         var3 = var1.getDisplayName();
         if (var3 == null || var3.length() == 0) {
            var3 = var2;
         }
      }

      return var3;
   }

   public void actionPerformed(ActionEvent var1) {
      String var2 = var1.getActionCommand();
      ((ExecutableInputMethodManager)InputMethodManager.getInstance()).changeInputMethod(var2);
   }
}
