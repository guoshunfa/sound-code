package com.sun.org.apache.xerces.internal.xpointer;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class XPointerMessageFormatter implements MessageFormatter {
   public static final String XPOINTER_DOMAIN = "http://www.w3.org/TR/XPTR";
   private Locale fLocale = null;
   private ResourceBundle fResourceBundle = null;

   public String formatMessage(Locale locale, String key, Object[] arguments) throws MissingResourceException {
      if (this.fResourceBundle == null || locale != this.fLocale) {
         if (locale != null) {
            this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", locale);
            this.fLocale = locale;
         }

         if (this.fResourceBundle == null) {
            this.fResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages");
         }
      }

      String msg = this.fResourceBundle.getString(key);
      if (arguments != null) {
         try {
            msg = MessageFormat.format(msg, arguments);
         } catch (Exception var6) {
            msg = this.fResourceBundle.getString("FormatFailed");
            msg = msg + " " + this.fResourceBundle.getString(key);
         }
      }

      if (msg == null) {
         msg = this.fResourceBundle.getString("BadMessageKey");
         throw new MissingResourceException(msg, "com.sun.org.apache.xerces.internal.impl.msg.XPointerMessages", key);
      } else {
         return msg;
      }
   }
}
