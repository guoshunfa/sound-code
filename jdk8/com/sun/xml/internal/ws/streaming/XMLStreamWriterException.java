package com.sun.xml.internal.ws.streaming;

import com.sun.istack.internal.localization.Localizable;
import com.sun.xml.internal.ws.util.exception.JAXWSExceptionBase;

public class XMLStreamWriterException extends JAXWSExceptionBase {
   public XMLStreamWriterException(String key, Object... args) {
      super(key, args);
   }

   public XMLStreamWriterException(Throwable throwable) {
      super(throwable);
   }

   public XMLStreamWriterException(Localizable arg) {
      super("xmlwriter.nestedError", arg);
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.streaming";
   }
}
