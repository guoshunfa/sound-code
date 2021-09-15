package com.sun.org.apache.xalan.internal.res;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.ListResourceBundle;

public class XSLMessages extends XPATHMessages {
   private static ListResourceBundle XSLTBundle = null;
   private static final String XSLT_ERROR_RESOURCES = "com.sun.org.apache.xalan.internal.res.XSLTErrorResources";

   public static String createMessage(String msgKey, Object[] args) {
      if (XSLTBundle == null) {
         XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
      }

      return XSLTBundle != null ? createMsg(XSLTBundle, msgKey, args) : "Could not load any resource bundles.";
   }

   public static String createWarning(String msgKey, Object[] args) {
      if (XSLTBundle == null) {
         XSLTBundle = SecuritySupport.getResourceBundle("com.sun.org.apache.xalan.internal.res.XSLTErrorResources");
      }

      return XSLTBundle != null ? createMsg(XSLTBundle, msgKey, args) : "Could not load any resource bundles.";
   }
}
