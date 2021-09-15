package com.sun.xml.internal.ws.server.provider;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;

final class MessageProviderArgumentBuilder extends ProviderArgumentsBuilder<Message> {
   private final SOAPVersion soapVersion;

   public MessageProviderArgumentBuilder(SOAPVersion soapVersion) {
      this.soapVersion = soapVersion;
   }

   public Message getParameter(Packet packet) {
      return packet.getMessage();
   }

   protected Message getResponseMessage(Message returnValue) {
      return returnValue;
   }

   protected Message getResponseMessage(Exception e) {
      return SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, (CheckedExceptionImpl)null, (Throwable)e);
   }
}
