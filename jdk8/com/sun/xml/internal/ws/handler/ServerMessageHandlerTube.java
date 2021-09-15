package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.handler.MessageHandler;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
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

public class ServerMessageHandlerTube extends HandlerTube {
   private SEIModel seiModel;
   private Set<String> roles;

   public ServerMessageHandlerTube(SEIModel seiModel, WSBinding binding, Tube next, HandlerTube cousinTube) {
      super(next, cousinTube, binding);
      this.seiModel = seiModel;
      this.setUpHandlersOnce();
   }

   private ServerMessageHandlerTube(ServerMessageHandlerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.seiModel = that.seiModel;
      this.handlers = that.handlers;
      this.roles = that.roles;
   }

   private void setUpHandlersOnce() {
      this.handlers = new ArrayList();
      HandlerConfiguration handlerConfig = ((BindingImpl)this.getBinding()).getHandlerConfig();
      List<MessageHandler> msgHandlersSnapShot = handlerConfig.getMessageHandlers();
      if (!msgHandlersSnapShot.isEmpty()) {
         this.handlers.addAll(msgHandlersSnapShot);
         this.roles = new HashSet();
         this.roles.addAll(handlerConfig.getRoles());
      }

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

   protected void resetProcessor() {
      this.processor = null;
   }

   void setUpProcessor() {
      if (!this.handlers.isEmpty() && this.processor == null) {
         this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
      }

   }

   void closeHandlers(MessageContext mc) {
      this.closeServersideHandlers(mc);
   }

   MessageUpdatableContext getContext(Packet packet) {
      MessageHandlerContextImpl context = new MessageHandlerContextImpl(this.seiModel, this.getBinding(), this.port, packet, this.roles);
      return context;
   }

   protected void initiateClosing(MessageContext mc) {
      this.close(mc);
      super.initiateClosing(mc);
   }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
      return new ServerMessageHandlerTube(this, cloner);
   }
}
