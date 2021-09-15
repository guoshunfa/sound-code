package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Message;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

final class XMLReaderImpl extends XMLFilterImpl {
   private final Message msg;
   private static final ContentHandler DUMMY = new DefaultHandler();
   protected static final InputSource THE_SOURCE = new InputSource();

   XMLReaderImpl(Message msg) {
      this.msg = msg;
   }

   public void parse(String systemId) {
      this.reportError();
   }

   private void reportError() {
      throw new IllegalStateException("This is a special XMLReader implementation that only works with the InputSource given in SAXSource.");
   }

   public void parse(InputSource input) throws SAXException {
      if (input != THE_SOURCE) {
         this.reportError();
      }

      this.msg.writeTo(this, this);
   }

   public ContentHandler getContentHandler() {
      return super.getContentHandler() == DUMMY ? null : super.getContentHandler();
   }

   public void setContentHandler(ContentHandler contentHandler) {
      if (contentHandler == null) {
         contentHandler = DUMMY;
      }

      super.setContentHandler(contentHandler);
   }
}
