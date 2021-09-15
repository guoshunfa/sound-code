package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class ErrorHandlerAdaptor implements XMLErrorHandler {
   private boolean hadError = false;

   public boolean hadError() {
      return this.hadError;
   }

   public void reset() {
      this.hadError = false;
   }

   protected abstract ErrorHandler getErrorHandler();

   public void fatalError(String domain, String key, XMLParseException e) {
      try {
         this.hadError = true;
         this.getErrorHandler().fatalError(Util.toSAXParseException(e));
      } catch (SAXException var5) {
         throw new WrappedSAXException(var5);
      }
   }

   public void error(String domain, String key, XMLParseException e) {
      try {
         this.hadError = true;
         this.getErrorHandler().error(Util.toSAXParseException(e));
      } catch (SAXException var5) {
         throw new WrappedSAXException(var5);
      }
   }

   public void warning(String domain, String key, XMLParseException e) {
      try {
         this.getErrorHandler().warning(Util.toSAXParseException(e));
      } catch (SAXException var5) {
         throw new WrappedSAXException(var5);
      }
   }
}
