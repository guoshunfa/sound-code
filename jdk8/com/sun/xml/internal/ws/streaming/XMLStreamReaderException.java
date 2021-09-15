package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamReaderException extends JAXWSExceptionBase {
   public XMLStreamReaderException(String key, Object... args) {
      super(key, args);
   }

   public XMLStreamReaderException(Throwable throwable) {
      super(throwable);
   }

   public XMLStreamReaderException(Localizable arg) {
      super("xmlreader.nestedError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.streaming";
   }
}
