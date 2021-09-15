package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

public abstract class HandlerTube extends AbstractFilterTubeImpl {
   HandlerTube cousinTube;
   protected List<Handler> handlers;
   HandlerProcessor processor;
   boolean remedyActionTaken = false;
   @Nullable
   protected final WSDLPort port;
   boolean requestProcessingSucessful = false;
   private WSBinding binding;
   private HandlerConfiguration hc;
   private HandlerTube.HandlerTubeExchange exchange;

   public HandlerTube(Tube next, WSDLPort port, WSBinding binding) {
      super(next);
      this.port = port;
      this.binding = binding;
   }

   public HandlerTube(Tube next, HandlerTube cousinTube, WSBinding binding) {
      super(next);
      this.cousinTube = cousinTube;
      this.binding = binding;
      if (cousinTube != null) {
         this.port = cousinTube.port;
      } else {
         this.port = null;
      }

   }

   protected HandlerTube(HandlerTube that, TubeCloner cloner) {
      super(that, cloner);
      if (that.cousinTube != null) {
         this.cousinTube = (HandlerTube)cloner.copy(that.cousinTube);
      }

      this.port = that.port;
      this.binding = that.binding;
   }

   protected WSBinding getBinding() {
      return this.binding;
   }

   public NextAction processRequest(Packet request) {
      this.setupExchange();
      if (this.isHandleFalse()) {
         this.remedyActionTaken = true;
         return this.doInvoke(super.next, request);
      } else {
         this.setUpProcessorInternal();
         MessageUpdatableContext context = this.getContext(request);
         boolean isOneWay = this.checkOneWay(request);

         NextAction var5;
         try {
            if (!this.isHandlerChainEmpty()) {
               boolean handlerResult = this.callHandlersOnRequest(context, isOneWay);
               context.updatePacket();
               if (!isOneWay && !handlerResult) {
                  var5 = this.doReturnWith(request);
                  return var5;
               }
            }

            this.requestProcessingSucessful = true;
            NextAction var11 = this.doInvoke(super.next, request);
            return var11;
         } catch (RuntimeException var9) {
            if (!isOneWay) {
               throw var9;
            }

            if (request.transportBackChannel != null) {
               request.transportBackChannel.close();
            }

            request.setMessage((Message)null);
            var5 = this.doReturnWith(request);
         } finally {
            if (!this.requestProcessingSucessful) {
               this.initiateClosing(context.getMessageContext());
            }

         }

         return var5;
      }
   }

   public NextAction processResponse(Packet response) {
      this.setupExchange();
      MessageUpdatableContext context = this.getContext(response);

      label56: {
         NextAction var3;
         try {
            if (!this.isHandleFalse() && response.getMessage() != null) {
               this.setUpProcessorInternal();
               boolean isFault = this.isHandleFault(response);
               if (!this.isHandlerChainEmpty()) {
                  this.callHandlersOnResponse(context, isFault);
               }
               break label56;
            }

            var3 = this.doReturnWith(response);
         } finally {
            this.initiateClosing(context.getMessageContext());
         }

         return var3;
      }

      context.updatePacket();
      return this.doReturnWith(response);
   }

   public NextAction processException(Throwable t) {
      boolean var9 = false;

      NextAction var2;
      try {
         var9 = true;
         var2 = this.doThrow(t);
         var9 = false;
      } finally {
         if (var9) {
            Packet packet = Fiber.current().getPacket();
            MessageUpdatableContext context = this.getContext(packet);
            this.initiateClosing(context.getMessageContext());
         }
      }

      Packet packet = Fiber.current().getPacket();
      MessageUpdatableContext context = this.getContext(packet);
      this.initiateClosing(context.getMessageContext());
      return var2;
   }

   protected void initiateClosing(MessageContext mc) {
   }

   public final void close(MessageContext msgContext) {
      if (this.requestProcessingSucessful && this.cousinTube != null) {
         this.cousinTube.close(msgContext);
      }

      if (this.processor != null) {
         this.closeHandlers(msgContext);
      }

      this.exchange = null;
      this.requestProcessingSucessful = false;
   }

   abstract void closeHandlers(MessageContext var1);

   protected void closeClientsideHandlers(MessageContext msgContext) {
      if (this.processor != null) {
         if (this.remedyActionTaken) {
            this.processor.closeHandlers(msgContext, this.processor.getIndex(), 0);
            this.processor.setIndex(-1);
            this.remedyActionTaken = false;
         } else {
            this.processor.closeHandlers(msgContext, this.handlers.size() - 1, 0);
         }

      }
   }

   protected void closeServersideHandlers(MessageContext msgContext) {
      if (this.processor != null) {
         if (this.remedyActionTaken) {
            this.processor.closeHandlers(msgContext, this.processor.getIndex(), this.handlers.size() - 1);
            this.processor.setIndex(-1);
            this.remedyActionTaken = false;
         } else {
            this.processor.closeHandlers(msgContext, 0, this.handlers.size() - 1);
         }

      }
   }

   abstract void callHandlersOnResponse(MessageUpdatableContext var1, boolean var2);

   abstract boolean callHandlersOnRequest(MessageUpdatableContext var1, boolean var2);

   private boolean checkOneWay(Packet packet) {
      if (this.port != null) {
         return packet.getMessage().isOneWay(this.port);
      } else {
         return packet.expectReply == null || !packet.expectReply;
      }
   }

   private void setUpProcessorInternal() {
      HandlerConfiguration hc = ((BindingImpl)this.binding).getHandlerConfig();
      if (hc != this.hc) {
         this.resetProcessor();
      }

      this.hc = hc;
      this.setUpProcessor();
   }

   abstract void setUpProcessor();

   protected void resetProcessor() {
      this.handlers = null;
   }

   public final boolean isHandlerChainEmpty() {
      return this.handlers.isEmpty();
   }

   abstract MessageUpdatableContext getContext(Packet var1);

   private boolean isHandleFault(Packet packet) {
      if (this.cousinTube != null) {
         return this.exchange.isHandleFault();
      } else {
         boolean isFault = packet.getMessage().isFault();
         this.exchange.setHandleFault(isFault);
         return isFault;
      }
   }

   final void setHandleFault() {
      this.exchange.setHandleFault(true);
   }

   private boolean isHandleFalse() {
      return this.exchange.isHandleFalse();
   }

   final void setHandleFalse() {
      this.exchange.setHandleFalse();
   }

   private void setupExchange() {
      if (this.exchange == null) {
         this.exchange = new HandlerTube.HandlerTubeExchange();
         if (this.cousinTube != null) {
            this.cousinTube.exchange = this.exchange;
         }
      } else if (this.cousinTube != null) {
         this.cousinTube.exchange = this.exchange;
      }

   }

   static final class HandlerTubeExchange {
      private boolean handleFalse;
      private boolean handleFault;

      boolean isHandleFault() {
         return this.handleFault;
      }

      void setHandleFault(boolean isFault) {
         this.handleFault = isFault;
      }

      public boolean isHandleFalse() {
         return this.handleFalse;
      }

      void setHandleFalse() {
         this.handleFalse = true;
      }
   }
}
