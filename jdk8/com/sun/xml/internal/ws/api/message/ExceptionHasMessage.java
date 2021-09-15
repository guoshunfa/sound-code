package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public abstract class ExceptionHasMessage extends JAXWSExceptionBase {
   public ExceptionHasMessage(String key, Object... args) {
      super(key, args);
   }

   public abstract Message getFaultMessage();
}
