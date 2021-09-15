package sun.lwawt.macosx;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.List;
import java.util.Locale;

public class CInputMethodDescriptor implements InputMethodDescriptor {
   public Locale[] getAvailableLocales() {
      Object[] var1 = getAvailableLocalesInternal();
      Locale[] var2 = new Locale[var1.length];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   static Object[] getAvailableLocalesInternal() {
      List var0 = nativeGetAvailableLocales();
      Locale var1 = CInputMethod.getNativeLocale();
      if (var0 != null && !var0.isEmpty()) {
         if (var1 != null && !var0.contains(var1)) {
            var0.add(var1);
         }

         return var0.toArray();
      } else {
         return new Object[]{var1 != null ? var1 : Locale.getDefault()};
      }
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

   public InputMethod createInputMethod() throws Exception {
      return new CInputMethod();
   }

   public String toString() {
      Locale[] var1 = this.getAvailableLocales();
      String var2 = null;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var2 == null) {
            var2 = var1[var3].toString();
         } else {
            var2 = var2 + "," + var1[var3];
         }
      }

      return this.getClass().getName() + "[locales=" + var2 + ",localelist=" + (this.hasDynamicLocaleList() ? "dynamic" : "static") + "]";
   }

   private static native void nativeInit();

   private static native List<Object> nativeGetAvailableLocales();

   static {
      nativeInit();
   }
}
