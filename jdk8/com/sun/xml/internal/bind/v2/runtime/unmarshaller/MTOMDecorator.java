package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.activation.DataHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.SAXException;

final class MTOMDecorator implements XmlVisitor {
   private final XmlVisitor next;
   private final AttachmentUnmarshaller au;
   private UnmarshallerImpl parent;
   private final Base64Data base64data = new Base64Data();
   private boolean inXopInclude;
   private boolean followXop;

   public MTOMDecorator(UnmarshallerImpl parent, XmlVisitor next, AttachmentUnmarshaller au) {
      this.parent = parent;
      this.next = next;
      this.au = au;
   }

   public void startDocument(LocatorEx loc, NamespaceContext nsContext) throws SAXException {
      this.next.startDocument(loc, nsContext);
   }

   public void endDocument() throws SAXException {
      this.next.endDocument();
   }

   public void startElement(TagName tagName) throws SAXException {
      if (tagName.local.equals("Include") && tagName.uri.equals("http://www.w3.org/2004/08/xop/include")) {
         String href = tagName.atts.getValue("href");
         DataHandler attachment = this.au.getAttachmentAsDataHandler(href);
         if (attachment == null) {
            this.parent.getEventHandler().handleEvent((ValidationEvent)null);
         }

         this.base64data.set(attachment);
         this.next.text(this.base64data);
         this.inXopInclude = true;
         this.followXop = true;
      } else {
         this.next.startElement(tagName);
      }

   }

   public void endElement(TagName tagName) throws SAXException {
      if (this.inXopInclude) {
         this.inXopInclude = false;
         this.followXop = true;
      } else {
         this.next.endElement(tagName);
      }
   }

   public void startPrefixMapping(String prefix, String nsUri) throws SAXException {
      this.next.startPrefixMapping(prefix, nsUri);
   }

   public void endPrefixMapping(String prefix) throws SAXException {
      this.next.endPrefixMapping(prefix);
   }

   public void text(CharSequence pcdata) throws SAXException {
      if (!this.followXop) {
         this.next.text(pcdata);
      } else {
         this.followXop = false;
      }

   }

   public UnmarshallingContext getContext() {
      return this.next.getContext();
   }

   public XmlVisitor.TextPredictor getPredictor() {
      return this.next.getPredictor();
   }
}
