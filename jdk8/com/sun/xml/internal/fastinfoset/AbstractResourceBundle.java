package com.sun.xml.internal.fastinfoset;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class AbstractResourceBundle extends ResourceBundle {
   public static final String LOCALE = "com.sun.xml.internal.fastinfoset.locale";

   public String getString(String key, Object[] args) {
      String pattern = this.getBundle().getString(key);
      return MessageFormat.format(pattern, args);
   }

   public static Locale parseLocale(String localeString) {
      Locale locale = null;
      if (localeString == null) {
         locale = Locale.getDefault();
      } else {
         try {
            String[] args = localeString.split("_");
            if (args.length == 1) {
               locale = new Locale(args[0]);
            } else if (args.length == 2) {
               locale = new Locale(args[0], args[1]);
            } else if (args.length == 3) {
               locale = new Locale(args[0], args[1], args[2]);
            }
         } catch (Throwable var3) {
            locale = Locale.getDefault();
         }
      }

      return locale;
   }

   public abstract ResourceBundle getBundle();

   protected Object handleGetObject(String key) {
      return this.getBundle().getObject(key);
   }

   public final Enumeration getKeys() {
      return this.getBundle().getKeys();
   }
}
