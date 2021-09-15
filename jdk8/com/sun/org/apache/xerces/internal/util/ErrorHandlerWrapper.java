package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerWrapper implements XMLErrorHandler {
   protected ErrorHandler fErrorHandler;

   public ErrorHandlerWrapper() {
   }

   public ErrorHandlerWrapper(ErrorHandler errorHandler) {
      this.setErrorHandler(errorHandler);
   }

   public void setErrorHandler(ErrorHandler errorHandler) {
      this.fErrorHandler = errorHandler;
   }

   public ErrorHandler getErrorHandler() {
      return this.fErrorHandler;
   }

   public void warning(String domain, String key, XMLParseException exception) throws XNIException {
      if (this.fErrorHandler != null) {
         SAXParseException saxException = createSAXParseException(exception);

         try {
            this.fErrorHandler.warning(saxException);
         } catch (SAXParseException var6) {
            throw createXMLParseException(var6);
         } catch (SAXException var7) {
            throw createXNIException(var7);
         }
      }

   }

   public void error(String domain, String key, XMLParseException exception) throws XNIException {
      if (this.fErrorHandler != null) {
         SAXParseException saxException = createSAXParseException(exception);

         try {
            this.fErrorHandler.error(saxException);
         } catch (SAXParseException var6) {
            throw createXMLParseException(var6);
         } catch (SAXException var7) {
            throw createXNIException(var7);
         }
      }

   }

   public void fatalError(String domain, String key, XMLParseException exception) throws XNIException {
      if (this.fErrorHandler != null) {
         SAXParseException saxException = createSAXParseException(exception);

         try {
            this.fErrorHandler.fatalError(saxException);
         } catch (SAXParseException var6) {
            throw createXMLParseException(var6);
         } catch (SAXException var7) {
            throw createXNIException(var7);
         }
      }

   }

   protected static SAXParseException createSAXParseException(XMLParseException exception) {
      return new SAXParseException(exception.getMessage(), exception.getPublicId(), exception.getExpandedSystemId(), exception.getLineNumber(), exception.getColumnNumber(), exception.getException());
   }

   protected static XMLParseException createXMLParseException(SAXParseException exception) {
      final String fPublicId = exception.getPublicId();
      final String fExpandedSystemId = exception.getSystemId();
      final int fLineNumber = exception.getLineNumber();
      final int fColumnNumber = exception.getColumnNumber();
      XMLLocator location = new XMLLocator() {
         public String getPublicId() {
            return fPublicId;
         }

         public String getExpandedSystemId() {
            return fExpandedSystemId;
         }

         public String getBaseSystemId() {
            return null;
         }

         public String getLiteralSystemId() {
            return null;
         }

         public int getColumnNumber() {
            return fColumnNumber;
         }

         public int getLineNumber() {
            return fLineNumber;
         }

         public int getCharacterOffset() {
            return -1;
         }

         public String getEncoding() {
            return null;
         }

         public String getXMLVersion() {
            return null;
         }
      };
      return new XMLParseException(location, exception.getMessage(), exception);
   }

   protected static XNIException createXNIException(SAXException exception) {
      return new XNIException(exception.getMessage(), exception);
   }
}
