package com.sun.rowset.internal;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlErrorHandler extends DefaultHandler {
   public int errorCounter = 0;

   public void error(SAXParseException var1) throws SAXException {
      ++this.errorCounter;
   }

   public void fatalError(SAXParseException var1) throws SAXException {
      ++this.errorCounter;
   }

   public void warning(SAXParseException var1) throws SAXException {
   }
}
