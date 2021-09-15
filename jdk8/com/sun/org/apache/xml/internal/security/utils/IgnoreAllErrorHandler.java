package com.sun.org.apache.xml.internal.security.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class IgnoreAllErrorHandler implements ErrorHandler {
   private static Logger log = Logger.getLogger(IgnoreAllErrorHandler.class.getName());
   private static final boolean warnOnExceptions = System.getProperty("com.sun.org.apache.xml.internal.security.test.warn.on.exceptions", "false").equals("true");
   private static final boolean throwExceptions = System.getProperty("com.sun.org.apache.xml.internal.security.test.throw.exceptions", "false").equals("true");

   public void warning(SAXParseException var1) throws SAXException {
      if (warnOnExceptions) {
         log.log(Level.WARNING, (String)"", (Throwable)var1);
      }

      if (throwExceptions) {
         throw var1;
      }
   }

   public void error(SAXParseException var1) throws SAXException {
      if (warnOnExceptions) {
         log.log(Level.SEVERE, (String)"", (Throwable)var1);
      }

      if (throwExceptions) {
         throw var1;
      }
   }

   public void fatalError(SAXParseException var1) throws SAXException {
      if (warnOnExceptions) {
         log.log(Level.WARNING, (String)"", (Throwable)var1);
      }

      if (throwExceptions) {
         throw var1;
      }
   }
}
