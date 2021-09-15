package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class JAXPValidationMessageFormatter {
   public static String formatMessage(Locale locale, String key, Object[] arguments) throws MissingResourceException {
      ResourceBundle resourceBundle = null;
      if (locale != null) {
         resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages", locale);
      } else {
         resourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.JAXPValidationMessages");
      }

      String msg;
      try {
         msg = resourceBundle.getString(key);
         if (arguments != null) {
            try {
               msg = MessageFormat.format(msg, arguments);
            } catch (Exception var7) {
               msg = resourceBundle.getString("FormatFailed");
               msg = msg + " " + resourceBundle.getString(key);
            }
         }
      } catch (MissingResourceException var8) {
         msg = resourceBundle.getString("BadMessageKey");
         throw new MissingResourceException(key, msg, key);
      }

      if (msg == null) {
         msg = key;
         if (arguments.length > 0) {
            StringBuffer str = new StringBuffer(key);
            str.append('?');

            for(int i = 0; i < arguments.length; ++i) {
               if (i > 0) {
                  str.append('&');
               }

               str.append(String.valueOf(arguments[i]));
            }
         }
      }

      return msg;
   }
}
