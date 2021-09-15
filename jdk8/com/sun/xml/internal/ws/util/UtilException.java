package com.sun.xml.internal.ws.util;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class UtilException extends JAXWSExceptionBase {
   public UtilException(String key, Object... args) {
      super(key, args);
   }

   public UtilException(Throwable throwable) {
      super(throwable);
   }

   public UtilException(Localizable arg) {
      super("nestedUtilError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.util";
   }
}
