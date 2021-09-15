package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianValidationErrorHandler extends ValidationErrorHandler {
   public void warning(SAXParseException e) throws SAXException {
   }

   public void error(SAXParseException e) throws SAXException {
      throw e;
   }

   public void fatalError(SAXParseException e) throws SAXException {
      throw e;
   }
}
