package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class MessageFactoryImpl extends MessageFactory {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
   protected OutputStream listener;
   protected boolean lazyAttachments = false;

   public OutputStream listen(OutputStream newListener) {
      OutputStream oldListener = this.listener;
      this.listener = newListener;
      return oldListener;
   }

   public SOAPMessage createMessage() throws SOAPException {
      throw new UnsupportedOperationException();
   }

   public SOAPMessage createMessage(boolean isFastInfoset, boolean acceptFastInfoset) throws SOAPException {
      throw new UnsupportedOperationException();
   }

   public SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws SOAPException, IOException {
      String contentTypeString = MessageImpl.getContentType(headers);
      if (this.listener != null) {
         in = new TeeInputStream((InputStream)in, this.listener);
      }

      try {
         ContentType contentType = new ContentType(contentTypeString);
         int stat = MessageImpl.identifyContentType(contentType);
         if (MessageImpl.isSoap1_1Content(stat)) {
            return new Message1_1Impl(headers, contentType, stat, (InputStream)in);
         } else if (MessageImpl.isSoap1_2Content(stat)) {
            return new Message1_2Impl(headers, contentType, stat, (InputStream)in);
         } else {
            log.severe("SAAJ0530.soap.unknown.Content-Type");
            throw new SOAPExceptionImpl("Unrecognized Content-Type");
         }
      } catch (ParseException var6) {
         log.severe("SAAJ0531.soap.cannot.parse.Content-Type");
         throw new SOAPExceptionImpl("Unable to parse content type: " + var6.getMessage());
      }
   }

   protected static final String getContentType(MimeHeaders headers) {
      String[] values = headers.getHeader("Content-Type");
      return values == null ? null : values[0];
   }

   public void setLazyAttachmentOptimization(boolean flag) {
      this.lazyAttachments = flag;
   }
}
