package com.sun.xml.internal.bind.api;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public interface ErrorListener extends ErrorHandler {
   void error(SAXParseException var1);

   void fatalError(SAXParseException var1);

   void warning(SAXParseException var1);

   void info(SAXParseException var1);
}
