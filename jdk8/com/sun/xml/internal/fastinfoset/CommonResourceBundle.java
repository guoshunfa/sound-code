package com.sun.xml.internal.fastinfoset;

import java.util.Locale;
import java.util.ResourceBundle;

public class CommonResourceBundle extends AbstractResourceBundle {
   public static final String BASE_NAME = "com.sun.xml.internal.fastinfoset.resources.ResourceBundle";
   private static volatile CommonResourceBundle instance = null;
   private static Locale locale = null;
   private ResourceBundle bundle = null;

   protected CommonResourceBundle() {
      this.bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle");
   }

   protected CommonResourceBundle(Locale locale) {
      this.bundle = ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", locale);
   }

   public static CommonResourceBundle getInstance() {
      if (instance == null) {
         Class var0 = CommonResourceBundle.class;
         synchronized(CommonResourceBundle.class) {
            instance = new CommonResourceBundle();
            locale = parseLocale((String)null);
         }
      }

      return instance;
   }

   public static CommonResourceBundle getInstance(Locale locale) {
      Class var1;
      if (instance == null) {
         var1 = CommonResourceBundle.class;
         synchronized(CommonResourceBundle.class) {
            instance = new CommonResourceBundle(locale);
         }
      } else {
         var1 = CommonResourceBundle.class;
         synchronized(CommonResourceBundle.class) {
            if (CommonResourceBundle.locale != locale) {
               instance = new CommonResourceBundle(locale);
            }
         }
      }

      return instance;
   }

   public ResourceBundle getBundle() {
      return this.bundle;
   }

   public ResourceBundle getBundle(Locale locale) {
      return ResourceBundle.getBundle("com.sun.xml.internal.fastinfoset.resources.ResourceBundle", locale);
   }
}
