package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Messages;
import java.util.List;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.http.HTTPException;

final class XMLHandlerProcessor<C extends MessageUpdatableContext> extends HandlerProcessor<C> {
   public XMLHandlerProcessor(HandlerTube owner, WSBinding binding, List<? extends Handler> chain) {
      super(owner, binding, chain);
   }

   final void insertFaultMessage(C context, ProtocolException exception) {
      if (exception instanceof HTTPException) {
         context.put((String)"javax.xml.ws.http.response.code", ((HTTPException)exception).getStatusCode());
      }

      if (context != null) {
         context.setPacketMessage(Messages.createEmpty(this.binding.getSOAPVersion()));
      }

   }
}
