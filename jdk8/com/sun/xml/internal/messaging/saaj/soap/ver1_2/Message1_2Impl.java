package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class Message1_2Impl extends MessageImpl implements SOAPConstants {
   public Message1_2Impl() {
   }

   public Message1_2Impl(SOAPMessage msg) {
      super(msg);
   }

   public Message1_2Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
      super(isFastInfoset, acceptFastInfoset);
   }

   public Message1_2Impl(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
      super(headers, in);
   }

   public Message1_2Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in) throws SOAPExceptionImpl {
      super(headers, ct, stat, in);
   }

   public SOAPPart getSOAPPart() {
      if (this.soapPartImpl == null) {
         this.soapPartImpl = new SOAPPart1_2Impl(this);
      }

      return this.soapPartImpl;
   }

   protected boolean isCorrectSoapVersion(int contentTypeId) {
      return (contentTypeId & 8) != 0;
   }

   protected String getExpectedContentType() {
      return this.isFastInfoset ? "application/soap+fastinfoset" : "application/soap+xml";
   }

   protected String getExpectedAcceptHeader() {
      String accept = "application/soap+xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
      return this.acceptFastInfoset ? "application/soap+fastinfoset, " + accept : accept;
   }
}
