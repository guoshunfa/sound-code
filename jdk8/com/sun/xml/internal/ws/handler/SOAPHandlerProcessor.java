package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Messages;
import java.util.List;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;

final class SOAPHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {
   public SOAPHandlerProcessor(boolean isClient, HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
      super(owner, binding, chain);
      this.isClient = isClient;
   }

   final void insertFaultMessage(C context, ProtocolException exception) {
      try {
         if (!context.getPacketMessage().isFault()) {
            Message faultMessage = Messages.create(this.binding.getSOAPVersion(), exception, this.determineFaultCode(this.binding.getSOAPVersion()));
            context.setPacketMessage(faultMessage);
         }

      } catch (Exception var4) {
         logger.log(Level.SEVERE, (String)"exception while creating fault message in handler chain", (Throwable)var4);
         throw new RuntimeException(var4);
      }
   }

   private QName determineFaultCode(SOAPVersion soapVersion) {
      return this.isClient ? soapVersion.faultCodeClient : soapVersion.faultCodeServer;
   }
}
