package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLReaderException extends JAXWSExceptionBase {
   public XMLReaderException(String key, Object... args) {
      super(key, args);
   }

   public XMLReaderException(Throwable throwable) {
      super(throwable);
   }

   public XMLReaderException(Localizable arg) {
      super("xmlreader.nestedError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.streaming";
   }
}
