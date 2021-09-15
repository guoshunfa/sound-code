package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class Util {
   public static final XMLInputSource toXMLInputSource(StreamSource in) {
      if (in.getReader() != null) {
         return new XMLInputSource(in.getPublicId(), in.getSystemId(), in.getSystemId(), in.getReader(), (String)null);
      } else {
         return in.getInputStream() != null ? new XMLInputSource(in.getPublicId(), in.getSystemId(), in.getSystemId(), in.getInputStream(), (String)null) : new XMLInputSource(in.getPublicId(), in.getSystemId(), in.getSystemId());
      }
   }

   public static SAXException toSAXException(XNIException e) {
      if (e instanceof XMLParseException) {
         return toSAXParseException((XMLParseException)e);
      } else {
         return e.getException() instanceof SAXException ? (SAXException)e.getException() : new SAXException(e.getMessage(), e.getException());
      }
   }

   public static SAXParseException toSAXParseException(XMLParseException e) {
      return e.getException() instanceof SAXParseException ? (SAXParseException)e.getException() : new SAXParseException(e.getMessage(), e.getPublicId(), e.getExpandedSystemId(), e.getLineNumber(), e.getColumnNumber(), e.getException());
   }
}
