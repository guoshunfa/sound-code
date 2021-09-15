package sun.awt.im;

import java.awt.AWTException;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

final class InputMethodLocator {
   private InputMethodDescriptor descriptor;
   private ClassLoader loader;
   private Locale locale;

   InputMethodLocator(InputMethodDescriptor var1, ClassLoader var2, Locale var3) {
      if (var1 == null) {
         throw new NullPointerException("descriptor can't be null");
      } else {
         this.descriptor = var1;
         this.loader = var2;
         this.locale = var3;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         InputMethodLocator var2 = (InputMethodLocator)var1;
         if (!this.descriptor.getClass().equals(var2.descriptor.getClass())) {
            return false;
         } else if (this.loader == null && var2.loader != null || this.loader != null && !this.loader.equals(var2.loader)) {
            return false;
         } else {
            return (this.locale != null || var2.locale == null) && (this.locale == null || this.locale.equals(var2.locale));
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.descriptor.hashCode();
      if (this.loader != null) {
         var1 |= this.loader.hashCode() << 10;
      }

      if (this.locale != null) {
         var1 |= this.locale.hashCode() << 20;
      }

      return var1;
   }

   InputMethodDescriptor getDescriptor() {
      return this.descriptor;
   }

   ClassLoader getClassLoader() {
      return this.loader;
   }

   Locale getLocale() {
      return this.locale;
   }

   boolean isLocaleAvailable(Locale var1) {
      try {
         Locale[] var2 = this.descriptor.getAvailableLocales();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].equals(var1)) {
               return true;
            }
         }
      } catch (AWTException var4) {
      }

      return false;
   }

   InputMethodLocator deriveLocator(Locale var1) {
      return var1 == this.locale ? this : new InputMethodLocator(this.descriptor, this.loader, var1);
   }

   boolean sameInputMethod(InputMethodLocator var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!this.descriptor.getClass().equals(var1.descriptor.getClass())) {
         return false;
      } else {
         return (this.loader != null || var1.loader == null) && (this.loader == null || this.loader.equals(var1.loader));
      }
   }

   String getActionCommandString() {
      String var1 = this.descriptor.getClass().getName();
      return this.locale == null ? var1 : var1 + "\n" + this.locale.toString();
   }
}
