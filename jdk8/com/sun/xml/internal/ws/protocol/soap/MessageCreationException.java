package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.namespace.QName;

public class MessageCreationException extends ExceptionHasMessage {
   private final SOAPVersion soapVersion;

   public MessageCreationException(SOAPVersion soapVersion, Object... args) {
      super("soap.msg.create.err", args);
      this.soapVersion = soapVersion;
   }

   public String getDefaultResourceBundleName() {
      return "com.sun.xml.internal.ws.resources.soap";
   }

   public Message getFaultMessage() {
      QName faultCode = this.soapVersion.faultCodeClient;
      return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, this.getLocalizedMessage(), faultCode);
   }
}
