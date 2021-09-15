package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;

public class ClientSOAPHandlerTube extends HandlerTube {
   private Set<String> roles;

   public ClientSOAPHandlerTube(WSBinding binding, WSDLPort port, Tube next) {
      super(next, port, binding);
      if (binding.getSOAPVersion() != null) {
      }

   }

   public ClientSOAPHandlerTube(WSBinding binding, Tube next, HandlerTube cousinTube) {
      super(next, cousinTube, binding);
   }

   private ClientSOAPHandlerTube(ClientSOAPHandlerTube that, TubeCloner cloner) {
      super(that, cloner);
   }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
      return new ClientSOAPHandlerTube(this, cloner);
   }

   void setUpProcessor() {
      if (this.handlers == null) {
         this.handlers = new ArrayList();
         HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
         List<SOAPHandler> soapSnapShot = handlerConfig.getSoapHandlers();
         if (!soapSnapShot.isEmpty()) {
            this.handlers.addAll(soapSnapShot);
            this.roles = new HashSet();
            this.roles.addAll(handlerConfig.getRoles());
            this.processor = new SOAPHandlerProcessor(true, this, this.getBinding(), this.handlers);
         }
      }

   }

   MessageUpdatableContext getContext(Packet packet) {
      SOAPMessageContextImpl context = new SOAPMessageContextImpl(this.getBinding(), packet, this.roles);
      return context;
   }

   boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {
      Map<String, DataHandler> atts = (Map)context.get("javax.xml.ws.binding.attachments.outbound");
      AttachmentSet attSet = context.packet.getMessage().getAttachments();
      Iterator var6 = atts.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry<String, DataHandler> entry = (Map.Entry)var6.next();
         String cid = (String)entry.getKey();
         if (attSet.get(cid) == null) {
            Attachment att = new DataHandlerAttachment(cid, (DataHandler)atts.get(cid));
            attSet.add(att);
         }
      }

      boolean handlerResult;
      try {
         handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, context, !isOneWay);
      } catch (WebServiceException var10) {
         this.remedyActionTaken = true;
         throw var10;
      } catch (RuntimeException var11) {
         this.remedyActionTaken = true;
         throw new WebServiceException(var11);
      }

      if (!handlerResult) {
         this.remedyActionTaken = true;
      }

      return handlerResult;
   }

   void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
      try {
         this.processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, context, handleFault);
      } catch (WebServiceException var4) {
         throw var4;
      } catch (RuntimeException var5) {
         throw new WebServiceException(var5);
      }
   }

   void closeHandlers(MessageContext mc) {
      this.closeClientsideHandlers(mc);
   }
}
