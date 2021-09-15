package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;

public class WrappedSAXException extends RuntimeException {
   public final SAXException exception;

   WrappedSAXException(SAXException e) {
      this.exception = e;
   }
}
