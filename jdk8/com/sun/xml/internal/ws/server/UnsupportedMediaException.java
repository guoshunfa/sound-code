package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;
import java.util.List;

public final class UnsupportedMediaException extends JAXWSExceptionBase {
   public UnsupportedMediaException(@NotNull String contentType, List<String> expectedContentTypes) {
      super(ServerMessages.localizableUNSUPPORTED_CONTENT_TYPE(contentType, expectedContentTypes));
   }

   public UnsupportedMediaException() {
      super(ServerMessages.localizableNO_CONTENT_TYPE());
   }

   public UnsupportedMediaException(String charset) {
      super(ServerMessages.localizableUNSUPPORTED_CHARSET(charset));
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.server";
   }
}
