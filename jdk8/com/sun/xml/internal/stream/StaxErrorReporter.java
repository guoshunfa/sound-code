package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

public class StaxErrorReporter extends XMLErrorReporter {
   protected XMLReporter fXMLReporter = null;

   public StaxErrorReporter(PropertyManager propertyManager) {
      this.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
      this.reset(propertyManager);
   }

   public StaxErrorReporter() {
      this.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
   }

   public void reset(PropertyManager propertyManager) {
      this.fXMLReporter = (XMLReporter)propertyManager.getProperty("javax.xml.stream.reporter");
   }

   public String reportError(XMLLocator location, String domain, String key, Object[] arguments, short severity) throws XNIException {
      MessageFormatter messageFormatter = this.getMessageFormatter(domain);
      String message;
      if (messageFormatter != null) {
         message = messageFormatter.formatMessage(this.fLocale, key, arguments);
      } else {
         StringBuffer str = new StringBuffer();
         str.append(domain);
         str.append('#');
         str.append(key);
         int argCount = arguments != null ? arguments.length : 0;
         if (argCount > 0) {
            str.append('?');

            for(int i = 0; i < argCount; ++i) {
               str.append(arguments[i]);
               if (i < argCount - 1) {
                  str.append('&');
               }
            }
         }

         message = str.toString();
      }

      switch(severity) {
      case 0:
         try {
            if (this.fXMLReporter != null) {
               this.fXMLReporter.report(message, "WARNING", (Object)null, this.convertToStaxLocation(location));
            }
            break;
         } catch (XMLStreamException var12) {
            throw new XNIException(var12);
         }
      case 1:
         try {
            if (this.fXMLReporter != null) {
               this.fXMLReporter.report(message, "ERROR", (Object)null, this.convertToStaxLocation(location));
            }
            break;
         } catch (XMLStreamException var11) {
            throw new XNIException(var11);
         }
      case 2:
         if (!this.fContinueAfterFatalError) {
            throw new XNIException(message);
         }
      }

      return message;
   }

   Location convertToStaxLocation(final XMLLocator location) {
      return new Location() {
         public int getColumnNumber() {
            return location.getColumnNumber();
         }

         public int getLineNumber() {
            return location.getLineNumber();
         }

         public String getPublicId() {
            return location.getPublicId();
         }

         public String getSystemId() {
            return location.getLiteralSystemId();
         }

         public int getCharacterOffset() {
            return location.getCharacterOffset();
         }

         public String getLocationURI() {
            return "";
         }
      };
   }
}
