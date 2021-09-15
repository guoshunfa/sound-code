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

public class ServerSOAPHandlerTube extends HandlerTube {
   private Set<String> roles;

   public ServerSOAPHandlerTube(WSBinding binding, WSDLPort port, Tube next) {
      super(next, port, binding);
      if (binding.getSOAPVersion() != null) {
      }

      this.setUpHandlersOnce();
   }

   public ServerSOAPHandlerTube(WSBinding binding, Tube next, HandlerTube cousinTube) {
      super(next, cousinTube, binding);
      this.setUpHandlersOnce();
   }

   private ServerSOAPHandlerTube(ServerSOAPHandlerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.handlers = that.handlers;
      this.roles = that.roles;
   }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
      return new ServerSOAPHandlerTube(this, cloner);
   }

   private void setUpHandlersOnce() {
      this.handlers = new ArrayList();
      HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
      List<SOAPHandler> soapSnapShot = handlerConfig.getSoapHandlers();
      if (!soapSnapShot.isEmpty()) {
         this.handlers.addAll(soapSnapShot);
         this.roles = new HashSet();
         this.roles.addAll(handlerConfig.getRoles());
      }

   }

   protected void resetProcessor() {
      this.processor = null;
   }

   void setUpProcessor() {
      if (!this.handlers.isEmpty() && this.processor == null) {
         this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
      }

   }

   MessageUpdatableContext getContext(Packet packet) {
      SOAPMessageContextImpl context = new SOAPMessageContextImpl(this.getBinding(), packet, this.roles);
      return context;
   }

   boolean callHandlersOnRequest(MessageUpdatableContext context, boolean isOneWay) {
      boolean handlerResult;
      try {
         handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, context, !isOneWay);
      } catch (RuntimeException var5) {
         this.remedyActionTaken = true;
         throw var5;
      }

      if (!handlerResult) {
         this.remedyActionTaken = true;
      }

      return handlerResult;
   }

   void callHandlersOnResponse(MessageUpdatableContext context, boolean handleFault) {
      Map<String, DataHandler> atts = (Map)context.get("javax.xml.ws.binding.attachments.outbound");
      AttachmentSet attSet = context.packet.getMessage().getAttachments();
      Iterator var5 = atts.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry<String, DataHandler> entry = (Map.Entry)var5.next();
         String cid = (String)entry.getKey();
         if (attSet.get(cid) == null) {
            Attachment att = new DataHandlerAttachment(cid, (DataHandler)atts.get(cid));
            attSet.add(att);
         }
      }

      try {
         this.processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, context, handleFault);
      } catch (WebServiceException var9) {
         throw var9;
      } catch (RuntimeException var10) {
         throw var10;
      }
   }

   void closeHandlers(MessageContext mc) {
      this.closeServersideHandlers(mc);
   }
}
