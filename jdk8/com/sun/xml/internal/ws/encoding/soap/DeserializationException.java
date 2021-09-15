package com.sun.xml.internal.ws.encoding.soap;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class DeserializationException extends JAXWSExceptionBase {
   public DeserializationException(String key, Object... args) {
      super(key, args);
   }

   public DeserializationException(Throwable throwable) {
      super(throwable);
   }

   public DeserializationException(Localizable arg) {
      super("nestedDeserializationError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.encoding";
   }
}
