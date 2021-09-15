package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.Locale;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler extends DefaultHandler {
   private static int ERROR_COUNT_LIMIT = 10;
   private int errorCount = 0;
   private Locale locale = Locale.getDefault();

   public DefaultValidationErrorHandler(Locale locale) {
      this.locale = locale;
   }

   public void error(SAXParseException e) throws SAXException {
      if (this.errorCount < ERROR_COUNT_LIMIT) {
         if (this.errorCount == 0) {
            System.err.println(SAXMessageFormatter.formatMessage(this.locale, "errorHandlerNotSet", new Object[]{this.errorCount}));
         }

         String systemId = e.getSystemId();
         if (systemId == null) {
            systemId = "null";
         }

         String message = "Error: URI=" + systemId + " Line=" + e.getLineNumber() + ": " + e.getMessage();
         System.err.println(message);
         ++this.errorCount;
      }
   }
}
