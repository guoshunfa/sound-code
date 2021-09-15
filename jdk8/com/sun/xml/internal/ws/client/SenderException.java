package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class SenderException extends JAXWSExceptionBase {
   public SenderException(String key, Object... args) {
      super(key, args);
   }

   public SenderException(Throwable throwable) {
      super(throwable);
   }

   public SenderException(Localizable arg) {
      super("sender.nestedError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.sender";
   }
}
