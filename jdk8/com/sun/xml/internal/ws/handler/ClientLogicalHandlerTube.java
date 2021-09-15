package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.MessageContext;

public class ClientLogicalHandlerTube extends HandlerTube {
   private SEIModel seiModel;

   public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, WSDLPort port, Tube next) {
      super(next, port, binding);
      this.seiModel = seiModel;
   }

   public ClientLogicalHandlerTube(WSBinding binding, SEIModel seiModel, Tube next, HandlerTube cousinTube) {
      super(next, cousinTube, binding);
      this.seiModel = seiModel;
   }

   private ClientLogicalHandlerTube(ClientLogicalHandlerTube that, TubeCloner cloner) {
      super(that, cloner);
      this.seiModel = that.seiModel;
   }

   protected void initiateClosing(MessageContext mc) {
      this.close(mc);
      super.initiateClosing(mc);
   }

   public AbstractFilterTubeImpl copy(TubeCloner cloner) {
      return new ClientLogicalHandlerTube(this, cloner);
   }

   void setUpProcessor() {
      if (this.handlers == null) {
         this.handlers = new ArrayList();
         WSBinding binding = this.getBinding();
         List<LogicalHandler> logicalSnapShot = ((BindingImpl)binding).getHandlerConfig().getLogicalHandlers();
         if (!logicalSnapShot.isEmpty()) {
            this.handlers.addAll(logicalSnapShot);
            if (binding.getSOAPVersion() == null) {
               this.processor = new XMLHandlerProcessor(this, binding, this.handlers);
            } else {
               this.processor = new SOAPHandlerProcessor(true, this, binding, this.handlers);
            }
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
         handlerResult = this.processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, context, !isOneWay);
      } catch (WebServiceException var5) {
         this.remedyActionTaken = true;
         throw var5;
      } catch (RuntimeException var6) {
         this.remedyActionTaken = true;
         throw new WebServiceException(var6);
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
