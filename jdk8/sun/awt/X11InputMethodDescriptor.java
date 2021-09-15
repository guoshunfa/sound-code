package sun.awt;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

public abstract class X11InputMethodDescriptor implements InputMethodDescriptor {
   private static Locale locale;

   public X11InputMethodDescriptor() {
      locale = getSupportedLocale();
   }

   public Locale[] getAvailableLocales() {
      Locale[] var1 = new Locale[]{locale};
      return var1;
   }

   public boolean hasDynamicLocaleList() {
      return false;
   }

   public synchronized String getInputMethodDisplayName(Locale var1, Locale var2) {
      String var3 = "System Input Methods";
      if (Locale.getDefault().equals(var2)) {
         var3 = Toolkit.getProperty("AWT.HostInputMethodDisplayName", var3);
      }

      return var3;
   }

   public Image getInputMethodIcon(Locale var1) {
      return null;
   }

   public abstract InputMethod createInputMethod() throws Exception;

   static Locale getSupportedLocale() {
      return SunToolkit.getStartupLocale();
   }
}
