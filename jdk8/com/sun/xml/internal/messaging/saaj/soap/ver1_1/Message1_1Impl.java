package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class Message1_1Impl extends MessageImpl implements SOAPConstants {
   protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_1", "com.sun.xml.internal.messaging.saaj.soap.ver1_1.LocalStrings");

   public Message1_1Impl() {
   }

   public Message1_1Impl(boolean isFastInfoset, boolean acceptFastInfoset) {
      super(isFastInfoset, acceptFastInfoset);
   }

   public Message1_1Impl(SOAPMessage msg) {
      super(msg);
   }

   public Message1_1Impl(MimeHeaders headers, InputStream in) throws IOException, SOAPExceptionImpl {
      super(headers, in);
   }

   public Message1_1Impl(MimeHeaders headers, ContentType ct, int stat, InputStream in) throws SOAPExceptionImpl {
      super(headers, ct, stat, in);
   }

   public SOAPPart getSOAPPart() {
      if (this.soapPartImpl == null) {
         this.soapPartImpl = new SOAPPart1_1Impl(this);
      }

      return this.soapPartImpl;
   }

   protected boolean isCorrectSoapVersion(int contentTypeId) {
      return (contentTypeId & 4) != 0;
   }

   public String getAction() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"Action"}));
      throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
   }

   public void setAction(String type) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"Action"}));
      throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
   }

   public String getCharset() {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"Charset"}));
      throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
   }

   public void setCharset(String charset) {
      log.log(Level.SEVERE, (String)"SAAJ0303.ver1_1.msg.op.unsupported.in.SOAP1.1", (Object[])(new String[]{"Charset"}));
      throw new UnsupportedOperationException("Operation not supported by SOAP 1.1");
   }

   protected String getExpectedContentType() {
      return this.isFastInfoset ? "application/fastinfoset" : "text/xml";
   }

   protected String getExpectedAcceptHeader() {
      String accept = "text/xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
      return this.acceptFastInfoset ? "application/fastinfoset, " + accept : accept;
   }
}
