package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class StAXConnector {
   protected final XmlVisitor visitor;
   protected final UnmarshallingContext context;
   protected final XmlVisitor.TextPredictor predictor;
   protected final TagName tagName = new StAXConnector.TagNameImpl();

   public abstract void bridge() throws XMLStreamException;

   protected StAXConnector(XmlVisitor visitor) {
      this.visitor = visitor;
      this.context = visitor.getContext();
      this.predictor = visitor.getPredictor();
   }

   protected abstract Location getCurrentLocation();

   protected abstract String getCurrentQName();

   protected final void handleStartDocument(NamespaceContext nsc) throws SAXException {
      this.visitor.startDocument(new LocatorEx() {
         public ValidationEventLocator getLocation() {
            return new ValidationEventLocatorImpl(this);
         }

         public int getColumnNumber() {
            return StAXConnector.this.getCurrentLocation().getColumnNumber();
         }

         public int getLineNumber() {
            return StAXConnector.this.getCurrentLocation().getLineNumber();
         }

         public String getPublicId() {
            return StAXConnector.this.getCurrentLocation().getPublicId();
         }

         public String getSystemId() {
            return StAXConnector.this.getCurrentLocation().getSystemId();
         }
      }, nsc);
   }

   protected final void handleEndDocument() throws SAXException {
      this.visitor.endDocument();
   }

   protected static String fixNull(String s) {
      return s == null ? "" : s;
   }

   protected final String getQName(String prefix, String localName) {
      return prefix != null && prefix.length() != 0 ? prefix + ':' + localName : localName;
   }

   private final class TagNameImpl extends TagName {
      private TagNameImpl() {
      }

      public String getQname() {
         return StAXConnector.this.getCurrentQName();
      }

      // $FF: synthetic method
      TagNameImpl(Object x1) {
         this();
      }
   }
}
