package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianErrorHandler implements ErrorHandler {
   public static final ErrorHandler theInstance = new DraconianErrorHandler();

   private DraconianErrorHandler() {
   }

   public void error(SAXParseException e) throws SAXException {
      throw e;
   }

   public void fatalError(SAXParseException e) throws SAXException {
      throw e;
   }

   public void warning(SAXParseException e) throws SAXException {
   }
}
