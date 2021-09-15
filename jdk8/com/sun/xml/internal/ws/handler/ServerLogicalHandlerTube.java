package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.MessageContext;

public class ServerLogicalHandlerTube extends HandlerTube {
   private SEIModel seiModel;

   public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
      super(next, port, binding);
      this.seiModel = seiModel;
      this.setUpHandlersOnce();
   }

   public ServerLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
      super(next, cousinTube, binding);
      this.seiModel = seiModel;
      this.setUpHandlersOnce();
   }

   private ServerLogicalHandlerTube(ServerLogicalHandlerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.seiModel = that.seiModel;
      this.handlers = that.handlers;
   }

   protected void initiateClosing(MessageContext mc) {
      if (this.getBinding().getSOAPVersion() != null) {
         super.initiateClosing(mc);
      } else {
         this.close(mc);
         super.initiateClosing(mc);
      }

   }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
      return new ServerLogicalHandlerTube(this, cloner);
   }

   private void setUpHandlersOnce() {
      this.handlers = new ArrayList();
      List<LogicalHandler> logicalSnapShot = ((BindingImpl)this.getBinding()).getHandlerConfig().getLogicalHandlers();
      if (!logicalSnapShot.isEmpty()) {
         this.handlers.addAll(logicalSnapShot);
      }

   }

   protected void resetProcessor() {
      this.processor = null;
   }

   void setUpProcessor() {
      if (!this.handlers.isEmpty() && this.processor == null) {
         if (this.getBinding().getSOAPVersion() == null) {
            this.processor = new XMLHandlerProcessor(this, this.getBinding(), this.handlers);
         } else {
            this.processor = new SOAPHandlerProcessor(false, this, this.getBinding(), this.handlers);
         }
      }

   }

   MessageUpdatableContext getContext(Packet packet) {
      return new LogicalMessageContextImpl(this.getBinding(), this.getBindingContext(), packet);
   }

   private BindingContext getBindingContext() {
      return this.seiModel != null && this.seiModel instanceof AbstractSEIModelImpl ? ((AbstractSEIModelImpl)this.seiModel).getBindingContext() : null;
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
         Attachment att = new DataHandlerAttachment(cid, (DataHandler)atts.get(cid));
         attSet.add(att);
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
