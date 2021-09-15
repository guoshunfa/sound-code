package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactory1_1Impl extends MessageFactoryImpl {
   public SOAPMessage createMessage() throws SOAPException {
      return new Message1_1Impl();
   }

   public SOAPMessage createMessage(boolean isFastInfoset, boolean acceptFastInfoset) throws SOAPException {
      return new Message1_1Impl(isFastInfoset, acceptFastInfoset);
   }

   public SOAPMessage createMessage(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
      if (headers == null) {
         headers = new MimeHeaders();
      }

      if (getContentType(headers) == null) {
         headers.setHeader("Content-Type", "text/xml");
      }

      MessageImpl msg = new Message1_1Impl(headers, in);
      msg.setLazyAttachments(this.lazyAttachments);
      return msg;
   }
}
