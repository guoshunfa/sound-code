package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class HandlerException extends JAXWSExceptionBase {
   public HandlerException(String key, Object... args) {
      super(key, args);
   }

   public HandlerException(Throwable throwable) {
      super(throwable);
   }

   public HandlerException(Localizable arg) {
      super("handler.nestedError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.handler";
   }
}
