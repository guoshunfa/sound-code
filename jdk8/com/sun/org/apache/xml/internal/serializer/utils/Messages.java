package com.sun.org.apache.xml.internal.serializer.utils;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import java.text.MessageFormat;
import java.util.ListResourceBundle;
import java.util.Locale;

public final class Messages {
   private final Locale m_locale = Locale.getDefault();
   private ListResourceBundle m_resourceBundle;
   private String m_resourceBundleName;

   Messages(String resourceBundle) {
      this.m_resourceBundleName = resourceBundle;
   }

   private Locale getLocale() {
      return this.m_locale;
   }

   public final String createMessage(String msgKey, Object[] args) {
      if (this.m_resourceBundle == null) {
         this.m_resourceBundle = SecuritySupport.getResourceBundle(this.m_resourceBundleName);
      }

      return this.m_resourceBundle != null ? this.createMsg(this.m_resourceBundle, msgKey, args) : "Could not load the resource bundles: " + this.m_resourceBundleName;
   }

   private final String createMsg(ListResourceBundle fResourceBundle, String msgKey, Object[] args) {
      String fmsg = null;
      boolean throwex = false;
      String msg = null;
      if (msgKey != null) {
         msg = fResourceBundle.getString(msgKey);
      } else {
         msgKey = "";
      }

      if (msg == null) {
         throwex = true;

         try {
            msg = MessageFormat.format("BAD_MSGKEY", msgKey, this.m_resourceBundleName);
         } catch (Exception var10) {
            msg = "The message key '" + msgKey + "' is not in the message class '" + this.m_resourceBundleName + "'";
         }
      } else if (args != null) {
         try {
            int n = args.length;

            for(int i = 0; i < n; ++i) {
               if (null == args[i]) {
                  args[i] = "";
               }
            }

            fmsg = MessageFormat.format(msg, args);
         } catch (Exception var11) {
            throwex = true;

            try {
               fmsg = MessageFormat.format("BAD_MSGFORMAT", msgKey, this.m_resourceBundleName);
               fmsg = fmsg + " " + msg;
            } catch (Exception var9) {
               fmsg = "The format of message '" + msgKey + "' in message class '" + this.m_resourceBundleName + "' failed.";
            }
         }
      } else {
         fmsg = msg;
      }

      if (throwex) {
         throw new RuntimeException(fmsg);
      } else {
         return fmsg;
      }
   }
}
