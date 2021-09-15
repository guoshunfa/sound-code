package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class RuntimeModelerException extends JAXWSExceptionBase {
   public RuntimeModelerException(String key, Object... args) {
      super(key, args);
   }

   public RuntimeModelerException(Throwable throwable) {
      super(throwable);
   }

   public RuntimeModelerException(Localizable arg) {
      super("nestedModelerError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.modeler";
   }
}
