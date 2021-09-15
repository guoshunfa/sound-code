package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DOMMessageFormatter {
   public static final String DOM_DOMAIN = "http://www.w3.org/dom/DOMTR";
   public static final String XML_DOMAIN = "http://www.w3.org/TR/1998/REC-xml-19980210";
   public static final String SERIALIZER_DOMAIN = "http://apache.org/xml/serializer";
   private static ResourceBundle domResourceBundle = null;
   private static ResourceBundle xmlResourceBundle = null;
   private static ResourceBundle serResourceBundle = null;
   private static Locale locale = null;

   DOMMessageFormatter() {
      locale = Locale.getDefault();
   }

   public static String formatMessage(String domain, String key, Object[] arguments) throws MissingResourceException {
      ResourceBundle resourceBundle = getResourceBundle(domain);
      if (resourceBundle == null) {
         init();
         resourceBundle = getResourceBundle(domain);
         if (resourceBundle == null) {
            throw new MissingResourceException("Unknown domain" + domain, (String)null, key);
         }
      }

      String msg;
      try {
         msg = key + ": " + resourceBundle.getString(key);
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

   static ResourceBundle getResourceBundle(String domain) {
      if (domain != "http://www.w3.org/dom/DOMTR" && !domain.equals("http://www.w3.org/dom/DOMTR")) {
         if (domain != "http://www.w3.org/TR/1998/REC-xml-19980210" && !domain.equals("http://www.w3.org/TR/1998/REC-xml-19980210")) {
            return domain != "http://apache.org/xml/serializer" && !domain.equals("http://apache.org/xml/serializer") ? null : serResourceBundle;
         } else {
            return xmlResourceBundle;
         }
      } else {
         return domResourceBundle;
      }
   }

   public static void init() {
      if (locale != null) {
         domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages", locale);
         serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages", locale);
         xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages", locale);
      } else {
         domResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.DOMMessages");
         serResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLSerializerMessages");
         xmlResourceBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.msg.XMLMessages");
      }

   }

   public static void setLocale(Locale dlocale) {
      locale = dlocale;
   }
}
