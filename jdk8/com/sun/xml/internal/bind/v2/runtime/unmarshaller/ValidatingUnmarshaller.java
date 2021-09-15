package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.SAXException;

final class ValidatingUnmarshaller implements XmlVisitor, XmlVisitor.TextPredictor {
   private final XmlVisitor next;
   private final ValidatorHandler validator;
   private NamespaceContext nsContext = null;
   private final XmlVisitor.TextPredictor predictor;
   private char[] buf = new char[256];

   public ValidatingUnmarshaller(Schema schema, XmlVisitor next) {
      this.validator = schema.newValidatorHandler();
      this.next = next;
      this.predictor = next.getPredictor();
      this.validator.setErrorHandler(new FatalAdapter(this.getContext()));
   }

   public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException {
      this.nsContext = nsContext;
      this.validator.setDocumentLocator(locator);
      this.validator.startDocument();
      this.next.startDocument(locator, nsContext);
   }

   public void endDocument() throws SAXException {
      this.nsContext = null;
      this.validator.endDocument();
      this.next.endDocument();
   }

   public void startElement(TagName tagName) throws SAXException {
      if (this.nsContext != null) {
         String tagNamePrefix = tagName.getPrefix().intern();
         if (tagNamePrefix != "") {
            this.validator.startPrefixMapping(tagNamePrefix, this.nsContext.getNamespaceURI(tagNamePrefix));
         }
      }

      this.validator.startElement(tagName.uri, tagName.local, tagName.getQname(), tagName.atts);
      this.next.startElement(tagName);
   }

   public void endElement(TagName tagName) throws SAXException {
      this.validator.endElement(tagName.uri, tagName.local, tagName.getQname());
      this.next.endElement(tagName);
   }

   public void startPrefixMapping(String prefix, String nsUri) throws SAXException {
      this.validator.startPrefixMapping(prefix, nsUri);
      this.next.startPrefixMapping(prefix, nsUri);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      this.validator.endPrefixMapping(prefix);
      this.next.endPrefixMapping(prefix);
   }

   public void text(CharSequence pcdata) throws SAXException {
      int len = pcdata.length();
      if (this.buf.length < len) {
         this.buf = new char[len];
      }

      for(int i = 0; i < len; ++i) {
         this.buf[i] = pcdata.charAt(i);
      }

      this.validator.characters(this.buf, 0, len);
      if (this.predictor.expectText()) {
         this.next.text(pcdata);
      }

   }

   public UnmarshallingContext getContext() {
      return this.next.getContext();
   }

   public XmlVisitor.TextPredictor getPredictor() {
      return this;
   }

   /** @deprecated */
   @Deprecated
   public boolean expectText() {
      return true;
   }
}
